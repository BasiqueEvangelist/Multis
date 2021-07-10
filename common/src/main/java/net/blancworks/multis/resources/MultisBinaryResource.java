package net.blancworks.multis.resources;

import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.io.InputStream;

public class MultisBinaryResource extends MultisResource<byte[]> {

    @Override
    public void readFromPacket(PacketByteBuf packet) {
        packet.writeByteArray(this.value);
    }

    @Override
    public void writeToPacket(PacketByteBuf packet) {
        this.value = packet.readByteArray();
    }

    @Override
    public void readFromInputStream(InputStream is) {
        try {
            this.value = new byte[is.available()];
            is.read(this.value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFactoryID() {
        return "binary";
    }
}
