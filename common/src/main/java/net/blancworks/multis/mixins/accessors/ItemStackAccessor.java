package net.blancworks.multis.mixins.accessors;

import net.blancworks.multis.objects.item.MultisItem;

public interface ItemStackAccessor {
    MultisItem multis_getMultisItem();
    void multis_setMultisItem(MultisItem item);
}
