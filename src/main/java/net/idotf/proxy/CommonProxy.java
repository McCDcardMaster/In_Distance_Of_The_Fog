package net.idotf.proxy;

import net.idotf.Main;
import net.idotf.events.client.DifficultyBlocker;
import net.idotf.events.client.TimerEvent;
import net.idotf.events.structures.DeadForestStructure;
import net.minecraftforge.common.MinecraftForge;
import net.idotf.entity.herobrine;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event) {
		registerEntities();
        MinecraftForge.TERRAIN_GEN_BUS.register( new DeadForestStructure() );
        TimerEvent.registerCommon();
        MinecraftForge.EVENT_BUS.register( new DifficultyBlocker() );
    }
	
	private void registerEntities() {
		
		EntityRegistry.registerModEntity(
                new ResourceLocation("idotf", "herobrine"),
                herobrine.class,
                "Herobrine",
                0,
                Main.instance,
                64, 3, true
        );
	}
	
    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

}