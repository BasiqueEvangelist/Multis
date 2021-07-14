package net.blancworks.multis.mixins;

import net.blancworks.multis.access.TranslationStorageAccessor;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(TranslationStorage.class)
public class TranslationStorageMixin implements TranslationStorageAccessor {

    @Shadow @Final private Map<String, String> translations;

    @Override
    public Map<String, String> getMap() {
        return translations;
    }
}
