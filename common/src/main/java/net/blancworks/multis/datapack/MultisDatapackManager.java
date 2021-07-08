package net.blancworks.multis.datapack;

import com.google.gson.JsonObject;
import net.blancworks.multis.access.ReloadableResourceManagerImplAccessor;
import net.blancworks.multis.resources.MultisResourceManager;
import net.blancworks.multis.resources.MultisStringResource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is responsible for loading and file-watching assets from datapacks.
 * This class is assumed to be SERVER-SIDE ONLY, as it handles loading from datapacks, which only happens on the server.
 */
public class MultisDatapackManager {

    private static final MultisPackMetadataReader reader = new MultisPackMetadataReader();

    /**
     * Call this with a resource manager to allow the DatapackManager to evaluate changes to the datapacks.
     *
     * @param manager Resource manager to manage datapacks.
     */
    public static void onDatapackReload(ResourceManager manager) {

        SortedSet<MultisPackMetadata> packMetas = new TreeSet<MultisPackMetadata>(Comparator.comparingInt(k -> k.priority));

        ReloadableResourceManagerImplAccessor accessor = (ReloadableResourceManagerImplAccessor)manager;

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

            //Get all scripts
            Collection<Identifier> scriptIDs = pack.findResources(ResourceType.SERVER_DATA, namespace, "scripts/items", 100, p -> p.endsWith(".lua"));

            for (Identifier scriptID : scriptIDs) {
                try {
                    InputStream is = pack.open(ResourceType.SERVER_DATA, scriptID);

                    MultisStringResource stringResource = new MultisStringResource();
                    stringResource.readFromInputStream(is);

                    MultisResourceManager.setResource(scriptID, stringResource);

                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
