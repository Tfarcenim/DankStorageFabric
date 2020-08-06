package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

public class C2SMessageScrollSlot implements PacketConsumer {

	public static void send(boolean right) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(right);
		ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.scroll, buf);
	}

	@Override
  public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
    boolean right = packetByteBuf.readBoolean();
    packetContext.getTaskQueue().execute(() -> handle(packetContext,right));
  }

  public void handle(PacketContext ctx, boolean right) {
    PlayerEntity player = ctx.getPlayer();
    ItemStack bag = player.getMainHandStack();
    Utils.changeSlot(bag, right);
  }
}

