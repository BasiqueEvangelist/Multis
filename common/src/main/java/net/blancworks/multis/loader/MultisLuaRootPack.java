package net.blancworks.multis.loader;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import net.blancworks.multis.resources.MultisStringResource;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MultisLuaRootPack extends MultisRootPack {
    @Override
    public void registerScript(Identifier id, InputStream stream) {
        try {
            ByteSource byteSource = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    return stream;
                }
            };

            String text = byteSource.asCharSource(Charsets.UTF_8).read();

            MultisStringResource stringResource = new MultisStringResource(text);

            scriptPack.objects.put(id, stringResource);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void unregisterScript(Identifier id) {
        scriptPack.objects.remove(id);
    }
}
