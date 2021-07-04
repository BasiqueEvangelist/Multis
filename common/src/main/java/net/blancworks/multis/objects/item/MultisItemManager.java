package net.blancworks.multis.objects.item;

import net.blancworks.multis.MultisMod;
import net.blancworks.multis.mixins.accessors.ItemStackAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class MultisItemManager {

    private static final Map<Identifier, MultisItem> itemRegistry = new HashMap<>();
    private static MultisItem emptyItem;



    public static void registerItem(Identifier id, MultisItem item) {
        item.id = id;
        itemRegistry.put(id, item);
    }

    /**
     * Returns a MultisItem for a given item stack.
     *
     * @param stack The item stack to check.
     * @return The item, if the stack has one. Empty item if none could be found.
     */
    public static MultisItem getItemFromStack(ItemStack stack) {
        if (stack == null || stack.getItem() != MultisMod.MULTIS_MINECRAFT_ITEM)
            return emptyItem;

        //Get NBT tag for item.
        String string = stack.getOrCreateTag().getString("multis_item");

        //Return empty for empty string.
        if (string.isEmpty())
            return emptyItem;

        //ID of the item, according to NBT data.
        Identifier nbtID = new Identifier(string);

        //Cast to accessor type.
        ItemStackAccessor accessor = (ItemStackAccessor) (Object) stack;

        //Get item from accessor.
        MultisItem getItem = accessor.multis_getMultisItem();

        if (getItem == null || !getItem.id.equals(nbtID)) {
            //if there is no item/ there's an item but the ID doesn't match NBT.
            return setItemFromID(accessor, nbtID);
        } else {
            //There is an item
            return getItem;
        }
    }

    /**
     * Sets an item to a stack given an ID.
     * @param accessor The ItemStackAccessor for the item stack.
     * @param id The ID to attempt to set the item to.
     * @return The item the stack was set to.
     */
    private static MultisItem setItemFromID(ItemStackAccessor accessor, Identifier id){
        //Find multis item at NBT tag location
        MultisItem itemAtID = getItemAtLocation(id);

        //Return empty when no item is found
        if (itemAtID == null) {
            accessor.multis_setMultisItem(null);
            return emptyItem;
        }

        //Set item so we don't check for it again later
        accessor.multis_setMultisItem(itemAtID);
        return itemAtID;
    }

    /**
     * Gets an item at a location in the registry.
     * @param id The location of the item in the registry.
     * @return The item if found, null otherwise.
     */
    public static MultisItem getItemAtLocation(Identifier id) {
        return itemRegistry.get(id);
    }
}
