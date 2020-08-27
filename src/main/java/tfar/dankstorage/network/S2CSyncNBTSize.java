package tfar.dankstorage.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.dankstorage.container.DankContainer;
import tfar.dankstorage.network.DankPacketHandler;

public class S2CSyncNBTSize implements PacketConsumer {

  public S2CSyncNBTSize(){}

  public static void send(Player player,int nbtSize) {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    buf.writeInt(nbtSize);
    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, DankPacketHandler.sync_nbt, buf);
  }

  @Override
  public void accept(PacketContext packetContext, FriendlyByteBuf packetByteBuf) {
    int size = packetByteBuf.readInt();
    packetContext.getTaskQueue().execute(() -> handle(packetContext,size));
  }

  public void handle(PacketContext ctx, int size) {
    Player player = Minecraft.getInstance().player;
    AbstractContainerMenu container = player.containerMenu;
    if (container instanceof DankContainer) {
      ((DankContainer) container).nbtSize = size;
    }
  }

}