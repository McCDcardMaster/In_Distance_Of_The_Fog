package net.idotf.events.structures;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class AirTableStructure {
    private static final int SPAWN_RADIUS = 13;
    private static final int MIN_HEIGHT = 13;
    private static final int MAX_HEIGHT = 15;

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer) || event.getWorld().isRemote) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntity();
        spawnStructure(player.world, player.getPosition());
    }

    public static void spawnStructure(World world, BlockPos playerPos) {
        Random rand = new Random();

        int offsetX = rand.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS;
        int offsetZ = rand.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS;
        int height = playerPos.getY() + MIN_HEIGHT + rand.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1);

        BlockPos structurePos = new BlockPos(
                playerPos.getX() + offsetX,
                height,
                playerPos.getZ() + offsetZ
        );

        world.setBlockState(structurePos, Blocks.NETHERRACK.getDefaultState());

        BlockPos signPos = structurePos.up();

        world.setBlockState(signPos, Blocks.STANDING_SIGN.getDefaultState()
                .withProperty(BlockStandingSign.ROTATION, rand.nextInt(16)));

        if (world.getTileEntity(signPos) instanceof TileEntitySign) {
            TileEntitySign sign = (TileEntitySign) world.getTileEntity(signPos);
            sign.signText[0] = new TextComponentString("");
            sign.signText[1] = new TextComponentString("NULL");
            sign.signText[1].getStyle().setColor(TextFormatting.RED);
            sign.signText[2] = new TextComponentString("");
            sign.signText[3] = new TextComponentString("");
            sign.markDirty();
        }
    }
}