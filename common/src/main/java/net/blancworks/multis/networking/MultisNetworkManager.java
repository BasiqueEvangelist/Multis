package net.blancworks.multis.networking;

import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.event.events.PlayerEvent;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class MultisNetworkManager {

    private static final Queue<PacketByteBuf> bufferPool = new LinkedList<>();

    public static final List<ServerPlayerEntity> all_players = new ArrayList<>();


    private static PacketByteBuf getBuffer() {
        if (bufferPool.size() == 0)
            return new PacketByteBuf(Unpooled.buffer());
        return bufferPool.poll();
    }

    public static void client_init() {
        PlayerEvent.PLAYER_JOIN.register(MultisNetworkManager::onPlayerJoin);
        PlayerEvent.PLAYER_QUIT.register(all_players::remove);
    }

    public static void server_init() {
        PlayerEvent.PLAYER_JOIN.register(MultisNetworkManager::onPlayerJoin);
        PlayerEvent.PLAYER_QUIT.register(all_players::remove);
    }

    private static void onPlayerJoin(ServerPlayerEntity player) {
        all_players.add(player);
    }


}