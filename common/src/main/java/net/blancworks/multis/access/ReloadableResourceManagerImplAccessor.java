package net.blancworks.multis.access;

import com.google.common.collect.Lists;
import net.minecraft.resource.ResourcePack;

import java.util.List;

public interface ReloadableResourceManagerImplAccessor {

    List<ResourcePack> multis_getPackList();

}
