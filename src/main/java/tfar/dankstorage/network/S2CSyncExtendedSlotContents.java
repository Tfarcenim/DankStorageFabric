package tfar.dankstorage.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import tfar.dankstorage.container.AbstractDankContainer;
import tfar.dankstorage.utils.PacketBufferEX;

public class S2CSyncExtendedSlotContents implements PacketConsumer {

  public static void send(PlayerEntity player, int id,int slot,ItemStack stack) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(id);
    buf.writeInt(slot);
    PacketBufferEX.writeExtendedItemStack(buf,stack);
    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, DankPacketHandler.sync_stacks, buf);
  }

    public void handle(PacketContext ctx, int windowId, int slot, ItemStack stack) {
    PlayerEntity player = ctx.getPlayer();
        if (player.currentScreenHandler instanceof AbstractDankContainer && windowId == player.currentScreenHandler.syncId) {
          player.currentScreenHandler.slots.get(slot).setStack(stack);
        }
    }

  @Override
  public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
    int windowId = packetByteBuf.readInt();
    int slot = packetByteBuf.readInt();
    ItemStack stack = PacketBufferEX.readExtendedItemStack(packetByteBuf);
    packetContext.getTaskQueue().execute(() -> handle(packetContext,windowId,slot,stack));
  }
}