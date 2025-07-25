package net.idotf.events.soundevents;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@SideOnly(Side.CLIENT)
public class HerobrineSpawnSoundEvent {
    private static final ResourceLocation SOUND_PATH =
            new ResourceLocation("idotf", "sounds/herobrine/herobrine_spawn.mp3");

    private static Player audioPlayer;
    private static Thread playerThread;
    private static volatile boolean isPlaying = false;

    public static void play() {
        if (isPlaying || Minecraft.getMinecraft().world == null) return;

        try {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(SOUND_PATH);
            InputStream inputStream = new BufferedInputStream(resource.getInputStream());

            playerThread = new Thread(() -> {
                try (InputStream stream = inputStream) {
                    isPlaying = true;
                    audioPlayer = new Player(stream);
                    audioPlayer.play();
                } catch (JavaLayerException | IOException e) {
                    e.printStackTrace();
                } finally {
                    closeResources();
                }
            });

            playerThread.start();

        } catch (IOException e) {
            System.err.println("Sound file not found: " + SOUND_PATH);
        }
    }

    public static void stop() {
        closeResources();
        if (playerThread != null && playerThread.isAlive()) {
            playerThread.interrupt();
        }
    }

    private static void closeResources() {
        if (audioPlayer != null) {
            audioPlayer.close();
            audioPlayer = null;
        }
        isPlaying = false;
    }

    public static boolean isPlaying() {
        return isPlaying;
    }
}