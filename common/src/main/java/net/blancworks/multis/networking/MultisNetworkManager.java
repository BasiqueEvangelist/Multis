package net.blancworks.multis.networking;

import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.event.events.PlayerEvent;
import me.shedaniel.architectury.networking.NetworkManager;
import net.blancworks.multis.resources.MultisResource;
import net.blancworks.multis.resources.MultisResourcePack;
import net.blancworks.multis.resources.MultisResourceType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;


public class MultisNetworkManager {

    private static final Queue<PacketByteBuf> bufferPool = new LinkedList<>();

    private static final Identifier CLIENT_REQUEST_RESOURCE_ID = new Identifier("multis", "request_resource");
    private static final Identifier SERVER_SUPPLY_RESOURCE_ID = new Identifier("multis", "supply_resource");

    public static final List<ServerPlayerEntity> all_players = new ArrayList<>();


    private static PacketByteBuf getBuffer() {
        if (bufferPool.size() == 0)
            return new PacketByteBuf(Unpooled.buffer());
        return bufferPool.poll();
    }

    public static void client_init() {
        NetworkManager.registerReceiver(NetworkManager.serverToClient(), SERVER_SUPPLY_RESOURCE_ID, MultisNetworkManager::clientReceiveResourceFromServer);
        PlayerEvent.PLAYER_JOIN.register(MultisNetworkManager::onPlayerJoin);
        PlayerEvent.PLAYER_QUIT.register(all_players::remove);
    }

    public static void server_init() {
        PlayerEvent.PLAYER_JOIN.register(MultisNetworkManager::onPlayerJoin);
        PlayerEvent.PLAYER_QUIT.register(all_players::remove);
    }

    private static void onPlayerJoin(ServerPlayerEntity player) {
        all_players.add(player);

        supplyAllResourcesToPlayer(player);
    }

    private static void supplyAllResourcesToPlayer(ServerPlayerEntity player) {
        CompletableFuture.runAsync(() -> {
            MultisResourcePack.scriptSet.forEach((id, r) -> {
                supplyResourceToPlayer(player, MultisResourceType.Script, id);
            });
        });
    }

    /**
     * Finds a matching resource and sends it to the player.
     *
     * @param target The player to send the resource to.
     * @param type   The type of resource it is.
     * @param id     The ID of the resource.
     */
    public static void supplyResourceToPlayer(ServerPlayerEntity target, MultisResourceType type, Identifier id) {
        MultisResource resource = MultisResourcePack.getResource(type, id);

        if (resource == null) return;

        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());

        buffer.writeIdentifier(id);
        buffer.writeInt(type.ordinal());
        resource.writeToPacket(buffer);

        NetworkManager.sendToPlayer(target, SERVER_SUPPLY_RESOURCE_ID, buffer);
        System.out.println("Sending resource " + id + " to player " + target.getName().asString());
    }

    public static void supplyResourceToAllPlayers(MultisResourceType type, Identifier id) {
        MultisResource resource = MultisResourcePack.getResource(type, id);

        if (resource == null) return;

        PacketByteBuf buffer = getBuffer();

        buffer.writeIdentifier(id);
        buffer.writeInt(type.ordinal());
        resource.writeToPacket(buffer);

        NetworkManager.sendToPlayers(all_players, SERVER_SUPPLY_RESOURCE_ID, buffer);
    }

    public static void clientReceiveResourceFromServer(PacketByteBuf packetByteBuf, NetworkManager.PacketContext packetContext) {
        Identifier id = packetByteBuf.readIdentifier();
        MultisResourceType type = MultisResourceType.values()[packetByteBuf.readInt()];
        System.out.println("Got resource " + id + " to player " + packetContext.getPlayer().getName().asString());
    }
}