package net.blancworks.multis.loader;

import net.blancworks.multis.resources.MultisResource;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Multis packs represent a collection of Multis Objects.
 * Packs are objects themselves, and therefore, recursive packs are allowed.
 */
public class MultisPack extends MultisResource {

    /**
     * A map of objects, by ID.
     */
    public final Map<Identifier, MultisResource> objects = new HashMap<>();


    @Override
    public void onLoad() {

    }

    @Override
    public void onUnload() {
        //Unload all child objects of this pack.
        for (Map.Entry<Identifier, MultisResource> entry : objects.entrySet()) {
            entry.getValue().onUnload();
        }
    }

    public Set<Map.Entry<Identifier, MultisResource>> getEntries(){
        return objects.entrySet();
    }
    public Set<Identifier> getKeys(){
        return objects.keySet();
    }
}
