package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;


public class C2SMessageToggleUseType implements PacketConsumer {

  public static void send() {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.toggle_use, buf);
  }

    public void handle(PacketContext ctx) {
      Player player = ctx.getPlayer();

        if (player.getMainHandItem().getItem() instanceof DankItem) {
          Utils.cyclePlacement(player.getMainHandItem(),player);
        }
    }

  public static final UseType[] useTypes = UseType.values();

  @Override
  public void accept(PacketContext packetContext, FriendlyByteBuf packetByteBuf) {
    packetContext.getTaskQueue().execute(() -> handle(packetContext));
  }

  public enum UseType{
    bag,construction
    }
}

