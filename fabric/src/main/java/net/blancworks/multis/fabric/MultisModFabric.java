package net.blancworks.multis.fabric;

import me.shedaniel.architectury.utils.Env;
import me.shedaniel.architectury.utils.EnvExecutor;
import net.blancworks.multis.MultisMod;
import net.fabricmc.api.ModInitializer;

public class MultisModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MultisMod.init_common();
        EnvExecutor.runInEnv(Env.CLIENT, ()->MultisMod::init_client);
        EnvExecutor.runInEnv(Env.SERVER, ()->MultisMod::init_server);
    }
}
