package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;

public class C2SMessageScrollSlot implements PacketConsumer {

    public static void send(boolean right) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(right);
        ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.scroll, buf);
    }

    @Override
    public void accept(PacketContext packetContext, FriendlyByteBuf packetByteBuf) {
        boolean right = packetByteBuf.readBoolean();
        packetContext.getTaskQueue().execute(() -> handle(packetContext, right));
    }

    public void handle(PacketContext ctx, boolean right) {
        Player player = ctx.getPlayer();
        if (player.getMainHandItem().getItem() instanceof DankItem)
            Utils.changeSlot(player.getMainHandItem(), right);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            Utils.changeSlot(player.getOffhandItem(), right);
    }
}

