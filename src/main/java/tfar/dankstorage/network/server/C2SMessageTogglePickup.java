package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Mode;
import tfar.dankstorage.utils.Utils;

public class C2SMessageTogglePickup implements PacketConsumer {

  public static void send() {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.toggle_pickup, buf);
  }

  public static final Mode[] modes = Mode.values();

    public void handle(PacketContext ctx) {
      PlayerEntity player = ctx.getPlayer();
        ItemStack bag = player.getMainHandStack();
        if (!(bag.getItem() instanceof DankItem)){
          bag = player.getOffHandStack();
          if (!(bag.getItem() instanceof DankItem))return;
        }
          Utils.cycleMode(bag,player);
    }

  @Override
  public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
    packetContext.getTaskQueue().execute(() -> handle(packetContext));
  }
}

