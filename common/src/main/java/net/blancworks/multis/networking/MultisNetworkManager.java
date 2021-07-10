package net.blancworks.multis.networking;

import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.event.events.PlayerEvent;
import me.shedaniel.architectury.event.events.TickEvent;
import me.shedaniel.architectury.networking.NetworkManager;
import net.blancworks.multis.resources.MultisResource;
import net.blancworks.multis.resources.MultisResourceManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;


public class MultisNetworkManager {

    private static final Queue<PacketByteBuf> bufferPool = new LinkedList<>();

    public static final List<ServerPlayerEntity> all_players = new ArrayList<>();

    public static final HashMap<ServerPlayerEntity, Queue<Identifier>> playerAssetUpdateQueues = new HashMap<>();

    private static final int ASSETS_PER_TICK = 20;

    private static final Identifier S2C_PROVIDE_ASSET = new Identifier("multis", "s2c_provide_asset");

    private static PacketByteBuf getBuffer() {
        if (bufferPool.size() == 0)
            return new PacketByteBuf(Unpooled.buffer());
        return bufferPool.poll();
    }

    public static void client_init() {
        PlayerEvent.PLAYER_JOIN.register(MultisNetworkManager::onPlayerJoin);
        PlayerEvent.PLAYER_QUIT.register(MultisNetworkManager::onPlayerLeft);
        TickEvent.SERVER_POST.register(MultisNetworkManager::onTick);

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2C_PROVIDE_ASSET, MultisNetworkManager::clientReceiveAsset);
    }

    public static void server_init() {
        PlayerEvent.PLAYER_JOIN.register(MultisNetworkManager::onPlayerJoin);
        PlayerEvent.PLAYER_QUIT.register(MultisNetworkManager::onPlayerLeft);
        TickEvent.SERVER_POST.register(MultisNetworkManager::onTick);
    }

    private static synchronized void onTick(MinecraftServer minecraftServer) {
        for (Map.Entry<ServerPlayerEntity, Queue<Identifier>> entry : playerAssetUpdateQueues.entrySet()) {
            Queue<Identifier> queue = entry.getValue();
            ServerPlayerEntity pEnt = entry.getKey();
            for (int i = 0; i < ASSETS_PER_TICK && queue.size() > 0; i++) {

                Identifier id = queue.remove();

                System.out.println("SENDING ASSET OF ID " + id + " USING PACKET");

                PacketByteBuf pbb = getBuffer();

                if (MultisResourceManager.writeResourceToPacket(id, pbb)) {
                    NetworkManager.sendToPlayer(pEnt, S2C_PROVIDE_ASSET, pbb);
                }
            }
        }
    }

    private static synchronized void onPlayerJoin(ServerPlayerEntity player) {
        all_players.add(player);


        Queue<Identifier> idQueue = new LinkedList<>();
        playerAssetUpdateQueues.put(player, idQueue);

        MultisResourceManager.fillAssetQueue(idQueue);
    }

    private static synchronized void onPlayerLeft(ServerPlayerEntity player) {
        all_players.remove(player);

        playerAssetUpdateQueues.remove(player);
    }

    public static synchronized void onAssetUpdate(Identifier id) {
        for (Map.Entry<ServerPlayerEntity, Queue<Identifier>> entry : playerAssetUpdateQueues.entrySet()) {
            entry.getValue().add(id);
        }
    }

    public static synchronized void clientReceiveAsset(PacketByteBuf buf, NetworkManager.PacketContext ctx) {
        MultisResourceManager.readResourceFromPacket(buf);
    }
}