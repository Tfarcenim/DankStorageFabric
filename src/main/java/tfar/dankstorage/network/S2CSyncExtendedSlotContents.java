package tfar.dankstorage.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.utils.PacketBufferEX;

public class S2CSyncExtendedSlotContents implements PacketConsumer {

  public static void send(Player player, int id,int slot,ItemStack stack) {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    buf.writeInt(id);
    buf.writeInt(slot);
    PacketBufferEX.writeExtendedItemStack(buf,stack);
    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, DankPacketHandler.sync_stacks, buf);
  }

    public void handle(PacketContext ctx, int windowId, int slot, ItemStack stack) {
    Player player = ctx.getPlayer();
        if (player.containerMenu instanceof AbstractDankMenu && windowId == player.containerMenu.containerId) {
          player.containerMenu.slots.get(slot).set(stack);
        }
    }

  @Override
  public void accept(PacketContext packetContext, FriendlyByteBuf packetByteBuf) {
    int windowId = packetByteBuf.readInt();
    int slot = packetByteBuf.readInt();
    ItemStack stack = PacketBufferEX.readExtendedItemStack(packetByteBuf);
    packetContext.getTaskQueue().execute(() -> handle(packetContext,windowId,slot,stack));
  }
}