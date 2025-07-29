package net.idotf.events.soundevents;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class RandomMobsSounds {

    @SideOnly(Side.CLIENT)
    public static void playRandomSound() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;

        SoundEvent[] sounds = {
                SoundEvents.ENTITY_CREEPER_PRIMED,
                SoundEvents.ENTITY_ZOMBIE_AMBIENT,
                SoundEvents.ENTITY_SKELETON_AMBIENT,
                SoundEvents.ENTITY_SPIDER_AMBIENT
        };

        SoundEvent selectedSound = sounds[new Random().nextInt(sounds.length)];
        player.playSound(selectedSound, 1.0F, 1.0F);
    }
}