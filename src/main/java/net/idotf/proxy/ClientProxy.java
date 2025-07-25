package net.idotf.proxy;

import net.idotf.Main;
import net.idotf.entity.herobrine;
import net.idotf.events.client.CheatMod;
import net.idotf.events.client.DifficultyBlocker;
import net.idotf.events.client.TimerEvent;
import net.idotf.events.client.renderdistance.RenderDistance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        EntityRegistry.registerModEntity(
                new ResourceLocation("idotf", "herobrine"),
                herobrine.class,
                "Herobrine",
                0,
                Main.instance,
                64, 3, true
        );
        //RenderDistance.init();
        MinecraftForge.EVENT_BUS.register( new CheatMod() );
        TimerEvent.register();
        MinecraftForge.EVENT_BUS.register( new DifficultyBlocker() );
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