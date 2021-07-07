package net.blancworks.multis.access;

import net.blancworks.multis.objects.item.MultisItem;

public interface ItemStackAccessor {
    MultisItem multis_getMultisItem();
    void multis_setMultisItem(MultisItem item);
}
