package net.blancworks.multis.resources;

import net.minecraft.network.PacketByteBuf;

import java.io.InputStream;

public abstract class MultisResource<T> {
    protected T value;

    public T getValue(){
        return value;
    }

    /**
     * Reads the resource from a packet.
     * @param packet
     */
    public abstract boolean readFromPacket(PacketByteBuf packet);

    /**
     * Writes the resource into a packet.
     * @param packet
     */
    public abstract void writeToPacket(PacketByteBuf packet);

    /**
     * Reads the resource from an InputStream.
     * @param is
     */
    public abstract boolean readFromInputStream(InputStream is);

    /**
     * @return The ID of the factory used to create this resource.
     */
    public abstract String getFactoryID();
}
