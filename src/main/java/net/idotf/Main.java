package net.idotf;

import net.idotf.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Main.MOD_ID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MOD_ID = "idotf";
    public static final String NAME = "In Distance Of The Fog";
	public static final String VERSION = "";
	
	@Mod.Instance(MOD_ID)
    public static Main instance;
	
	@SidedProxy(clientSide = "net.idotf.proxy.ClientProxy", serverSide = "net.idotf.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit( event );
	}

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init( event );
	}
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit( event );
    }
}