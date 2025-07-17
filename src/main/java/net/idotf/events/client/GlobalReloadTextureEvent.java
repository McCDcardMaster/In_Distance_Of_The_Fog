package net.idotf.events.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class GlobalReloadTextureEvent {
    public static boolean distortionEnabled = false;
    public static int distortionMode = 0;
    private static final Random random = new Random();
    private static final Field TEXTURE_MAP_FIELD = getTextureMapField();

    private static Field getTextureMapField() {
        String[] possibleFieldNames = {"field_110585_a", "textureMap", "a"};
        for (String fieldName : possibleFieldNames) {
            try {
                Field field = TextureManager.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {}
        }
        System.err.println("[TextureDistortion] Failed to find textureMap field!");
        return null;
    }

    public static void applyDistortion(int mode) {
        distortionMode = mode;
        distortionEnabled = true;
        
        if (TEXTURE_MAP_FIELD == null) return;
        
        try {
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            Map<ResourceLocation, ITextureObject> textureMap = (Map<ResourceLocation, ITextureObject>) TEXTURE_MAP_FIELD.get(textureManager);
            
            for (Map.Entry<ResourceLocation, ITextureObject> entry : textureMap.entrySet()) {
                ITextureObject textureObj = entry.getValue();
                if (textureObj instanceof AbstractTexture) {
                    processTexture(entry.getKey(), (AbstractTexture) textureObj);
                }
            }
        } catch (Exception e) {
            System.err.println("[TextureDistortion] Error during texture processing: ");
            e.printStackTrace();
        }
    }

    private static void processTexture(ResourceLocation location, AbstractTexture texture) {
        try {
            BufferedImage original = readTextureFromGPU(texture);
            if (original == null) {
                original = loadTextureFromResources(location);
            }
            BufferedImage distorted = applyDistortionEffect(original);
            loadTexture(texture, distorted);
        } catch (Exception e) {
            System.err.println("[TextureDistortion] Failed: " + location);
            e.printStackTrace();
        }
    }

    private static BufferedImage loadTextureFromResources(ResourceLocation location) throws IOException {
        IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
        try (InputStream is = manager.getResource(location).getInputStream()) {
            return ImageIO.read(is);
        }
    }

    private static BufferedImage readTextureFromGPU(AbstractTexture texture) {
        int textureId = texture.getGlTextureId();
        if (textureId == -1) return null;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        if (width <= 0 || height <= 0) return null;

        IntBuffer buffer = GLAllocation.createDirectIntBuffer(width * height);
        int[] pixels = new int[width * height];

        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
        buffer.get(pixels);

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            pixels[i] = (pixel & 0xFF00FF00) | ((pixel & 0x00FF0000) >> 16) | ((pixel & 0x000000FF) << 16);
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }

    private static BufferedImage applyDistortionEffect(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage distorted = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = original.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF;
                if (alpha == 0) {
                    distorted.setRGB(x, y, argb);
                    continue;
                }

                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                switch (distortionMode) {
                    case 1: g = 0; break; // Purple tint
                    case 2:          // Chaos mode
                        r = random.nextInt(256);
                        g = random.nextInt(256);
                        b = random.nextInt(256);
                        break;
                }
                distorted.setRGB(x, y, (alpha << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return distorted;
    }

    private static void loadTexture(AbstractTexture texture, BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        TextureUtil.uploadTexture(texture.getGlTextureId(), pixels, width, height);
    }
}