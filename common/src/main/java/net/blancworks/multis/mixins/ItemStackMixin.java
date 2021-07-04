package net.blancworks.multis.mixins;

import net.blancworks.multis.mixins.accessors.ItemStackAccessor;
import net.blancworks.multis.objects.item.MultisItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackAccessor {
    private MultisItem multis_multisItem;

    @Override
    public MultisItem multis_getMultisItem() {
        return multis_multisItem;
    }

    @Override
    public void multis_setMultisItem(MultisItem item) {
        multis_multisItem = item;
    }
}
