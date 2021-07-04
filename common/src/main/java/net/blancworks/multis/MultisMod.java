package net.blancworks.multis;

import me.shedaniel.architectury.utils.Env;
import me.shedaniel.architectury.utils.EnvExecutor;
import net.blancworks.multis.loader.MultisPackManager;
import net.blancworks.multis.minecraft.item.MultisMinecraftItem;

/**
 * Root instance of the Multis Mod.
 */
public class MultisMod {

    public static final String MOD_ID = "multis";

    public static final MultisMinecraftItem MULTIS_MINECRAFT_ITEM = new MultisMinecraftItem();

    /**
     * Init client-server common systems.
     */
    public static void init_common() {
        MultisExpectPlatform.registerReloadListener(MultisPackManager::loadResourcePacksIntoMultisPacks);
    }

    /**
     * Init client-specific systems.
     */
    public static void init_client() {

    }

    /**
     * Init server-specific systems.
     */
    public static void init_server() {

    }

}
