package net.idotf.events.structures;

import net.minecraft.block.BlockLeaves;
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

public class DeadForestStructure {
    private static final float DEAD_TREE_CHANCE = 0.55f;
    private static final int LEAF_CHECK_RADIUS = 7;
    private static final int MAX_ATTEMPTS = 3;

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
        if (!canSustainSapling(world, pos)) return;

        WorldGenAbstractTree treeGen = biome.getRandomTreeFeature(rand);
        treeGen.generate(world, rand, pos);
    }

    private void generateDeadTree(World world, Random rand, BlockPos originalPos, Biome biome) {
        BlockPos spawnPos = originalPos;
        int attempts = 0;
        
        while (hasNearbyLeaves(world, spawnPos) && attempts < MAX_ATTEMPTS) {
            int offsetX = rand.nextInt(5) - 2;
            int offsetZ = rand.nextInt(5) - 2;
            spawnPos = world.getHeight(new BlockPos(
                originalPos.getX() + offsetX,
                0,
                originalPos.getZ() + offsetZ
            ));
            attempts++;
        }

        if (!canSustainSapling(world, spawnPos)) return;

        IBlockState logState = getLogStateForBiome(biome, rand);

        int height = 4 + rand.nextInt(3);
        int brokenHeight = Math.max(1, height - rand.nextInt(2));

        for (int i = 0; i < brokenHeight; i++) {
            BlockPos trunkPos = spawnPos.up(i);
            if (world.isAirBlock(trunkPos)) {
                world.setBlockState(trunkPos, logState, 2);
            }
        }
    }

    private boolean hasNearbyLeaves(World world, BlockPos pos) {
        for (int dx = -LEAF_CHECK_RADIUS; dx <= LEAF_CHECK_RADIUS; dx++) {
            for (int dy = -2; dy <= 6; dy++) {
                for (int dz = -LEAF_CHECK_RADIUS; dz <= LEAF_CHECK_RADIUS; dz++) {
                    BlockPos checkPos = pos.add(dx, dy, dz);
                    IBlockState state = world.getBlockState(checkPos);
                    
                    if (state.getBlock() instanceof BlockLeaves) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canSustainSapling(World world, BlockPos pos) {
        return world.getBlockState(pos.down()).getBlock().canSustainPlant(
            world.getBlockState(pos.down()), world, pos.down(),
            net.minecraft.util.EnumFacing.UP,
            (net.minecraft.block.BlockSapling) Blocks.SAPLING
        );
    }

    private IBlockState getLogStateForBiome(Biome biome, Random rand) {
        if (biome instanceof BiomeForest) {
            BiomeForest forestBiome = (BiomeForest) biome;
            
            String biomeName = biome.getRegistryName().toString().toLowerCase();
            
            if (biomeName.contains("birch") || biomeName.contains("роща")) {
                return Blocks.LOG.getDefaultState().withProperty(
                    BlockOldLog.VARIANT, 
                    BlockPlanks.EnumType.BIRCH
                );
            }
            
            if (biomeName.contains("roofed") || biomeName.contains("темный") || biomeName.contains("dark")) {
                return Blocks.LOG2.getDefaultState().withProperty(
                    BlockOldLog.VARIANT, 
                    BlockPlanks.EnumType.DARK_OAK
                );
            }
            
            if (biomeName.contains("forest") || biomeName.contains("лес")) {
                if (rand.nextBoolean()) {
                    return Blocks.LOG.getDefaultState().withProperty(
                        BlockOldLog.VARIANT, 
                        BlockPlanks.EnumType.OAK
                    );
                } else {
                    return Blocks.LOG.getDefaultState().withProperty(
                        BlockOldLog.VARIANT, 
                        BlockPlanks.EnumType.BIRCH
                    );
                }
            }
        }
        
        return Blocks.LOG.getDefaultState().withProperty(
            BlockOldLog.VARIANT, 
            BlockPlanks.EnumType.OAK
        );
    }
}