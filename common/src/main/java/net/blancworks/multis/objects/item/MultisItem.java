package net.blancworks.multis.objects.item;

import me.shedaniel.architectury.utils.Env;
import me.shedaniel.architectury.utils.EnvExecutor;
import net.blancworks.multis.lua.LuaEnvironment;
import net.blancworks.multis.rendering.MultisItemModel;
import net.blancworks.multis.resources.MultisBinaryResource;
import net.blancworks.multis.resources.MultisResource;
import net.blancworks.multis.resources.MultisResourceManager;
import net.blancworks.multis.resources.MultisStringResource;
import net.minecraft.util.Identifier;

/**
 * A Multis Item is basically a wrapper for a Minecraft item that redirects calls to a script.
 * They can be dynamically created or destroyed, as with most things in Multis.
 */
public class MultisItem {

    public Identifier id;
    public Identifier modelID;
    public Identifier scriptID;
    public Identifier textureID;


    public MultisStringResource scriptSource;
    public MultisBinaryResource modelResource;

    private MultisLuaInterface luaInterface;

    public MultisItemModel model;

    public void init(Identifier id) {
        this.id = id;

        //Script ID is the same namespace and item name, but the path is slightly modified.
        scriptID = new Identifier(id.getNamespace(), "multis/scripts/items/" + id.getPath());

        //Register event for script reloads in the future.
        MultisResourceManager.addListener(scriptID, this::onScriptReload);
        //'reload' item with initial script.
        onScriptReload(MultisResourceManager.getResource(scriptID));

        textureID = new Identifier(id.getNamespace(), "multis/textures/items/" + id.getPath());

        //Generate ID for model.
        modelID = new Identifier(id.getNamespace(), "multis/models/items/" + id.getPath());

        //Listen for model changes.
        MultisResourceManager.addListener(modelID, this::onModelReload);
        //Initial model "reload"
        onModelReload(MultisResourceManager.getResource(modelID));
    }

    public void onScriptReload(MultisResource<String> source) {
        scriptSource = (MultisStringResource) source;

        //Load script if source string is provided.
        if (scriptSource != null && scriptSource.getValue() != null) {
            luaInterface = LuaEnvironment.loadMultisObject(source.getValue(), id, MultisLuaInterface.class);
        }
    }

    public void onModelReload(MultisResource<byte[]> source) {
        modelResource = (MultisBinaryResource) source;

        //Load model if source binary is provided.
        if (modelResource != null && modelResource.getValue() != null) {

        } else {
            //Build default model if none is available.
            EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
                model = new MultisItemModel(modelID, textureID);
            });
        }
    }

    public void onUnload() {
        MultisResourceManager.removeListener(scriptID, this::onScriptReload);
        MultisResourceManager.removeListener(modelID, this::onModelReload);

        scriptSource = null;
        model = null;

        luaInterface = null;

        scriptID = null;
        modelID = null;
        textureID = null;
    }

    private interface MultisLuaInterface {
        String onUse();
    }
}