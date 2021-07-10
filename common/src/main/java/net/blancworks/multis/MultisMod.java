package net.blancworks.multis;

import me.shedaniel.architectury.registry.DeferredRegister;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.blancworks.api.scripting.scripts.BWLuaScript;
import net.blancworks.multis.datapack.MultisDatapackManager;
import net.blancworks.multis.lua.LuaEnvironment;
import net.blancworks.multis.minecraft.item.MultisMinecraftItem;
import net.blancworks.multis.networking.MultisNetworkManager;
import net.blancworks.multis.rendering.MultisRenderingManager;
import net.blancworks.multis.resources.MultisResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

/**
 * Root instance of the Multis Mod.
 */
public class MultisMod {

    public static final String MOD_ID = "multis";

    public static final MultisMinecraftItem MULTIS_MINECRAFT_ITEM = new MultisMinecraftItem();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_KEY);
    public static final RegistrySupplier<Item> ITEM_SUPPLIER = ITEMS.register("multis_item", () -> MULTIS_MINECRAFT_ITEM);

    /**
     * Init client-server common systems.
     */
    public static void init_common() {
        ITEMS.register();

        BWLuaScript.setupNativesForLua();
        MultisExpectPlatform.registerReloadListener("datapack", MultisDatapackManager::onDatapackReload);


        LuaEnvironment.init();
    }

    /**
     * Init client-specific systems.
     */
    public static void init_client() {
        MultisRenderingManager.init();
        MultisNetworkManager.client_init();
        LuaEnvironment.client_init();
        MultisResourceManager.client_init();
    }

    /**
     * Init server-specific systems.
     */
    public static void init_server() {
        MultisNetworkManager.server_init();
        LuaEnvironment.server_init();
    }

}
