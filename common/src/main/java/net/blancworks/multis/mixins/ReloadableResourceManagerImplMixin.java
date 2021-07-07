package net.blancworks.multis.mixins;

import net.blancworks.multis.access.ReloadableResourceManagerImplAccessor;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ReloadableResourceManagerImpl.class)
public class ReloadableResourceManagerImplMixin implements ReloadableResourceManagerImplAccessor {

    @Shadow @Final private List<ResourcePack> field_25145;

    @Override
    public List<ResourcePack> multis_getPackList() {
        return field_25145;
    }
}
