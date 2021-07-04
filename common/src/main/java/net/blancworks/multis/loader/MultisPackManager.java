package net.blancworks.multis.loader;

import com.google.gson.JsonObject;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;

public class MultisPackManager {

    /**
     * List of all currently-loaded packs for Multis.
     */
    private static final Map<String, MultisRootPack> packs = new HashMap<>();

    private static final Map<String, Supplier<MultisRootPack>> loaders = new HashMap<String, Supplier<MultisRootPack>>() {{
        put("lua", MultisLuaRootPack::new);
    }};

    /**
     * Sets a loader for a given ID.
     *
     * @param loaderID The ID of the loader.
     * @param loader   The loader to set.
     */
    public void setLoader(String loaderID, Supplier<MultisRootPack> loader) {
        loaders.put(loaderID, loader);
    }

    //--- PACK MANAGEMENT ---

    /**
     * Registers and loads a pack.
     *
     * @param pack The pack to register.
     */
    public static void registerPack(String id, MultisRootPack pack) {
        packs.put(id, pack);
        pack.onLoad();
    }

    /**
     * Unregisters and unloads a pack.
     *
     * @param id The ID of the pack to unload.
     */
    public static void unregisterPack(String id) {
        MultisRootPack pack = packs.remove(id);
        if (pack != null)
            pack.onUnload();
    }

    public static MultisRootPack getPack(String id){
        return packs.get(id);
    }

    //--- RESOURCE PACK LOADING ---

    private static MultisMetadataReader metaReader = new MultisMetadataReader();

    /**
     * Parses out Multis Packs from the resource manager that Minecraft provides on asset reload.
     *
     * @param manager Minecraft's ResourceManager.
     */
    public static void loadResourcePacksIntoMultisPacks(ResourceManager manager) {

        //Sorted list of pack metas by priority.
        SortedSet<MultisPackMeta> packMetas = new TreeSet<>(Comparator.comparingInt(o -> o.priority));

        //Foreach resource pack
        manager.streamResourcePacks().forEach(resourcePack -> {
            try {
                //Attempt to parse the meta for this pack.
                MultisPackMeta meta = resourcePack.parseMetadata(metaReader);

                //Return with no meta if the pack doesn't have any (or the correct) multis data.
                if (meta == null)
                    return;

                meta.pack = resourcePack;
                packMetas.add(meta);
            } catch (IOException e) {

            }
        });

        //Mark reload start for packs.
        packs.forEach((id, pack) -> pack.markReloadStart());

        //For each pack meta.
        for (MultisPackMeta packMeta : packMetas) {
            //Try to get a loader for this pack meta
            Supplier<MultisRootPack> loader = loaders.get(packMeta.language);
            if (loader == null) continue;

            //Get all namespaces from the pack.
            List<String> allNamespaces = new ArrayList<>();
            allNamespaces.addAll(packMeta.pack.getNamespaces(ResourceType.SERVER_DATA));
            allNamespaces.addAll(packMeta.pack.getNamespaces(ResourceType.CLIENT_RESOURCES));

            //Loop over each namespaces.
            for (String namespace : allNamespaces) {
                MultisRootPack rootPack = packs.get(namespace);

                if (rootPack == null) {
                    packs.put(namespace, rootPack = loader.get());
                }

                //Register scripts from the datapack.
                Collection<Identifier> scriptIDs = packMeta.pack.findResources(ResourceType.SERVER_DATA, namespace, "scripts", 100, (p) -> {
                    return p.endsWith(".lua");
                });

                for (Identifier scriptID : scriptIDs) {
                    try {
                        InputStream is = packMeta.pack.open(ResourceType.CLIENT_RESOURCES, scriptID);

                        try {
                            rootPack.registerScript(scriptID, is);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        is.close();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }

                //Register textures from the datapack
                Collection<Identifier> textureIDs = packMeta.pack.findResources(ResourceType.CLIENT_RESOURCES, namespace, "textures", 100, (p) -> {
                    return p.endsWith(".png");
                });

                for (Identifier textureID : textureIDs) {
                    try {
                        InputStream is = packMeta.pack.open(ResourceType.CLIENT_RESOURCES, textureID);

                        try {
                            rootPack.registerTexture(textureID, is);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        is.close();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }

        //Mark reload end for packs.
        packs.forEach((id, pack) -> pack.markReloadEnd());
    }

    /**
     * Reads a MulisPackMeta from a Json Object
     */
    private static class MultisMetadataReader implements ResourceMetadataReader<MultisPackMeta> {

        @Override
        public String getKey() {
            return "multis";
        }

        @Override
        public MultisPackMeta fromJson(JsonObject json) {
            MultisPackMeta meta = new MultisPackMeta();

            meta.language = json.get("language").getAsString();
            meta.minApiVersion = String.valueOf(json.get("api").getAsInt());
            meta.priority = json.get("priority").getAsInt();

            return meta;
        }
    }

    /**
     * Stores Multis info from datapacks.
     */
    private static class MultisPackMeta {
        public String language;
        public String minApiVersion;
        public int priority;

        public ResourcePack pack;
    }
}
