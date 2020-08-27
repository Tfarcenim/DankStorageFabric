package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.dankstorage.container.AbstractDankContainer;
import tfar.dankstorage.network.DankPacketHandler;

public class C2SMessageLockSlot implements PacketConsumer {

  public static void send(int slot) {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    buf.writeInt(slot);
    ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.lock_slot, buf);
  }

  @Override
  public void accept(PacketContext packetContext, FriendlyByteBuf packetByteBuf) {
    int slot = packetByteBuf.readInt();
    packetContext.getTaskQueue().execute(() -> handle(packetContext, slot));
  }

  public void handle(PacketContext ctx, int slot) {
    AbstractContainerMenu container = ctx.getPlayer().containerMenu;
    if (container instanceof AbstractDankContainer) {
      AbstractDankContainer dankContainer = (AbstractDankContainer)container;
      int i = 1 - dankContainer.propertyDelegate.get(slot);
      dankContainer.propertyDelegate.set(slot,i);
    }
  }
}

