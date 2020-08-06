package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;


public class C2SMessageToggleUseType implements PacketConsumer {

  public static void send() {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.toggle_use, buf);
  }

    public void handle(PacketContext ctx) {
      PlayerEntity player = ctx.getPlayer();

        if (player.getMainHandStack().getItem() instanceof DankItem) {
          Utils.cyclePlacement(player.getMainHandStack(),player);
        }
    }

  public static final UseType[] useTypes = UseType.values();

  @Override
  public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
    packetContext.getTaskQueue().execute(() -> handle(packetContext));
  }

  public enum UseType{
    bag,construction
    }
}

