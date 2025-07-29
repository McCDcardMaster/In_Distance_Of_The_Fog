package net.idotf.events.client.renderdistance;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderDistance {

    private static final float FOG_START = 0.1f;
    private static final float FOG_END = 45.0f;
    private static final float[] DAY_FOG_COLOR = {0.58f, 0.82f, 0.97f};
    private static final float[] NIGHT_FOG_COLOR = {0.01f, 0.01f, 0.02f};
    private static final float[] CAVE_FOG_COLOR = {0.15f, 0.15f, 0.15f};
    private static final float CAVE_LIGHT_THRESHOLD = 0.1f;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new RenderDistance());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        Entity entity = event.getEntity();
        World world = entity.world;
        if (world == null) return;

        BlockPos eyePos = new BlockPos(
                entity.posX,
                entity.posY + entity.getEyeHeight(),
                entity.posZ
        );
        boolean hasBlockAbove = hasSolidBlockAbove(world, eyePos);
        boolean isCave = isInCave(world, eyePos);
        boolean applyGrayFog = hasBlockAbove || isCave;

        float celestialAngle = world.getCelestialAngle((float) event.getRenderPartialTicks());
        float[] baseFogColor = getBaseFogColor(celestialAngle);

        if (applyGrayFog) {
            float transitionFactor = calculateTransitionFactor(world, eyePos);
            event.setRed(transitionFactor * CAVE_FOG_COLOR[0] + (1 - transitionFactor) * baseFogColor[0]);
            event.setGreen(transitionFactor * CAVE_FOG_COLOR[1] + (1 - transitionFactor) * baseFogColor[1]);
            event.setBlue(transitionFactor * CAVE_FOG_COLOR[2] + (1 - transitionFactor) * baseFogColor[2]);
        } else {
            float dayNightFactor = calculateDayNightFactor(celestialAngle);
            event.setRed(dayNightFactor * DAY_FOG_COLOR[0] + (1 - dayNightFactor) * NIGHT_FOG_COLOR[0]);
            event.setGreen(dayNightFactor * DAY_FOG_COLOR[1] + (1 - dayNightFactor) * NIGHT_FOG_COLOR[1]);
            event.setBlue(dayNightFactor * DAY_FOG_COLOR[2] + (1 - dayNightFactor) * NIGHT_FOG_COLOR[2]);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onFogRender(EntityViewRenderEvent.RenderFogEvent event) {
        Entity entity = event.getEntity();

        GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
        GlStateManager.setFogStart(FOG_START);
        GlStateManager.setFogEnd(FOG_END);
    }

    private static boolean hasSolidBlockAbove(World world, BlockPos pos) {
        for (int y = 1; y <= 3; y++) {
            BlockPos checkPos = pos.up(y);
            if (!world.isAirBlock(checkPos)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInCave(World world, BlockPos pos) {
        float lightLevel = world.getLightBrightness(pos);
        return lightLevel < CAVE_LIGHT_THRESHOLD;
    }

    private static float calculateTransitionFactor(World world, BlockPos pos) {
        float factor = 0f;
        for (int y = 1; y <= 3; y++) {
            if (!world.isAirBlock(pos.up(y))) {
                factor += 0.3f;
            }
        }

        float lightFactor = 1.0f - MathHelper.clamp(
                world.getLightBrightness(pos) / CAVE_LIGHT_THRESHOLD,
                0f,
                1f
        );
        return MathHelper.clamp(factor + lightFactor * 0.5f, 0f, 1f);
    }

    private static float calculateDayNightFactor(float celestialAngle) {
        celestialAngle = celestialAngle % 1.0f;
        return MathHelper.clamp(
                (float) Math.cos(celestialAngle * Math.PI * 2) * 0.5f + 0.5f,
                0f,
                1f
        );
    }

    private static float[] getBaseFogColor(float celestialAngle) {
        float factor = calculateDayNightFactor(celestialAngle);
        return new float[]{
                factor * DAY_FOG_COLOR[0] + (1 - factor) * NIGHT_FOG_COLOR[0],
                factor * DAY_FOG_COLOR[1] + (1 - factor) * NIGHT_FOG_COLOR[1],
                factor * DAY_FOG_COLOR[2] + (1 - factor) * NIGHT_FOG_COLOR[2]
        };
    }

    public static int applyAlphaFog(int color, Vec3d position) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null) return color;

        float distance = (float) position.lengthVector();
        if (distance > FOG_END) return 0;

        BlockPos playerPos = new BlockPos(
                mc.player.posX,
                mc.player.posY + mc.player.getEyeHeight(),
                mc.player.posZ
        );

        World world = mc.world;
        float celestialAngle = world.getCelestialAngle(1.0f);
        float[] fogColor;

        if (hasSolidBlockAbove(world, playerPos) || isInCave(world, playerPos)) {
            fogColor = CAVE_FOG_COLOR;
        } else {
            float dayNightFactor = calculateDayNightFactor(celestialAngle);
            fogColor = new float[]{
                    dayNightFactor * DAY_FOG_COLOR[0] + (1 - dayNightFactor) * NIGHT_FOG_COLOR[0],
                    dayNightFactor * DAY_FOG_COLOR[1] + (1 - dayNightFactor) * NIGHT_FOG_COLOR[1],
                    dayNightFactor * DAY_FOG_COLOR[2] + (1 - dayNightFactor) * NIGHT_FOG_COLOR[2]
            };
        }

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