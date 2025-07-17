package net.idotf.entity;

import net.idotf.events.client.HerobrineSpawn;
import net.idotf.events.client.GlitchEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class herobrine extends EntityLiving {
    private int despawnTimer = 175;
    private boolean eventTriggered = false;
    private boolean soundPlayed = false;

    public herobrine(World world) {
        super(world);
        this.setSize(0.5F, 1.75F);
        this.experienceValue = 0;
        this.setEntityInvulnerable(true);
        this.setNoGravity(false);

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 40.0F));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote && despawnTimer-- <= 0) {
            this.setDead();
            return;
        }

        if (!world.isRemote && this.ticksExisted == 1 && !soundPlayed) {
            if (this.rand.nextFloat() < 0.5F) {
                HerobrineSpawn.playSound();
            }
            soundPlayed = true;
        }

        EntityPlayer nearestPlayer = this.world.getClosestPlayerToEntity(this, 40.0D);
        if (nearestPlayer != null) {
            double distance = this.getDistance(nearestPlayer);

            if (distance < 7.0 && !eventTriggered) {
                GlitchEvent.startGlitchEffect();
                eventTriggered = true;
                this.setDead();
                return;
            } else if (distance >= 7.0) {
                eventTriggered = false;
            }

            this.getLookHelper().setLookPositionWithEntity(
                    nearestPlayer,
                    30.0F,
                    30.0F
            );
        }

        this.motionX = 0;
        this.motionZ = 0;
    }
    private float updateRotation(float current, float target, float maxStep) {
        float angle = MathHelper.wrapDegrees(target - current);
        angle = MathHelper.clamp(angle, -maxStep, maxStep);
        return current + angle;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity entity) {}

    @Override
    public boolean isAIDisabled() {
        return false;
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, net.minecraft.block.state.IBlockState state, net.minecraft.util.math.BlockPos pos) {
        super.updateFallState(y, onGroundIn, state, pos);
    }
}