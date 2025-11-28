package net.idotf.events.soundevents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Plate13SoundEvent {
    private static final String TEXT = "Now playing: C418 - 13";
    private static final int DURATION_TICKS = 250;
    
    private static int displayTicks = 0;
    private static float hue = 0f;

    public static void triggerPlateEvent() {
        displayTicks = DURATION_TICKS;
        hue = 0f;
        playSound();
    }

    private static void playSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(
                new PositionedSoundRecord(
                        SoundEvents.RECORD_13,
                        SoundCategory.MUSIC,
                        1.0F,
                        1.0F,
                        0.0F,
                        0.0F,
                        0.0F
                ) {
                    @Override
                    public float getVolume() {
                        return 1.0F;
                    }

                    @Override
                    public AttenuationType getAttenuationType() {
                        return ISound.AttenuationType.NONE;
                    }
                }
            );
        }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (displayTicks > 0) {
            displayTicks--;
            hue += 0.005f;
            if (hue >= 1f) hue = 0f;
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (displayTicks <= 0) return;

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRenderer;

        int screenWidth = event.getResolution().getScaledWidth();
        int screenHeight = event.getResolution().getScaledHeight();

        float alpha = MathHelper.clamp(displayTicks / 20f, 0f, 1f);
        Color color = Color.getHSBColor(hue, 0.8f, 1f);
        int rgba = (color.getRGB() & 0xFFFFFF) | ((int) (alpha * 255) << 24);

        int textWidth = fr.getStringWidth(TEXT);
        int posX = (screenWidth - textWidth) / 2;
        int posY = screenHeight - 60;

        renderTextWithEffects(fr, TEXT, posX, posY, rgba, alpha);
    }

    public static void renderTextWithEffects(FontRenderer fr, String text, int x, int y, int color, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int shadowColor = (0x111111 & 0xFFFFFF) | ((int) (alpha * 100) << 24);
        fr.drawString(text, x + 1, y + 1, shadowColor, false);
        fr.drawString(text, x, y, color, false);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}