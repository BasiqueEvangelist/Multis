package net.blancworks.multis.resources;

import com.google.common.collect.ImmutableMap;
import net.blancworks.api.rendering.models.BWModel;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MultisResourcePack {

    public static final MultisResourceSet<String> scriptSet = new MultisResourceSet<>();
    public static final MultisResourceSet<byte[]> textureSet = new MultisResourceSet<>();
    public static final MultisResourceSet<BWModel> modelSet = new MultisResourceSet<>();
    public static final MultisResourceSet<String> jsonSet = new MultisResourceSet<>();

    public static final Map<MultisResourceType, MultisResourceSet> sets = new ImmutableMap.Builder<MultisResourceType, MultisResourceSet>()
            .put(MultisResourceType.Script, scriptSet)
            .put(MultisResourceType.Texture, textureSet)
            .put(MultisResourceType.Model, modelSet)
            .put(MultisResourceType.Json, jsonSet)
            .build();

    public static final Map<MultisResourceType, BiConsumer<Identifier, PacketByteBuf>> readers = new ImmutableMap.Builder<MultisResourceType, BiConsumer<Identifier, PacketByteBuf>>()
            .put(MultisResourceType.Script, MultisResourcePack::readScript)
            .build();

    public static MultisResource getResource(MultisResourceType type, Identifier id) {
        switch (type) {
            case Json:
                return jsonSet.getResource(id);
            case Model:
                return modelSet.getResource(id);
            case Script:
                return scriptSet.getResource(id);
            case Texture:
                return textureSet.getResource(id);
        }

        return null;
    }

    public static void setResource(MultisResourceType type, Identifier id, PacketByteBuf buff) {

    }

    public static void readScript(Identifier id, PacketByteBuf buff){
        MultisStringResource msr = new MultisStringResource();
        msr.readFromPacket(buff);
        scriptSet.setResource(id, msr);
    }
}
