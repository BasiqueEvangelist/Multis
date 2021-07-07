package net.blancworks.multis.objects;

import net.blancworks.multis.objects.item.MultisItem;
import net.blancworks.multis.resources.MultisResourceSet;

/**
 * Multis Object Packs are packs of, you guessed it, Multis Objects.
 * These work very similarly to MultisResources, but the difference is mostly that MultisObjects typically reference
 * resources for the bulk of their work.
 */
public class MultisObjectPack {

    /**
     * The namespace of this pack.
     */
    public String namespace;

    /**
     * The set of all items contained within this pack.
     */
    public MultisResourceSet<MultisItem> itemSet = new MultisResourceSet<>();

}
