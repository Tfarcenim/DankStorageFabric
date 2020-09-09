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


public class C2SMessagePickBlock implements PacketConsumer
{

    public static void send(int slot)
    {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(slot);
        ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.pick_block, buf);
    }

    public void handle(PacketContext ctx, int slot)
    {
        Player player = ctx.getPlayer();
        if (player.getMainHandItem().getItem() instanceof DankItem)
            Utils.setSelectedSlot(player.getMainHandItem(), slot);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            Utils.setSelectedSlot(player.getOffhandItem(), slot);
    }

    @Override
    public void accept(PacketContext packetContext, FriendlyByteBuf packetByteBuf)
    {
        int slot = packetByteBuf.readInt();
        packetContext.getTaskQueue().execute(() -> handle(packetContext, slot));
    }
}
