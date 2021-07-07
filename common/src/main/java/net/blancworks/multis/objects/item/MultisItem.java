package net.blancworks.multis.objects.item;

import net.blancworks.api.rendering.models.BWModel;
import net.blancworks.multis.resources.MultisResource;
import net.blancworks.multis.resources.MultisResourcePack;
import net.minecraft.util.Identifier;

/**
 * A Multis Item is basically a wrapper for a Minecraft item that redirects calls to a script.
 * They can be dynamically created or destroyed, as with most things in Multis.
 */
public class MultisItem {

    public Identifier id;

    public MultisResource<String> scriptSource;
    public MultisResource<BWModel> model;

    public MultisResourcePack targetPack;

    public MultisItem(Identifier id, MultisResourcePack pack){
        this.id = id;
        targetPack = pack;

        scriptSource = pack.scriptSet.getResource(id);
        pack.scriptSet.addListener(id, this::onScriptReload);
    }


    public void onScriptReload(MultisResource<String> source){
        scriptSource = source;
    }

    public void onUnload(){
        targetPack.scriptSet.removeListener(id, this::onScriptReload);
    }
}