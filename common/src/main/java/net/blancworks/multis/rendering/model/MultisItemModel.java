package net.blancworks.multis.rendering.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.api.rendering.models.BWModel;
import net.blancworks.api.rendering.models.builders.BWItemModelBuilder;
import net.blancworks.api.rendering.textures.BWTexture;
import net.blancworks.multis.rendering.MultisRenderingManager;
import net.blancworks.multis.resources.MultisBinaryResource;
import net.blancworks.multis.resources.MultisResource;
import net.blancworks.multis.resources.MultisResourceManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MultisItemModel extends MultisModel {

    public final HashMap<Identifier, Layer> layers = new LinkedHashMap<>();

    @Override
    public void onUnload() {
        for (Map.Entry<Identifier, Layer> entry : layers.entrySet()) {
            entry.getValue().onUnload();
        }

        layers.clear();
    }

    @Override
    public void finalizeGeneration(Identifier id, JsonUnbakedModel mdl) {
        //Default transformations/side-light
        if (mdl == null) {
            transformation = MultisModelManager.getJsonModel(new Identifier("item/generated")).getTransformations();
            guiSideLit = false;
        }

        //Default texture.
        if(textureDependencies.size() == 0)
            textureDependencies.add(new Identifier(id.getNamespace(), "multis/textures/items/" + id.getPath()));


        //Load layers from textures.
        for (Identifier dependency : textureDependencies) {
            layers.put(dependency, new Layer(dependency));
        }
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider provider, int light, int overlay) {
        for (Map.Entry<Identifier, Layer> entry : layers.entrySet()) {

            Layer l = entry.getValue();
            VertexConsumer consumer = provider.getBuffer(RenderLayer.getItemEntityTranslucentCull(MultisRenderingManager.atlasID));

            //entry.getValue().model.texture.debug = MinecraftClient.getInstance().mouse.wasRightButtonClicked();
            entry.getValue().model.render(stack, consumer, overlay, light);
        }
    }

    protected class Layer {
        public Identifier id;
        public BWTexture texture;
        public BWModel model;

        private final Consumer<MultisResource> reloadEvent = this::onTextureAssetReload;

        public Layer(Identifier id) {
            this.id = id;
            model = new BWModel();
            MultisResourceManager.addListener(id, reloadEvent);
            onTextureAssetReload(MultisResourceManager.getResource(id));
        }

        public void onTextureAssetReload(MultisResource<byte[]> resource) {
            MultisBinaryResource mbr = (MultisBinaryResource) resource;

            if (mbr == null)
                return;

            RenderSystem.recordRenderCall(() -> {
                try {
                    byte[] data = mbr.getValue();
                    //Create NativeImage from byte[]
                    ByteBuffer wrapper = MemoryUtil.memAlloc(data.length);
                    wrapper.put(data);
                    wrapper.rewind();
                    NativeImage temp_image = NativeImage.read(wrapper);

                    texture = MultisRenderingManager.globalAtlas.addTexture(id, temp_image);

                    texture.parentAtlas = MultisRenderingManager.globalAtlas;

                    MultisRenderingManager.doTask(() -> {
                        BWItemModelBuilder.buildModel(model, temp_image, texture);
                        model.texture = texture;

                        temp_image.close();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        public void onUnload() {
            MultisResourceManager.removeListener(id, reloadEvent);
        }
    }
}
