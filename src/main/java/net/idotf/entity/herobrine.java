package net.idotf.entity;

import net.idotf.events.soundevents.HerobrineSpawnSoundEvent;
import net.idotf.events.client.GlitchEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;

import java.util.Random;

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
                HerobrineSpawnSoundEvent.play();
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

    @Override
    public void setDead() {
        if (!this.world.isRemote) {
            if (this.rand.nextFloat() < 0.25F) {
                BlockPos pos = new BlockPos(
                        MathHelper.floor(this.posX),
                        MathHelper.floor(this.posY),
                        MathHelper.floor(this.posZ)
                );

                switch (this.rand.nextInt(3)) {
                    case 0:
                        world.setBlockState(pos, Blocks.REDSTONE_TORCH.getDefaultState());
                        break;
                    case 1:
                        world.setBlockState(pos, Blocks.REDSTONE_WIRE.getDefaultState());
                        break;
                    case 2:
                        world.setBlockState(pos, Blocks.RED_FLOWER.getStateFromMeta(0));
                        break;
                }
            }
        }
        super.setDead();
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
    public static void spawnHerobrine(EntityPlayerMP player) {
        World world = player.world;
        if (world == null) return;

        Random rand = new Random();
        int direction = rand.nextInt(4);
        double offset = rand.nextDouble() * 20.0;

        double px = player.posX;
        double py = player.posY;
        double pz = player.posZ;

        double x = px;
        double z = pz;
        double y = py + 10.0;

        switch(direction) {
            case 0: // Север
                z += 26.0;
                x += rand.nextBoolean() ? offset : -offset;
                break;
            case 1:
                z -= 26.0;
                x += rand.nextBoolean() ? offset : -offset;
                break;
            case 2:
                x += 26.0;
                z += rand.nextBoolean() ? offset : -offset;
                break;
            case 3:
                x -= 26.0;
                z += rand.nextBoolean() ? offset : -offset;
                break;
        }

        int searchX = (int) Math.floor(x);
        int searchY = (int) Math.floor(y);
        int searchZ = (int) Math.floor(z);

        BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();
        searchPos.setPos(searchX, searchY, searchZ);

        boolean foundPosition = false;

        while (searchPos.getY() > 0) {
            if (!world.isAirBlock(searchPos)) {
                BlockPos surfacePos = new BlockPos(searchPos);
                BlockPos feetPos = surfacePos.up();
                BlockPos headPos = feetPos.up();

                if (world.isAirBlock(feetPos) && world.isAirBlock(headPos)) {
                    x = feetPos.getX() + 0.5;
                    y = feetPos.getY();
                    z = feetPos.getZ() + 0.5;
                    foundPosition = true;
                    break;
                }
            }
            searchPos.setY(searchPos.getY() - 1);
        }

        if (!foundPosition) {
            return;
        }

        herobrine entity = new herobrine(world);
        entity.setPosition(x, y, z);
        world.spawnEntity(entity);
    }
}