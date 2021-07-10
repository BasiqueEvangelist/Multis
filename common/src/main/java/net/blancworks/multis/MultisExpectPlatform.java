package net.blancworks.multis;

import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.resource.ResourceManager;

import java.util.function.Consumer;

public class MultisExpectPlatform {

    @ExpectPlatform
    public static void registerReloadListener(String ID, Consumer<ResourceManager> toRun){
        throw new AssertionError();
    }
}
