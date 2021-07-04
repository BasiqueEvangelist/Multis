package net.blancworks.multis;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resource.ResourceManager;

import java.util.function.Consumer;

public class MultisExpectPlatform {

    @ExpectPlatform
    public static void registerReloadListener(Consumer<ResourceManager> toRun){
        throw new AssertionError();
    }
}
