package net.idotf.events.client;

import net.idotf.entity.herobrine;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import java.util.List;
import java.util.Random;

public class TimerEvent {
    private static long accumulatedTime = 0;
    private static long lastUpdateTime = -1;
    private static final long DELAY = 10 * 60 * 1000;
    private static final Random random = new Random();

    private enum Events {
        HEROBRINE_SPAWN,
        GLITCH_EVENT,
        TABLE_SPAWN,
        ITEM_REMOVE,
        PLAYER_FIRE_EVENT,
        HEALTH_REDUCE_EVENT
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new TimerEvent());
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        if (event.phase == ServerTickEvent.Phase.END) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server == null) return;

            long currentTime = System.currentTimeMillis();

            if (lastUpdateTime == -1) {
                lastUpdateTime = currentTime;
            } else {
                accumulatedTime += currentTime - lastUpdateTime;
                lastUpdateTime = currentTime;

                if (accumulatedTime >= DELAY) {
                    triggerRandomEvent(server);
                    accumulatedTime = 0;
                }
            }
        }
    }

    public static void triggerRandomEvent(MinecraftServer server) {
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        if (players.isEmpty()) return;

        EntityPlayerMP target = players.get(random.nextInt(players.size()));
        Events selected = Events.values()[random.nextInt(Events.values().length)];

        switch(selected) {
            case HEROBRINE_SPAWN:
                spawnHerobrine(target);
                break;
            case GLITCH_EVENT:
                GlitchEvent.startGlitchEffect();
                break;
            case TABLE_SPAWN:
                TableSpawnEvent.spawnSign(target);
                break;
            case ITEM_REMOVE:
                RemoveItemsEvent.removeRandomItem(target);
                break;
            case PLAYER_FIRE_EVENT:
                FirePlayerEvent.ignitePlayer(target);
                break;
            case HEALTH_REDUCE_EVENT:
                HitPlayerEvent.deductHealth(target);
                break;
        }
    }

    private static void spawnHerobrine(EntityPlayerMP player) {
        World world = player.world;
        if (world == null) return;

        double angle = Math.toRadians(player.rotationYaw + 90);
        double x = player.posX + Math.cos(angle) * 13;
        double z = player.posZ + Math.sin(angle) * 13;
        double y = player.posY;
        
        herobrine entity = new herobrine(world);
        entity.setPosition(x, y, z);
        
        world.spawnEntity(entity);
    }
}