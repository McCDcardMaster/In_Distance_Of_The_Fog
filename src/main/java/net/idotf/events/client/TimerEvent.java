package net.idotf.events.client;

import net.idotf.entity.herobrine;
import net.idotf.events.soundevents.Plate13SoundEvent;
import net.idotf.events.structures.AirTableStructure;
import net.idotf.events.structures.PyramidStructure;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Random;

public class TimerEvent {
    private static long accumulatedTime = 0;
    private static long lastUpdateTime = -1;
    private static final long DELAY = 10 * 60 * 1000;
    private static final Random random = new Random();
    public static final SimpleNetworkWrapper CHANNEL = new SimpleNetworkWrapper("EventChannel");

    private enum Events {
        PLATE_13_EVENT,
        HEROBRINE_SPAWN,
        GLITCH_EVENT,
        TABLE_SPAWN,
        PYRAMID_STRUCTURE,
        AIR_TABLE_SPAWN,
        ITEM_REMOVE,
        PLAYER_FIRE_EVENT,
        HEALTH_REDUCE_EVENT
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new TimerEvent());
        CHANNEL.registerMessage(TriggerPlateHandler.class, TriggerPlateMessage.class, 0, Side.CLIENT);
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
            case PLATE_13_EVENT:
                CHANNEL.sendTo(new TriggerPlateMessage(), target);
                break;
            case PYRAMID_STRUCTURE:
                PyramidStructure.generateNearPlayer(target.world, target.getPosition());
                break;
            case AIR_TABLE_SPAWN:
                AirTableStructure.spawnStructure(target.world, target.getPosition());
                break;
            case HEROBRINE_SPAWN:
                herobrine.spawnHerobrine(target);
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

    public static class TriggerPlateMessage implements IMessage {
        @Override public void toBytes(ByteBuf buf) {}
        @Override public void fromBytes(ByteBuf buf) {}
    }

    public static class TriggerPlateHandler implements IMessageHandler<TriggerPlateMessage, IMessage> {
        @Override
        public IMessage onMessage(TriggerPlateMessage message, MessageContext ctx) {
            Plate13SoundEvent.triggerPlateEvent();
            return null;
        }
    }
}