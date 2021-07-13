package net.blancworks.multis.rendering.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.api.rendering.models.BWModel;
import net.blancworks.api.rendering.models.BWModelPart;
import net.blancworks.api.rendering.textures.BWTexture;
import net.blancworks.multis.rendering.MultisRenderingManager;
import net.blancworks.multis.resources.MultisBinaryResource;
import net.blancworks.multis.resources.MultisResource;
import net.blancworks.multis.resources.MultisResourceManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.*;

public abstract class MultisModel {

    public List<Identifier> textureDependencies = new ArrayList<>();
    public ModelTransformation transformation;
    public boolean guiSideLit = false;

    public Identifier rootID;

    private static final Identifier missingno_id = new Identifier("missingno");

    public void updateFromJsonUnbakedModel(Identifier id, JsonUnbakedModel mdl) {
        //Get textures first because they also get parent set up.
        Collection<SpriteIdentifier> spriteIDs = mdl.getTextureDependencies(MultisModelManager::getJsonModel, new HashSet<>());

        //Filter out missingno texture
        spriteIDs.removeIf((s) -> s.getTextureId().equals(missingno_id));

        //Set textures to stitch into item atlas
        for (SpriteIdentifier spriteID : spriteIDs) {
            Identifier textureID = spriteID.getTextureId();

            //Cannot depend on vanilla textures, they remap to this mod's namespace.
            if (textureID.getNamespace().equals("minecraft")) {
                textureID = new Identifier(id.getNamespace(), textureID.getPath());
            }

            //Converts the texture from an "easy" ID to the actual texture path.
            Identifier realTextureID = new Identifier(textureID.getNamespace(), "multis/textures/" + textureID.getPath());

            textureDependencies.add(realTextureID);
        }

        transformation = mdl.getTransformations();
        guiSideLit = mdl.getGuiLight().isSide();

        List<ModelElement> elements = mdl.getElements();

        //Generate a model from the elements provided
        if (elements != null && elements.size() != 0) {

        } else if (mdl.getRootModel() == ModelLoader.GENERATION_MARKER) {
            finalizeGeneration(id, mdl);
        }
    }

    public abstract void onUnload();

    public abstract void finalizeGeneration(Identifier id, JsonUnbakedModel mdl);

    public abstract void render(MatrixStack stack, VertexConsumerProvider provider, int light, int overlay);
}
