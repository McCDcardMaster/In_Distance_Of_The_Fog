package net.idotf.events.structures;

import java.util.Random;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDesert;

public class PyramidStructure {
    private static final IBlockState SAND = Blocks.SAND.getDefaultState();
    private static final IBlockState REDSTONE_TORCH = Blocks.REDSTONE_TORCH.getDefaultState();

    public static void generateInChunk(Random rand, World world, int chunkX, int chunkZ) {
        int x = chunkX * 16 + rand.nextInt(16);
        int z = chunkZ * 16 + rand.nextInt(16);
        BlockPos basePos = new BlockPos(x, 0, z);
        Biome biome = world.getBiome(basePos);

        if (!(biome instanceof BiomeDesert)) return;
        if (rand.nextInt(35) != 0) return;

        int y = world.getHeight(x, z) - 1;
        if (!world.getBlockState(new BlockPos(x, y, z)).isTopSolid()) return;

        generateStructure(world, new BlockPos(x, y, z));
    }

    public static void generateNearPlayer(World world, BlockPos playerPos) {
        Random rand = new Random();
        
        double angle = rand.nextDouble() * Math.PI * 2;
        double distance = 1 + rand.nextDouble() * 10;
        int offsetX = (int) Math.round(Math.cos(angle) * distance);
        int offsetZ = (int) Math.round(Math.sin(angle) * distance);
        
        BlockPos targetPos = playerPos.add(offsetX, 0, offsetZ);
        
        int surfaceY = world.getHeight(targetPos.getX(), targetPos.getZ()) - 1;
        BlockPos surfacePos = new BlockPos(targetPos.getX(), surfaceY, targetPos.getZ());
        
        generateStructure(world, surfacePos.up());
    }

    private static void generateStructure(World world, BlockPos basePos) {
        BlockSand.fallInstantly = true;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy <= 1; dy++) {
                    world.setBlockToAir(basePos.add(dx, dy, dz));
                }
            }
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.setBlockState(basePos.add(dx, 0, dz), SAND, 2);
            }
        }

        world.setBlockState(basePos, REDSTONE_TORCH, 2);
        world.setBlockState(basePos.add(0, 1, 0), SAND, 2);

        BlockSand.fallInstantly = false;
    }
}