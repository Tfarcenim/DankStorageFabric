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

  public static void send() {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.pick_block, buf);
  }

  private static final Logger LOGGER = LogManager.getLogger();

  public void handle(PacketContext ctx) {
    PlayerEntity player = ctx.getPlayer();

      ItemStack bag = player.getMainHandStack();
      if (bag.getItem() instanceof DankItem) {
        PortableDankInventory handler = Utils.getHandler(bag);
        ItemStack pickblock = onPickBlock(player.rayTrace(20.0D,0,false),player,player.world);
        int slot = -1;
        if (!pickblock.isEmpty())
        for (int i = 0; i < handler.size(); i++) {
          if (pickblock.getItem() == handler.getStack(i).getItem()){
            slot = i;
            break;
          }
        }
        if (slot != -1)
        Utils.setSelectedSlot(bag,slot);
      }
    }

  public static ItemStack onPickBlock(HitResult target, PlayerEntity player, World world) {
    ItemStack result = ItemStack.EMPTY;

    if (target.getType() == HitResult.Type.BLOCK) {
      BlockPos pos = ((BlockHitResult) target).getBlockPos();
      BlockState state = world.getBlockState(pos);

      if (state.isAir()) return ItemStack.EMPTY;
        result = state.getBlock().getPickStack(world, pos, state);

      if (result.isEmpty())
        LOGGER.warn("Picking on: [{}] {} gave null item", target.getType(), state.getBlock());
    }
    return result;
  }

  @Override
  public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
    packetContext.getTaskQueue().execute(() -> handle(packetContext));
  }
}
