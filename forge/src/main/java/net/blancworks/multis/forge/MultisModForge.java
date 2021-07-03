package net.blancworks.multis.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.blancworks.multis.MultisMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MultisMod.MOD_ID)
public class MultisModForge {
    public MultisModForge(){
        EventBuses.registerModEventBus(MultisMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        MultisMod.init();
    }
}
