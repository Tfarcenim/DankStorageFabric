package tfar.dankstorage.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import tfar.dankstorage.container.DankContainer;
import tfar.dankstorage.network.DankPacketHandler;

public class S2CSyncNBTSize implements PacketConsumer {

  public S2CSyncNBTSize(){}

  public static void send(PlayerEntity player,int nbtSize) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(nbtSize);
    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, DankPacketHandler.sync_nbt, buf);
  }

  @Override
  public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
    int size = packetByteBuf.readInt();
    packetContext.getTaskQueue().execute(() -> handle(packetContext,size));
  }

  public void handle(PacketContext ctx, int size) {
    PlayerEntity player = MinecraftClient.getInstance().player;
    ScreenHandler container = player.currentScreenHandler;
    if (container instanceof DankContainer) {
      ((DankContainer) container).nbtSize = size;
    }
  }

}