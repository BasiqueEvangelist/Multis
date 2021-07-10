package net.blancworks.multis.resources;

import com.google.common.base.Charsets;
import net.minecraft.network.PacketByteBuf;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

public class MultisStringResource extends MultisResource<String> {
    @Override
    public boolean readFromPacket(PacketByteBuf packet) {
        String newVal = packet.readString();

        if(!newVal.equals(value)) {
            value = newVal;

            return true;
        }

        return false;
    }

    @Override
    public void writeToPacket(PacketByteBuf packet) {
        packet.writeString(value);
    }

    @Override
    public boolean readFromInputStream(InputStream is) {
        try {
            String newVal = IOUtils.toString(is, Charsets.UTF_8);

            if (!newVal.equals(value)) {
                value = newVal;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String getFactoryID() {
        return "string";
    }
}
