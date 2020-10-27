package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;

public class C2SMessageTagMode implements PacketConsumer {

    public static void send() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ClientSidePacketRegistry.INSTANCE.sendToServer(DankPacketHandler.tag_mode, buf);
    }


    public void handle(PacketContext ctx) {
        Player player = ctx.getPlayer();

        if (player.getMainHandItem().getItem() instanceof DankItem) {
            boolean toggle = Utils.oredict(player.getMainHandItem());
            player.getMainHandItem().getOrCreateTag().putBoolean("tag", !toggle);
        } else if (player.getOffhandItem().getItem() instanceof DankItem) {
            boolean toggle = Utils.oredict(player.getOffhandItem());
            player.getOffhandItem().getOrCreateTag().putBoolean("tag", !toggle);
        }
    }

    @Override
    public void accept(PacketContext packetContext, FriendlyByteBuf packetByteBuf) {
        packetContext.getTaskQueue().execute(() -> handle(packetContext));
    }
}

