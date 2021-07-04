package net.blancworks.multis.loader;

import net.blancworks.multis.resources.MultisByteArrayResource;
import net.blancworks.multis.resources.MultisResource;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Root Packs are basically a way to hold all the data related to a specific multis pack.
 */
public abstract class MultisRootPack extends MultisResource {


    /**
     * Holds all the scripts loaded in the pack.
     */
    public MultisPack scriptPack = new MultisPack();

    /**
     * Holds all the texture objects in the pack.
     */
    public MultisPack texturePack = new MultisPack();

    /**
     * Holds all the model objects in the pack.
     */
    public MultisPack modelPack = new MultisPack();

    /**
     * Holds all the json objects in the pack.
     */
    public MultisPack jsonPack = new MultisPack();


    /**
     * The future used to asynchronously load assets.
     */
    private CompletableFuture registrationFuture = CompletableFuture.completedFuture(null);


    /**
     * Registers a script from an InputStream.
     *
     * @param id     The ID to put the script into.
     * @param stream The stream of the script.
     */
    public abstract void registerScript(Identifier id, InputStream stream);

    /**
     * Registers a texture from an InputStream.
     *
     * @param id     The ID to put the texture into.
     * @param stream The stream of the texture.
     */
    public void registerTexture(Identifier id, InputStream stream) {
        try {
            byte[] targetArray = new byte[stream.available()];
            MultisByteArrayResource resource = new MultisByteArrayResource(targetArray);

            texturePack.objects.put(id, resource);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Registers a model from an InputStream.
     *
     * @param id     The ID to put the model into.
     * @param stream The model of the script.
     */
    public void registerModel(Identifier id, InputStream stream) {

    }

    /**
     * Registers a json from an InputStream.
     *
     * @param id     The ID to put the json into.
     * @param stream The stream of the json.
     */
    public void registerJson(Identifier id, InputStream stream) {

    }


    /**
     * Unregisters a script.
     */
    public abstract void unregisterScript(Identifier id);

    /**
     * Unregisters a texture.
     */
    public void unregisterTexture(Identifier id) {
        texturePack.objects.remove(id);
    }

    /**
     * Unregisters a model.
     */
    public void unregisterModel(Identifier id) {

    }

    /**
     * Unregisters a JSON.
     */
    public void unregisterJson(Identifier id) {

    }


    @Override
    public void onLoad() {

    }

    @Override
    public void onUnload() {
        scriptPack.onUnload();
        texturePack.onUnload();
        modelPack.onUnload();
        jsonPack.onUnload();
    }

    private final HashSet<Identifier> scriptsAfterReload = new HashSet<>();
    private final HashSet<Identifier> texturesAfterReload = new HashSet<>();
    private final HashSet<Identifier> modelsAfterReload = new HashSet<>();
    private final HashSet<Identifier> jsonsAfterReload = new HashSet<>();

    public void markReloadStart() {
        scriptsAfterReload.clear();
        texturesAfterReload.clear();
        modelsAfterReload.clear();
        jsonsAfterReload.clear();
    }

    public void markReloadEnd() {
        clearExtras(scriptsAfterReload, scriptPack, this::unregisterScript);
        clearExtras(texturesAfterReload, texturePack, this::unregisterTexture);
        clearExtras(modelsAfterReload, modelPack, this::unregisterModel);
        clearExtras(jsonsAfterReload, jsonPack, this::unregisterJson);
    }

    /**
     * Clear values that aren't inside of set from pack.
     */
    private void clearExtras(Set<Identifier> set, MultisPack pack, Consumer<Identifier> removeAction) {
        //Copy pack to temporary set.
        Set<Map.Entry<Identifier, MultisResource>> objects = new HashSet<>();
        objects.addAll(pack.getEntries());

        //Loop over temporary set.
        for (Map.Entry<Identifier, MultisResource> entry : objects) {
            //If temporary set contains a value, but the reference set does not, remove the value from the pack.
            if (!set.contains(entry.getKey())) {
                removeAction.accept(entry.getKey());
            }
        }
    }
}
