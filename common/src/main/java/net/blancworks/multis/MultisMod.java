package net.blancworks.multis;

import me.shedaniel.architectury.utils.Env;
import me.shedaniel.architectury.utils.EnvExecutor;

/**
 * Root instance of the Multis Mod.
 */
public class MultisMod {

    public static final String MOD_ID = "multis";
    
    /**
     * Initializes the mod.
     */
    public static void init() {
        init_common();
        EnvExecutor.runInEnv(Env.CLIENT, () -> MultisMod::init_client);
        EnvExecutor.runInEnv(Env.SERVER, () -> MultisMod::init_server);
    }

    /**
     * Init client-server common systems.
     */
    private static void init_common() {

    }

    /**
     * Init client-specific systems.
     */
    private static void init_client() {

    }

    /**
     * Init server-specific systems.
     */
    private static void init_server() {

    }

}
