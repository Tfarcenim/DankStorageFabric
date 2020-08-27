package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.inventory.PortableDankInventory;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class C2SMessagePickBlock implements PacketConsumer {

  public static void send(int slot) {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    buf.writeInt(slot);
    ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.pick_block, buf);
  }


  public void handle(PacketContext ctx, int slot) {
    Player player = ctx.getPlayer();
      ItemStack bag = player.getMainHandItem();
      if (bag.getItem() instanceof DankItem) {
        Utils.setSelectedSlot(bag,slot);
      }
    }



  @Override
  public void accept(PacketContext packetContext, FriendlyByteBuf packetByteBuf) {
    int slot = packetByteBuf.readInt();
    packetContext.getTaskQueue().execute(() -> handle(packetContext,slot));
  }
}
