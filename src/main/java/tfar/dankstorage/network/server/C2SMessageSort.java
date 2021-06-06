package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;

public class C2SMessageSort implements ServerPlayNetworking.PlayChannelHandler {

    public static void send() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(DankPacketHandler.sort, buf);
    }

    public void handle(ServerPlayer player) {
        Utils.sort(player);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        server.execute(() -> handle(player));
    }
}