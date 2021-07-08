package net.blancworks.multis.objects.item;

import net.blancworks.multis.resources.MultisResource;
import net.blancworks.multis.resources.MultisResourceManager;
import net.minecraft.util.Identifier;

/**
 * A Multis Item is basically a wrapper for a Minecraft item that redirects calls to a script.
 * They can be dynamically created or destroyed, as with most things in Multis.
 */
public class MultisItem {

    public Identifier id;

    public MultisResource<String> scriptSource;

    public MultisItem(Identifier id){
        this.id = id;

        scriptSource = MultisResourceManager.getResource(id);
        MultisResourceManager.addListener(id, this::onScriptReload);
    }


    public void onScriptReload(MultisResource<String> source){
        scriptSource = source;
    }

    public void onUnload(){
        MultisResourceManager.removeListener(id, this::onScriptReload);
    }
}