package net.blancworks.multis.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.api.rendering.models.builders.BWItemModelBuilder;
import net.blancworks.api.rendering.textures.BWTexture;
import net.blancworks.multis.resources.MultisResource;
import net.blancworks.multis.resources.MultisResourceManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class MultisItemModel extends MultisModel {
    public Identifier id;
    public Identifier textureID;

    public boolean isHandheld = false;

    public MultisItemModel(Identifier id, Identifier textureID) {
        this.id = id;
        this.textureID = textureID;

        MultisResourceManager.addListener(this.textureID, this::onTextureReload);
        onTextureReload(MultisResourceManager.getResource(textureID));
    }


    public void onTextureReload(MultisResource<byte[]> resource) {
        if (resource != null && resource.getValue() != null)
            MultisRenderingManager.doTask(() -> stitchTexture(resource.getValue()));
    }

    private void stitchTexture(byte[] data) {

        //Store this.
        Identifier texID = textureID;

        //Run this on the render thread when we get the chance.
        RenderSystem.recordRenderCall(() -> {
            try {
                //Create NativeImage from byte[]
                ByteBuffer wrapper = MemoryUtil.memAlloc(data.length);
                wrapper.put(data);
                wrapper.rewind();
                NativeImage temp_image = NativeImage.read(wrapper);

                BWTexture tex = MultisRenderingManager.itemAtlas.addTexture(textureID, temp_image);

                tex.parentAtlas = MultisRenderingManager.itemAtlas;
                //GL45.glObjectLabel(GL11.GL_TEXTURE, tex.parentAtlas.getGlId(), "Figura Atlas");

                this.texture = tex;

                buildModel(temp_image, tex);

                //temp_image.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void buildModel(NativeImage img, BWTexture texture) {
        //Do this on worker thread whenever we can
        MultisRenderingManager.doTask(() -> {
            //Build item model into this model
            BWItemModelBuilder.buildModel(this, img, texture);

            //Close image.
            img.close();

            texture.isReady = true;
        });
    }

    @Override
    public ModelTransformation getTransformation() {
        return isHandheld ? MultisRenderingManager.heldItemModelTransformation : MultisRenderingManager.itemModelTransformation;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }
}
