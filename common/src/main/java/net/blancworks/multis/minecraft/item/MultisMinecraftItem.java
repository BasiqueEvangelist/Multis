package net.blancworks.multis.minecraft.item;

import net.blancworks.multis.objects.item.MultisItem;
import net.blancworks.multis.objects.item.MultisItemManager;
import net.blancworks.multis.resources.MultisResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.HashMap;

public class MultisMinecraftItem extends Item {
    public MultisMinecraftItem() {
        super(new Settings());
    }


    @Override
    public Text getName(ItemStack stack) {

        MultisItem item = MultisItemManager.getItemFromStack(stack);

        if(item == null)
            return super.getName(stack);

        return new LiteralText(MultisResourceManager.getTranslation(item.translationKey));
    }
}
