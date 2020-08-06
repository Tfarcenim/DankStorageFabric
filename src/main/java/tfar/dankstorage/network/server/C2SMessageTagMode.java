package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;

public class C2SMessageTagMode implements PacketConsumer {

  public static void send() {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.tag_mode, buf);
  }


    public void handle(PacketContext ctx) {
      PlayerEntity player = ctx.getPlayer();

        if (player.getMainHandStack().getItem() instanceof DankItem) {
          boolean toggle = Utils.oredict(player.getMainHandStack());
          player.getMainHandStack().getOrCreateTag().putBoolean("tag",!toggle);
        }
      }

  @Override
  public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
    packetContext.getTaskQueue().execute(() -> handle(packetContext));
  }
}

