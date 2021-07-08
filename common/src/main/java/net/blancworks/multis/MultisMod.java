package net.blancworks.multis;

import me.shedaniel.architectury.registry.ReloadListeners;
import net.blancworks.api.scripting.scripts.BWLuaScript;
import net.blancworks.multis.datapack.MultisDatapackManager;
import net.blancworks.multis.lua.LuaEnvironment;
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
        BWLuaScript.setupNativesForLua();
        MultisExpectPlatform.registerReloadListener(MultisDatapackManager::onDatapackReload);
        LuaEnvironment.init();
    }

    /**
     * Init client-specific systems.
     */
    public static void init_client() {
        MultisNetworkManager.client_init();
        LuaEnvironment.client_init();
    }

    /**
     * Init server-specific systems.
     */
    public static void init_server() {
        MultisNetworkManager.server_init();
        LuaEnvironment.server_init();
    }

}
