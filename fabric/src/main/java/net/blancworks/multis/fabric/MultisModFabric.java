package net.blancworks.multis.fabric;

import net.blancworks.multis.MultisMod;
import net.fabricmc.api.ModInitializer;

public class MultisModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MultisMod.init();
    }
}
