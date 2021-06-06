package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;


public class C2SMessagePickBlock implements ServerPlayNetworking.PlayChannelHandler {

    public static void send(int slot) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(slot);
        ClientPlayNetworking.send(DankPacketHandler.pick_block, buf);
    }

    public void handle(ServerPlayer player, int slot) {
        if (player.getMainHandItem().getItem() instanceof DankItem)
            Utils.setSelectedSlot(player.getMainHandItem(), slot);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            Utils.setSelectedSlot(player.getOffhandItem(), slot);
    }


    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int slot = buf.readInt();
        server.execute(() -> handle(player, slot));
    }
}
