package net.idotf.events.soundevents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class Plate13SoundEvent {
    public static final Plate13SoundEvent INSTANCE = new Plate13SoundEvent();
    
    private static final String TEXT = "Now playing: C418 - 13";
    private static final int DURATION_TICKS = 250;
    private static final int FADE_DURATION = 20;
    
    private int displayTicks = 0;
    private float hue = 0f;

    private Plate13SoundEvent() {}

    public static void triggerPlateEvent() {
        INSTANCE.displayTicks = DURATION_TICKS;
        INSTANCE.hue = 0f;
        playSound();
    }

    private static void playSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(
                new PositionedSoundRecord(
                        SoundEvents.RECORD_13.getSoundName(),
                        SoundCategory.MUSIC,
                        1.0F,
                        1.0F,
                        false,
                        0,
                        ISound.AttenuationType.NONE,
                        0.0F,
                        0.0F,
                        0.0F
                )
        );
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && displayTicks > 0) {
            displayTicks--;
            hue += 0.005f;
            if (hue >= 1f) hue = 0f;
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (displayTicks <= 0) return;
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRenderer;
        ScaledResolution res = new ScaledResolution(mc);

        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();

        float alpha = MathHelper.clamp(
            Math.min(displayTicks / (float)FADE_DURATION, 1.0f), 
            0.0f, 
            1.0f
        );

        Color color = Color.getHSBColor(hue, 0.8f, 1f);
        int textColor = new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            (int)(alpha * 255)
        ).getRGB();

        int shadowColor = new Color(0.1f, 0.1f, 0.1f, alpha * 0.8f).getRGB();

        int textWidth = fr.getStringWidth(TEXT);
        int posX = (screenWidth - textWidth) / 2;
        int posY = screenHeight - 60;

        renderTextWithEffects(fr, TEXT, posX, posY, textColor, shadowColor);
    }

    private void renderTextWithEffects(FontRenderer fr, String text, int x, int y, int color, int shadowColor) {
        fr.drawString(text, x + 1, y + 1, shadowColor);
        fr.drawString(text, x, y, color);
    }
}