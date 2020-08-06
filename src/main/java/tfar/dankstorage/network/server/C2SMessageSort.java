package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;

public class C2SMessageSort implements PacketConsumer {

  public static void send() {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.sort, buf);
  }

  public void handle(PacketContext ctx) {
    PlayerEntity player = ctx.getPlayer();
    Utils.sort(player);
  }

  @Override
  public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
    packetContext.getTaskQueue().execute(() -> handle(packetContext));
  }
}