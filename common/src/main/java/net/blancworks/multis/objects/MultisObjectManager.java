package net.blancworks.multis.objects;

import me.shedaniel.architectury.networking.NetworkManager;
import net.blancworks.multis.loader.MultisPack;
import net.blancworks.multis.loader.MultisPackManager;
import net.blancworks.multis.objects.item.MultisItem;
import net.blancworks.multis.resources.MultisStringResource;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class MultisObjectManager {



    public static Map<Identifier, MultisItem> items = new HashMap<>();


    /**
     * Takes an ID and a script ID and registers an item.
     * @param id The ID of the item to register.
     * @param scriptID The ID of the script to use for the item.
     */
    public static void registerItem(Identifier id, Identifier scriptID) {
        MultisStringResource scriptSource = (MultisStringResource)MultisPackManager.getPack(id.getNamespace()).scriptPack.objects.get(id);

        if(scriptSource == null) return;

        System.out.println(scriptSource.string);
    }
}
