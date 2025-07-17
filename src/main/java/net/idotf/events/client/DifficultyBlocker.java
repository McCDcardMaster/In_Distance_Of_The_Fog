package net.idotf.events.client;

import net.minecraft.command.CommandDifficulty;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DifficultyBlocker {

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            forcePeaceful(event.world);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWorldLoad(WorldEvent.Load event) {
        forcePeaceful(event.getWorld());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommand(CommandEvent event) {
        if (event.getCommand() instanceof CommandDifficulty) {
            event.setCanceled(true);
            if (event.getSender().getEntityWorld() != null) {
                forcePeaceful(event.getSender().getEntityWorld());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuiAction(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.getButton().id == 103 || event.getButton().id == 104) { // ID кнопок сложности
            forcePeaceful(net.minecraft.client.Minecraft.getMinecraft().world);
        }
    }

    private void forcePeaceful(net.minecraft.world.World world) {
        if (world != null && world.getWorldInfo().getDifficulty() != EnumDifficulty.PEACEFUL) {
            world.getWorldInfo().setDifficulty(EnumDifficulty.PEACEFUL);
            world.getWorldInfo().setDifficultyLocked(true);
        }
    }
}