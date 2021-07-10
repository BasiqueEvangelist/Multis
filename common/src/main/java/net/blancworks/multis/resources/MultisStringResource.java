package net.blancworks.multis.resources;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.io.InputStream;

public class MultisStringResource extends MultisResource<String> {
    @Override
    public void readFromPacket(PacketByteBuf packet) {
        value = packet.readString();
    }

    @Override
    public void writeToPacket(PacketByteBuf packet) {
        packet.writeString(value);
    }

    @Override
    public void readFromInputStream(InputStream is) {
        try {
            ByteSource byteSource = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    return is;
                }
            };

            value = byteSource.asCharSource(Charsets.UTF_8).read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFactoryID() {
        return "string";
    }
}
