package net.blancworks.multis.resources;

public class MultisByteArrayResource extends MultisResource {

    public byte[] data;

    public MultisByteArrayResource(byte[] data){
        this.data = data;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onUnload() {

    }
}
