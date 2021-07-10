package net.blancworks.multis.fabric;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class MultisExpectPlatformImpl {
    public static void registerReloadListener(String ID, Consumer<ResourceManager> toRun) {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void apply(ResourceManager manager) {
                toRun.accept(manager);
            }

            @Override
            public Identifier getFabricId() {
                return new Identifier("multis_" + ID, "data");
            }
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void apply(ResourceManager manager) {
                toRun.accept(manager);
            }

            @Override
            public Identifier getFabricId() {
                return new Identifier("multis_" + ID, "assets");
            }
        });
    }
}
