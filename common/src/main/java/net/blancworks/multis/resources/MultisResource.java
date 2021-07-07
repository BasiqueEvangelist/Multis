package net.blancworks.multis.resources;

import net.minecraft.network.PacketByteBuf;

import java.io.InputStream;

public abstract class MultisResource<T> {
    protected T value;

    public T getValue(){
        return value;
    }

    public abstract void readFromPacket(PacketByteBuf packet);

    public abstract void writeToPacket(PacketByteBuf packet);

    public abstract void readFromInputStream(InputStream is);
}
