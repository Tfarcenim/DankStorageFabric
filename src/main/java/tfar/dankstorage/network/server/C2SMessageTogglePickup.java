package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Mode;
import tfar.dankstorage.utils.Utils;

public class C2SMessageTogglePickup implements PacketConsumer {

  public static void send() {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.toggle_pickup, buf);
  }

  public void handle(PacketContext ctx) {
      Player player = ctx.getPlayer();
        ItemStack bag = player.getMainHandItem();
        if (!(bag.getItem() instanceof DankItem)){
          bag = player.getOffhandItem();
          if (!(bag.getItem() instanceof DankItem))return;
        }
          Utils.cycleMode(bag,player);
    }

  @Override
  public void accept(PacketContext packetContext, FriendlyByteBuf packetByteBuf) {
    packetContext.getTaskQueue().execute(() -> handle(packetContext));
  }
}

