package net.blancworks.multis.resources;

import net.minecraft.network.PacketByteBuf;

import java.util.function.Supplier;

/**
 * MultisResourceType is the class used to handle the creation, parsing, and updating of a specific kind of MultisResource.
 */
public class MultisResourceType {
    /**
     * The ID of this resource type.
     */
    public short id = Short.MIN_VALUE;

    /**
     * Factory used to create the instances of this resource type.
     */
    protected final Supplier<MultisResource> factory;

    public MultisResourceType(Supplier<MultisResource> factory){
        this.factory = factory;
    }
}
