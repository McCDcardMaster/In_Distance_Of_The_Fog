package net.idotf.events.client;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class CheatMod {
    
    private final List<Integer> expectedSequence = new ArrayList<>();
    private final List<Integer> enteredSequence = new ArrayList<>();
    private boolean sequenceActive = false;
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        expectedSequence.add(Keyboard.KEY_H);
        expectedSequence.add(Keyboard.KEY_I);
        
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        boolean shiftOn = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        
        if (!shiftOn) {
            enteredSequence.clear();
            sequenceActive = false;
            return;
        }
        
        if (Keyboard.getEventKey() == Keyboard.KEY_GRAVE && Keyboard.getEventKeyState()) {
            if (sequenceActive && enteredSequence.equals(expectedSequence)) {
                if (Minecraft.getMinecraft().player != null) {
                    Minecraft.getMinecraft().player.sendMessage( new TextComponentString( "Test" ) );
                    MinecraftServer server = Minecraft.getMinecraft().getIntegratedServer();
                    if (server != null) {
                        TimerEvent.triggerRandomEvent( server );
                    }
                }
            }
            enteredSequence.clear();
            sequenceActive = false;
            return;
        }
        
        if (Keyboard.getEventKeyState() && Keyboard.getEventKey() != Keyboard.KEY_GRAVE) {
            if (!sequenceActive) {
                sequenceActive = true;
                enteredSequence.clear();
            }
            enteredSequence.add(Keyboard.getEventKey());
            
            if (enteredSequence.size() > expectedSequence.size()) {
                enteredSequence.remove(0);
            }
        }
    }
}