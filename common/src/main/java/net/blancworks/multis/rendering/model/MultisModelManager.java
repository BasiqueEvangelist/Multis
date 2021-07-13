package net.blancworks.multis.rendering.model;

import net.blancworks.multis.datapack.MultisDatapackReloadManager;
import net.blancworks.multis.resources.MultisResourceManager;
import net.blancworks.multis.resources.MultisStringResource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

public class MultisModelManager {

    private static final HashMap<Identifier, JsonUnbakedModel> modeLCache = new HashMap<>();

    public static void clearCache() {
        modeLCache.clear();
    }

    public static JsonUnbakedModel getJsonModel(Identifier id) {

        if (id.getPath().equals("builtin/generated"))
            return ModelLoader.GENERATION_MARKER;

        if (id.getPath().equals("builtin/entity"))
            return ModelLoader.BLOCK_ENTITY_MARKER;

        if (id.getPath().equals("builtin/missing"))
            return JsonUnbakedModel.deserialize(ModelLoader.MISSING_DEFINITION);

        Identifier realID = new Identifier(id.getNamespace(), "models/" + id.getPath() + ".json");
        JsonUnbakedModel mdl = modeLCache.get(realID);

        if (mdl == null) {
            try (InputStream is = MultisResourceManager.resourceManager.getResource(realID).getInputStream()) {
                String s = IOUtils.toString(is, StandardCharsets.UTF_8);

                modeLCache.put(realID, mdl = JsonUnbakedModel.deserialize(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return mdl;
    }

}
