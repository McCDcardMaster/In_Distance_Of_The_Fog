package net.idotf.events.client;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TableSpawnEvent {
    private static final Random random = new Random();
    private static final String[][] MESSAGES = {
            {"\u00A7cERROR 404", "$av3mys0ul() ", "\u00A7cERROR", "\u00A7cERROR"},
            {"Vem ar jag?!", "\u00A7cERROR", "ar jag...", "\u00A7cERROR"},
            {"Varfor", "ar jag har?!", "\u00A7cERROR ", ""},
            {"\u00A7cERROR", "Vad gr ", "jag har?!", "\u00A7cERROR"},
            {"\u00A7cERROR", "Vad?!", "\u00A7cERROR", "\u00A7cERROR"},
            {"\u00A7cERROR", "DENIAL", "\u00A7cERROR", "\u00A7cERROR"},
            {"Du!", "", "", ""},
            {"Hur herliga", "ar dessa", "torturer!", "Kanner du dem?"},
            {"Du ar ingen!", "", "", ""},
            {"Jag har dig \u00A7c:)", "", "", ""},
            {"Jag ar nara dig", "\u00A7c:)", "", ""},
            {"\u00A7cERROR 404", "exit()", "\u00A7cERROR", "\u00A7cERROR"},
			{"\u00A7c|)13!!!!", "\u00A7c|)13 !!!", "\u00A7c|)13!!!!", ""},
			{"ko?...", "", "", ""},
			{"gri... z..?", "", "", ""},
			{"hen?...", "", "", ""},
			{"Hjalp mig!", "", "", ""},
			{"Slapp in mig!", "Du ar min van,", "eller hur?", ""},
			{"Valkommen!", "till helvetet!", "", ""},
			{"!?", "\u00A7cERROR", "\u00A7cERROR", "\u00A7cERROR"},
			{"Nej...", "\u00A7cERROR", "\u00A7cNEJ!", "\u00A7cERROR"},
			{"!!!", "\u00A7cERROR", "\u00A7cERROR", "\u00A7cERROR"},
			{"Jag ventade", "pa dig", "", ""},
			{"Hej kompis!", "", "", ""},
			{"Halla!", "", "", ""},
			{"Hej!", "", "", ""},
            {"mrkre...", "ar mrkre...", "i mrer..", "mig mrkret..."},
            {"ljud!?..."}
    };
    private static final List<String> PROTECTED_WORDS = Arrays.asList("\u00A7cERROR", "\u00A7c:)", "exit()", "\u00A7c|)13!!!!", "\u00A7c|)13 !!!");

    public static void spawnSign(EntityPlayer player) {
        if (player == null || player.world.isRemote) return;

        World world = player.world;
        BlockPos playerPos = player.getPosition();

        int x = playerPos.getX() + random.nextInt(16) - 8;
        int z = playerPos.getZ() + random.nextInt(16) - 8;

        BlockPos signPos = findValidPosition(world, x, z);

        if (signPos != null && world.isBlockLoaded(signPos)) {
            IBlockState signState = Blocks.STANDING_SIGN.getDefaultState()
                    .withProperty(BlockStandingSign.ROTATION, random.nextInt(16));

            world.setBlockState(signPos, signState, 3);

            TileEntitySign sign = (TileEntitySign) world.getTileEntity(signPos);
            if (sign != null) {
                String[] message = MESSAGES[random.nextInt(MESSAGES.length)];
                for (int i = 0; i < 4; i++) {
                    String line = i < message.length ? processString(message[i]) : "";
                    sign.signText[i] = new TextComponentString(line);
                }
                sign.markDirty();
                world.notifyBlockUpdate(signPos, signState, signState, 3);
            }
        }
    }

    private static String processString(String original) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < original.length()) {
            boolean found = false;
            for (String word : PROTECTED_WORDS) {
                if (original.startsWith(word, i)) {
                    result.append(word);
                    i += word.length();
                    found = true;
                    break;
                }
            }
            if (!found) {
                char c = original.charAt(i);
                if (Character.isLetter(c) && random.nextFloat() < 0.3f) {
                    result.append(random.nextBoolean() ? '?' : '#');
                } else {
                    result.append(c);
                }
                i++;
            }
        }
        return result.toString();
    }

    private static BlockPos findValidPosition(World world, int x, int z) {
        for (int y = world.getActualHeight() - 1; y > 0; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockPos downPos = pos.down();

            if (world.isAirBlock(pos) &&
                    world.getBlockState(downPos).isSideSolid(world, downPos, net.minecraft.util.EnumFacing.UP)) {
                return pos;
            }
        }
        return null;
    }
}