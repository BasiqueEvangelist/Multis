package net.blancworks.multis.objects;

import net.blancworks.multis.objects.item.MultisItem;
import net.blancworks.multis.objects.item.MultisItemManager;
import net.minecraft.util.Identifier;

import java.util.Map;

public class MultisObjectManager {

    public static final Map<Identifier, MultisItem> ITEM_REGISTRY = MultisItemManager.itemRegistry;


    /**
     * Registers an item.
     *
     * The script for the item is the script at the location matching the ID of the item.
     * An item with the id 'fruits:banana' will search for the script at 'fruits:scripts/items/banana.lua'
     *
     * This method will fail and return null if there is already a given item with an ID.
     *
     * @param id The ID of the item.
     * @param hasModel Determines if the item has a model or not. If false, one will be auto-generated, matching the
     *                 texture of the item. The texture is found by ID, the same way the script is.
     * @return The item created.
     */
    public static MultisItem registerItem(Identifier id, boolean hasModel) {

        //Do nothing if an item with this ID exists.
        if(ITEM_REGISTRY.containsKey(id)) return null;

        MultisItem createdItem = new MultisItem(id);
        ITEM_REGISTRY.put(id, createdItem);

        System.out.println("CREATED MULTIS ITEM " + id);
        return createdItem;
    }

}
