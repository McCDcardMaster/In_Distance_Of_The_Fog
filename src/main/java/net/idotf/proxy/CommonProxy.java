package net.idotf.proxy;

import net.idotf.events.client.DifficultyBlocker;
import net.idotf.events.client.TimerEvent;
import net.idotf.events.structures.DeadForestStructure;
import net.minecraftforge.common.MinecraftForge;
import net.idotf.entity.herobrine;
import net.idotf.entity.render.Renderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.TERRAIN_GEN_BUS.register( new DeadForestStructure() );
        RenderingRegistry.registerEntityRenderingHandler( herobrine.class, Renderer::new);
        TimerEvent.register();
        MinecraftForge.EVENT_BUS.register( new DifficultyBlocker() );
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

}