package net.blancworks.multis.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.architectury.event.events.client.ClientPlayerEvent;
import net.blancworks.multis.lua.LuaEnvironment;
import net.blancworks.multis.objects.item.MultisItemManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Language;

import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The collection of all resources for Multis.
 */
public class MultisResourceManager {
    public static ResourceManager resourceManager;

    private static final Map<Identifier, MultisResource> resources = new HashMap<>();

    private static final Map<Identifier, List<Consumer<MultisResource>>> changeListeners = new HashMap<>();

    private static final List<BiConsumer<Identifier, MultisResource>> globalChangeListeners = new ArrayList<>();

    private static final HashMap<String, MultisLang> multisLanguageMaps = new HashMap<>();

    private static final Map<String, Supplier<MultisResource>> resourceFactories = new HashMap<String, Supplier<MultisResource>>() {{
        put("string", MultisStringResource::new);
        put("binary", MultisBinaryResource::new);
    }};


    public static void client_init() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register((p) -> {
            if (p != null) {
                resources.clear();
                MultisItemManager.clear();
                LuaEnvironment.clear();
                multisLanguageMaps.clear();
            }
        });

        globalChangeListeners.add(MultisResourceManager::onLangReload);
    }

    public static synchronized MultisResource setResource(Identifier id, MultisResource resource) {
        MultisResource ret = resources.put(id, resource);
        notifyListeners(id, resource);
        return ret;
    }

    public static synchronized MultisResource removeResource(Identifier id) {
        MultisResource ret = resources.remove(id);
        notifyListeners(id, null);
        return ret;
    }

    public static synchronized MultisResource getResource(Identifier id) {
        return resources.get(id);
    }


    public static synchronized void addListener(Identifier id, Consumer<MultisResource> listener) {
        List<Consumer<MultisResource>> listenerList = changeListeners.computeIfAbsent(id, k -> new ArrayList<>());

        listenerList.add(listener);
    }

    public static synchronized boolean removeListener(Identifier id, Consumer<MultisResource> listener) {
        List<Consumer<MultisResource>> listenerList = changeListeners.computeIfAbsent(id, k -> new ArrayList<>());

        return listenerList.remove(listener);
    }

    private static synchronized void notifyListeners(Identifier id, MultisResource newResource) {
        List<Consumer<MultisResource>> listenerList = changeListeners.computeIfAbsent(id, k -> new ArrayList<>());

        for (BiConsumer<Identifier, MultisResource> consumer : globalChangeListeners) {
            consumer.accept(id, newResource);
        }

        for (Consumer<MultisResource> consumer : listenerList) {
            consumer.accept(newResource);
        }
    }

    private static void onLangReload(Identifier id, MultisResource<String> resource) {
        if (id.getPath().startsWith("multis/lang")) {
            String[] langID = id.getPath().split("/");
            String last = langID[langID.length - 1];

            JsonObject jobject = JsonHelper.deserialize(resource.getValue());

            MultisLang lang = multisLanguageMaps.computeIfAbsent(last, (i) -> new MultisLang());
            lang.code = last;
            Map<String, String> m = lang.translations;


            for (Map.Entry<String, JsonElement> entry : jobject.entrySet()) {
                m.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
    }

    /**
     * Reads and registers a resource from a packet.
     * <p>
     * If an existing resource exists at the given ID, it is replaced.
     *
     * @param packet The packet to read from.
     * @return The resource that was created.
     */
    public static synchronized MultisResource readResourceFromPacket(PacketByteBuf packet) {
        Identifier id = packet.readIdentifier();
        String type = packet.readString();

        System.out.println("READING ASSET " + id + " OF TYPE " + type + " FROM PACKET");

        Supplier<MultisResource> factory = resourceFactories.get(type);

        if (factory == null) {
            //TODO add error
            return null;
        }

        MultisResource rsc = getResource(id);
        if (rsc == null)
            rsc = factory.get();

        boolean changed = rsc.readFromPacket(packet);

        if (changed)
            setResource(id, rsc);

        return rsc;
    }

    /**
     * Reads a resource from the InputStream.
     *
     * @param type The type of the resource.
     * @param id   The ID of the resource.
     * @param is   The input stream for the resource.
     * @return True if the asset changed, false otherwise.
     */
    public static synchronized boolean readResourceFromInputStream(String type, Identifier id, InputStream is) {
        Supplier<MultisResource> factory = resourceFactories.get(type);

        if (factory == null) {
            //TODO add error
            return false;
        }

        MultisResource rsc = getResource(id);
        if (rsc == null)
            rsc = factory.get();

        boolean changed = rsc.readFromInputStream(is);

        if (changed)
            setResource(id, rsc);

        return changed;
    }

    /**
     * Writes a resource from the registry into a packet, if any.
     *
     * @param id  The ID of the resource to write.
     * @param buf The packet to write into.
     */
    public static synchronized boolean writeResourceToPacket(Identifier id, PacketByteBuf buf) {
        MultisResource rsc = resources.get(id);

        if (rsc == null)
            return false;

        buf.writeIdentifier(id);
        buf.writeString(rsc.getFactoryID());
        rsc.writeToPacket(buf);

        return true;
    }

    public static synchronized void fillAssetQueue(Queue<Identifier> idQueue) {
        for (Map.Entry<Identifier, MultisResource> entry : resources.entrySet()) {
            idQueue.add(entry.getKey());
        }
    }

    public static String getTranslation(String key) {
        String code = MinecraftClient.getInstance().getLanguageManager().getLanguage().getCode();
        MultisLang lang = multisLanguageMaps.get(code);

        if (lang == null) return key;

        String ret = lang.translations.get(key);

        if(ret == null) return key;

        return ret;
    }

    /**
     * Represents a language file for Multis
     * Vanilla reloads the entire resource pack whenever you change your language, we're just going to opt to store the lang files in memory here isntead.
     */
    private static class MultisLang {
        public String code = null;
        public HashMap<String, String> translations = new HashMap<>();
    }
}
