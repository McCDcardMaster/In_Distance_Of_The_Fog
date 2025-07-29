package net.idotf.proxy;

import net.idotf.entity.render.Renderer;
import net.idotf.entity.herobrine;
import net.idotf.events.client.CheatMod;
import net.idotf.events.client.DifficultyBlocker;
import net.idotf.events.client.TimerEvent;
import net.idotf.events.client.renderdistance.RenderDistance;
import net.idotf.events.soundevents.RandomMobsSounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        RenderingRegistry.registerEntityRenderingHandler( herobrine.class, Renderer::new);
        RenderDistance.init();
        MinecraftForge.EVENT_BUS.register( new CheatMod() );
        TimerEvent.registerClientHandlers();
        MinecraftForge.EVENT_BUS.register( new DifficultyBlocker() );
        MinecraftForge.EVENT_BUS.register( new RandomMobsSounds() );
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }
}