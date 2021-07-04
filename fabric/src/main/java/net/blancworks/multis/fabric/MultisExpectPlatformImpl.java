package net.blancworks.multis.fabric;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class MultisExpectPlatformImpl {
    public static void registerReloadListener(Consumer<ResourceManager> toRun) {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("multis", "data");
            }

            @Override
            public void reload(ResourceManager manager) {
                toRun.accept(manager);
            }
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("multis", "assets");
            }

            @Override
            public void reload(ResourceManager manager) {
                toRun.accept(manager);
            }
        });
    }
}
