package net.blancworks.multis.datapack;

import com.google.gson.JsonObject;
import net.blancworks.multis.access.ReloadableResourceManagerImplAccessor;
import net.blancworks.multis.networking.MultisNetworkManager;
import net.blancworks.multis.rendering.model.MultisModelManager;
import net.blancworks.multis.resources.MultisResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * This class is responsible for loading and file-watching assets from datapacks.
 * This class is assumed to be SERVER-SIDE ONLY, as it handles loading from datapacks, which only happens on the server.
 */
public class MultisDatapackReloadManager {

    private static final MultisPackMetadataReader reader = new MultisPackMetadataReader();

    /**
     * Call this with a resource manager to allow the DatapackManager to evaluate changes to the datapacks.
     *
     * @param manager Resource manager to manage datapacks.
     */
    public static void onDatapackReload(ResourceManager manager) {
        MultisModelManager.clearCache();

        SortedSet<MultisPackMetadata> packMetas = new TreeSet<MultisPackMetadata>(Comparator.comparingInt(k -> k.priority));

        ReloadableResourceManagerImplAccessor accessor = (ReloadableResourceManagerImplAccessor) manager;

        accessor.multis_getPackList().stream().forEach((pack) -> {
            try {
                MultisPackMetadata md = pack.parseMetadata(reader);

                if (md == null) return;

                md.sourcePack = pack;

                packMetas.add(md);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        for (MultisPackMetadata packMeta : packMetas) {
            loadAssetsFromResourcePack(packMeta);
        }
    }

    /**
     * Loads assets into Multis via a resource pack.
     *
     * @param packMetadata The pack to load assets from.
     */
    private static void loadAssetsFromResourcePack(MultisPackMetadata packMetadata) {
        ResourcePack pack = packMetadata.sourcePack;

        //Iterate server data (json and scripts)
        for (String namespace : pack.getNamespaces(ResourceType.SERVER_DATA)) {
            LinkedList<Identifier> changedIDs = new LinkedList<>();

            //Lang files
            Collection<Identifier> langIDs = pack.findResources(ResourceType.CLIENT_RESOURCES, namespace, "multis/lang", 100, p -> p.endsWith(".json"));

            foreachIDToInputStream(langIDs, pack, ResourceType.CLIENT_RESOURCES, (is, id) -> {
                id = new Identifier(id.getNamespace(), id.getPath().replace(".json", ""));
                boolean changed = MultisResourceManager.readResourceFromInputStream("string", id, is);
                if (changed)
                    changedIDs.add(id);
            });

            //Get all textures
            Collection<Identifier> modelIDs = pack.findResources(ResourceType.CLIENT_RESOURCES, namespace, "multis/models", 100, p -> p.endsWith(".json"));

            foreachIDToInputStream(modelIDs, pack, ResourceType.CLIENT_RESOURCES, (is, id) -> {
                id = new Identifier(id.getNamespace(), id.getPath().replace(".json", ""));
                boolean changed = MultisResourceManager.readResourceFromInputStream("string", id, is);
                if (changed)
                    changedIDs.add(id);
            });

            //Get all models
            Collection<Identifier> textureIDs = pack.findResources(ResourceType.CLIENT_RESOURCES, namespace, "multis/textures", 100, p -> p.endsWith(".png"));

            foreachIDToInputStream(textureIDs, pack, ResourceType.CLIENT_RESOURCES, (is, id) -> {
                id = new Identifier(id.getNamespace(), id.getPath().replace(".png", ""));
                boolean changed = MultisResourceManager.readResourceFromInputStream("binary", id, is);

                if (changed)
                    changedIDs.add(id);
            });

            //Get all item scripts
            Collection<Identifier> scriptIDs = pack.findResources(ResourceType.SERVER_DATA, namespace, "multis/scripts/items", 100, p -> p.endsWith(".lua"));

            foreachIDToInputStream(scriptIDs, pack, ResourceType.SERVER_DATA, (is, id) -> {
                id = new Identifier(id.getNamespace(), id.getPath().replace(".lua", ""));
                String[] splitPath = id.getPath().split("/");
                String itemName = splitPath[splitPath.length - 1];

                boolean changed = MultisResourceManager.readResourceFromInputStream("string", id, is);

                if (changed)
                    changedIDs.add(id);
            });

            for (Identifier id : changedIDs) {
                System.out.println("ASSET AT ID " + id + " CHANGED");

                MultisNetworkManager.onAssetUpdate(id);
            }
        }
    }

    private static void foreachIDToInputStream(Collection<Identifier> ids, ResourcePack pack, ResourceType type, BiConsumer<InputStream, Identifier> streamConsumer) {
        for (Identifier resourceID : ids) {
            try (InputStream is = pack.open(type, resourceID)) {
                if (is == null)
                    continue;

                streamConsumer.accept(is, resourceID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class MultisPackMetadataReader implements ResourceMetadataReader<MultisPackMetadata> {

        @Override
        public String getKey() {
            return "multis";
        }

        @Override
        public MultisPackMetadata fromJson(JsonObject json) {
            MultisPackMetadata md = new MultisPackMetadata();
            if (json.has("priority")) md.priority = json.get("priority").getAsInt();
            if (json.has("language")) md.language = MultisPackLanguage.valueOf(json.get("language").getAsString());
            return md;
        }
    }

    private static class MultisPackMetadata {
        public ResourcePack sourcePack;
        public int priority = 0;
        public MultisPackLanguage language = MultisPackLanguage.LUA;
    }

    public enum MultisPackLanguage {
        LUA
    }
}
