package net.idotf.events.client;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;

public class HitPlayerEvent {
    private static final float DAMAGE_AMOUNT = 10.0F;

    public static void deductHealth(EntityPlayerMP player) {
        if (player.world.isRemote) return;

        float currentHealth = player.getHealth();

        float newHealth = Math.max(0.0F, currentHealth - DAMAGE_AMOUNT);

        player.setHealth(newHealth);

        if (newHealth <= 0.0F) {
            player.onDeath(DamageSource.GENERIC);
        }
    }
}