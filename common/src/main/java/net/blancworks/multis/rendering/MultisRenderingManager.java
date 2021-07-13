package net.blancworks.multis.rendering;

import com.google.common.base.Charsets;
import me.shedaniel.architectury.event.events.client.ClientPlayerEvent;
import net.blancworks.api.rendering.textures.BWAtlas;
import net.blancworks.api.rendering.textures.BWTexture;
import net.blancworks.multis.MultisExpectPlatform;
import net.blancworks.multis.resources.MultisBinaryResource;
import net.blancworks.multis.resources.MultisResource;
import net.blancworks.multis.resources.MultisResourceManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import javax.naming.BinaryRefAddr;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


//TODO - Move model stuff to it's own class
public class MultisRenderingManager {
    public static final Identifier atlasID = new Identifier("multis", "item_atlas");
    public static final BWAtlas globalAtlas = new BWAtlas(atlasID, 16, 2, 2);

    private static final HashMap<Identifier, Consumer<MultisResource>> resourceReloadListeners = new HashMap<>();

    public static ModelTransformation itemModelTransformation;
    public static ModelTransformation heldItemModelTransformation;

    private static CompletableFuture<Void> futureTask = CompletableFuture.completedFuture(null);

    public static void init() {
        MultisExpectPlatform.registerReloadListener("rendering", m -> {
            MinecraftClient.getInstance().getTextureManager().registerTexture(atlasID, globalAtlas);

            globalAtlas.reload();

            ModelTransformation mt = loadTransformationFromResource(m, new Identifier("minecraft", "models/item/generated.json"));
            itemModelTransformation = mt == null ? itemModelTransformation : mt;

            mt = loadTransformationFromResource(m, new Identifier("minecraft", "models/item/handheld.json"));
            heldItemModelTransformation = mt == null ? heldItemModelTransformation : mt;
        });

        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register((p) -> {
            if (p != null)
                globalAtlas.clear();
        });
    }

    private static ModelTransformation loadTransformationFromResource(ResourceManager manager, Identifier id) {
        try {
            Resource r = manager.getResource(id);
            InputStream is = r.getInputStream();

            try {
                String jsonText = IOUtils.toString(is, Charsets.UTF_8);
                JsonUnbakedModel mdl = JsonUnbakedModel.deserialize(jsonText);

                return mdl.getTransformations();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                is.close();
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return null;
    }

    /**
     * Sets the Multis Rendering System to run a given action, eventually.
     *
     * @param rb The action to run.
     */
    public static synchronized void doTask(Runnable rb) {
        futureTask = futureTask.thenRunAsync(rb);
    }
}
