package net.idotf.events.client;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;

public class FirePlayerEvent {
    private static final int FIRE_DURATION = 20 * 10;

    public static void ignitePlayer(EntityPlayerMP player) {
        if (player.world.isRemote) return;

        if (!player.isImmuneToFire()) {
            player.setFire(FIRE_DURATION);
        } else {
            player.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
            player.setFire(1);
        }
    }
}