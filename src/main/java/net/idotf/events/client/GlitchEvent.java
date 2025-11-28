package net.idotf.events.client;

import net.idotf.events.soundevents.GlitchSoundEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class GlitchEvent {
    private static boolean isGlitchActive = false;
    private static long glitchStartTime;
    private static final Random rand = new Random();
    private static final float MAX_SHAKE = 0.10f;
    private static final float GLITCH_DURATION = 5.5f;
    private static boolean originalAnaglyphState;

    public static void startGlitchEffect() {
        if (!isGlitchActive) {
            isGlitchActive = true;
            glitchStartTime = System.currentTimeMillis();
            originalAnaglyphState = Minecraft.getMinecraft().gameSettings.anaglyph;
            MinecraftForge.EVENT_BUS.register(new GlitchEvent());
            GlitchSoundEvent.play();
        }
    }

    public static void stopGlitchEffect() {
        if (isGlitchActive) {
            isGlitchActive = false;
            MinecraftForge.EVENT_BUS.unregister(GlitchEvent.class);
            Minecraft.getMinecraft().gameSettings.anaglyph = originalAnaglyphState;
            GlitchSoundEvent.stop();
			
			applyRandomTextureDistortion();
        }
    }
	
	private static void applyRandomTextureDistortion() {
        int mode = 1 + rand.nextInt(2);
        GlobalReloadTextureEvent.applyDistortion(mode);
        System.out.println("[Glitch] Applied texture distortion mode: " + mode);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getMinecraft().world == null) {
                stopGlitchEffect();
            }

            if (isGlitchActive) {
                Minecraft.getMinecraft().gameSettings.anaglyph = (System.currentTimeMillis() % 200 < 100);
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (!isGlitchActive || event.phase != TickEvent.Phase.START) return;

        float progress = (System.currentTimeMillis() - glitchStartTime) / 1000f;
        float intensity = (1 - (progress / GLITCH_DURATION)) * MAX_SHAKE;

        GlStateManager.translate(
            Math.sin(progress * 250) * intensity + 
            Math.cos(progress * 250) * intensity * 0.5f,
            0,
            0
        );
    }

    @SubscribeEvent
    public void onPostRender(RenderGameOverlayEvent.Post event) {
        if (isGlitchActive) {
            if ((System.currentTimeMillis() - glitchStartTime) > GLITCH_DURATION * 1000) {
                stopGlitchEffect();
            }
        }
    }
}