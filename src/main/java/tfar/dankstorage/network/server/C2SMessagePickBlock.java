package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.inventory.PortableDankInventory;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class C2SMessagePickBlock implements PacketConsumer {

  public static void send(int slot) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(slot);
    ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.pick_block, buf);
  }


  public void handle(PacketContext ctx, int slot) {
    PlayerEntity player = ctx.getPlayer();
      ItemStack bag = player.getMainHandStack();
      if (bag.getItem() instanceof DankItem) {
        Utils.setSelectedSlot(bag,slot);
      }
    }



  @Override
  public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
    int slot = packetByteBuf.readInt();
    packetContext.getTaskQueue().execute(() -> handle(packetContext,slot));
  }
}
