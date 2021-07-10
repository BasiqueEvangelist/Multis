package net.blancworks.multis.resources;

import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class MultisBinaryResource extends MultisResource<byte[]> {

    @Override
    public boolean readFromPacket(PacketByteBuf packet) {
        byte[] newDat = packet.readByteArray();

        if (!Arrays.equals(newDat, this.value)) {
            this.value = newDat;
            return true;
        }

        return false;
    }

    @Override
    public void writeToPacket(PacketByteBuf packet) {
        packet.writeByteArray(this.value);
    }

    @Override
    public boolean readFromInputStream(InputStream is) {
        try {
            byte[] newDat = new byte[is.available()];
            is.read(newDat);

            if (!Arrays.equals(newDat, value)) {
                value = newDat;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String getFactoryID() {
        return "binary";
    }
}
