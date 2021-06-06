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
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;

public class C2SMessageTogglePickup implements ServerPlayNetworking.PlayChannelHandler {

    public static void send() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(DankPacketHandler.toggle_pickup, buf);
    }

    public void handle(ServerPlayer player) {
        ItemStack bag = player.getMainHandItem();
        System.out.println(bag);
        if (!(bag.getItem() instanceof DankItem)) {
            bag = player.getOffhandItem();
            if (!(bag.getItem() instanceof DankItem)) return;
        }
        Utils.cycleMode(bag, player);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        server.execute(() -> handle(player));
    }
}

