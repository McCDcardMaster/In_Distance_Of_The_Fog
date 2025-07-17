package net.idotf.proxy;

import net.idotf.events.generator.DeadForest;
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
        MinecraftForge.TERRAIN_GEN_BUS.register( new DeadForest() );
        RenderingRegistry.registerEntityRenderingHandler( herobrine.class, Renderer::new);
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

}