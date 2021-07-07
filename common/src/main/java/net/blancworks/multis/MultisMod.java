package net.blancworks.multis;

import net.blancworks.multis.datapack.MultisDatapackManager;
import net.blancworks.multis.minecraft.item.MultisMinecraftItem;
import net.blancworks.multis.networking.MultisNetworkManager;

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
        MultisExpectPlatform.registerReloadListener(MultisDatapackManager::onDatapackReload);
    }

    /**
     * Init client-specific systems.
     */
    public static void init_client() {
        MultisNetworkManager.client_init();
    }

    /**
     * Init server-specific systems.
     */
    public static void init_server() {
        MultisNetworkManager.server_init();
    }

}
