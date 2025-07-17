package net.idotf.events.generator;

import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class DeadForest {
    private static final float DEAD_TREE_CHANCE = 0.75f;

    @SubscribeEvent
    public void onTreeDecorate(DecorateBiomeEvent.Decorate event) {
        if (event.getType() != DecorateBiomeEvent.Decorate.EventType.TREE) return;

        World world = event.getWorld();
        Random rand = event.getRand();
        BlockPos basePos = event.getPos();
        Biome biome = world.getBiome(basePos);

        if (!(biome instanceof BiomeForest)) return;

        event.setResult(Result.DENY);

        int treeCount = ((BiomeForest) biome).decorator.treesPerChunk + rand.nextInt(3);

        for (int i = 0; i < treeCount; i++) {
            int x = basePos.getX() + rand.nextInt(16) + 8;
            int z = basePos.getZ() + rand.nextInt(16) + 8;
            BlockPos surfacePos = world.getHeight(new BlockPos(x, 0, z));

            if (rand.nextFloat() < DEAD_TREE_CHANCE) {
                generateDeadTree(world, rand, surfacePos, biome);
            } else {
                generateVanillaTree(world, rand, surfacePos, biome);
            }
        }
    }

    private void generateVanillaTree(World world, Random rand, BlockPos pos, Biome biome) {
        if (!world.getBlockState(pos.down()).getBlock().canSustainPlant(
                world.getBlockState(pos.down()), world, pos.down(),
                net.minecraft.util.EnumFacing.UP,
                (net.minecraft.block.BlockSapling) Blocks.SAPLING
        )) return;

        WorldGenAbstractTree treeGen = biome.getRandomTreeFeature(rand);
        treeGen.generate(world, rand, pos);
    }

    private void generateDeadTree(World world, Random rand, BlockPos pos, Biome biome) {
        if (!world.getBlockState(pos.down()).getBlock().canSustainPlant(
                world.getBlockState(pos.down()), world, pos.down(),
                net.minecraft.util.EnumFacing.UP,
                (net.minecraft.block.BlockSapling) Blocks.SAPLING
        )) return;

        IBlockState logState = getLogStateForBiome(biome, rand);

        int height = 4 + rand.nextInt(3);
        int brokenHeight = Math.max(1, height - rand.nextInt(2));

        for (int i = 0; i < brokenHeight; i++) {
            BlockPos trunkPos = pos.up(i);
            if (world.isAirBlock(trunkPos)) {
                world.setBlockState(trunkPos, logState, 2);
            }
        }
    }

    private IBlockState getLogStateForBiome(Biome biome, Random rand) {
        return Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
    }
}