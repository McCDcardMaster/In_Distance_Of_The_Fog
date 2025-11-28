package net.idotf.events.client.renderdistance;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderDistance {

    private static final float FOG_START = 0.1f;
    private static final float FOG_END = 16.0f;
    private static final float[] DAY_FOG_COLOR = {0.58f, 0.82f, 0.97f};
    private static final float[] NIGHT_FOG_COLOR = {0.01f, 0.01f, 0.02f};

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new RenderDistance());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        Entity entity = event.getEntity();
        World world = entity.world;

        if (world == null) return;

        if (!entity.isInWater() && !entity.isInLava()) {
            float celestialAngle = world.getCelestialAngle(1.0f);
            boolean isDay = celestialAngle < 0.25f || celestialAngle > 0.75f;

            if (isDay) {
                event.setRed(DAY_FOG_COLOR[0]);
                event.setGreen(DAY_FOG_COLOR[1]);
                event.setBlue(DAY_FOG_COLOR[2]);
            } else {
                event.setRed(NIGHT_FOG_COLOR[0]);
                event.setGreen(NIGHT_FOG_COLOR[1]);
                event.setBlue(NIGHT_FOG_COLOR[2]);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onFogRender(EntityViewRenderEvent.RenderFogEvent event) {
        Entity entity = event.getEntity();

        if (!entity.isInWater() && !entity.isInLava()) {
            GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
            GlStateManager.setFogStart(FOG_START);
            GlStateManager.setFogEnd(FOG_END);

            GlStateManager.disableFog();
            GlStateManager.enableFog();
        }
    }

    public static int applyAlphaFog(int color, Vec3d position) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null) return color;

        float distance = (float) position.lengthVector();
        if (distance > FOG_END) return 0;

        float celestialAngle = mc.world.getCelestialAngle(1.0f);
        boolean isDay = celestialAngle < 0.25f || celestialAngle > 0.75f;
        float[] fogColor = isDay ? DAY_FOG_COLOR : NIGHT_FOG_COLOR;

        float factor = Math.min(1.0f, distance / FOG_END);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int fr = (int) (fogColor[0] * 255);
        int fg = (int) (fogColor[1] * 255);
        int fb = (int) (fogColor[2] * 255);

        int blendedR = (int) (r * (1 - factor) + fr * factor);
        int blendedG = (int) (g * (1 - factor) + fg * factor);
        int blendedB = (int) (b * (1 - factor) + fb * factor);

        return 0xFF << 24 | blendedR << 16 | blendedG << 8 | blendedB;
    }
}
