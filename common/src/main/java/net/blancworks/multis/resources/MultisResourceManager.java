package net.blancworks.multis.resources;

import me.shedaniel.architectury.event.events.client.ClientPlayerEvent;
import net.blancworks.multis.objects.item.MultisItemManager;
import net.blancworks.multis.rendering.MultisItemModel;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The collection of all resources for Multis.
 */
public class MultisResourceManager {
    private static final Map<Identifier, MultisResource> resources = new HashMap<>();

    private static final Map<Identifier, List<Consumer<MultisResource>>> changeListeners = new HashMap<>();

    private static final Map<String, Supplier<MultisResource>> resourceFactories = new HashMap<String, Supplier<MultisResource>>() {{
        put("string", MultisStringResource::new);
        put("binary", MultisBinaryResource::new);
    }};


    public static void client_init() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register((p) -> {
            if (p != null) {
                resources.clear();
                MultisItemManager.clear();
            }
        });
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

        for (Consumer<MultisResource> consumer : listenerList) {
            consumer.accept(newResource);
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

        Supplier<MultisResource> factory = resourceFactories.get(type);

        if (factory == null) {
            //TODO add error
            return null;
        }

        MultisResource rsc = factory.get();
        rsc.readFromPacket(packet);

        setResource(id, rsc);

        return rsc;
    }

    /**
     * Reads a resource from the InputStream.
     *
     * @param type The type of the resource.
     * @param id   The ID of the resource.
     * @param is   The input stream for the resource.
     * @return The resource that was created.
     */
    public static synchronized MultisResource readResourceFromInputStream(String type, Identifier id, InputStream is) {
        Supplier<MultisResource> factory = resourceFactories.get(type);

        if (factory == null) {
            //TODO add error
            return null;
        }

        MultisResource rsc = factory.get();
        rsc.readFromInputStream(is);

        setResource(id, rsc);

        return rsc;
    }

    /**
     * Writes a resource from the registry into a packet, if any.
     *
     * @param id  The ID of the resource to write.
     * @param buf The packet to write into.
     */
    public static synchronized void writeResourceToPacket(Identifier id, PacketByteBuf buf) {
        MultisResource rsc = resources.get(id);

        if (rsc == null)
            return;

        buf.writeIdentifier(id);
        buf.writeString(rsc.getFactoryID());
        rsc.writeToPacket(buf);
    }

}
