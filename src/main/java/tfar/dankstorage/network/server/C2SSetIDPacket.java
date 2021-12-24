package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.world.DankInventory;

public class C2SSetIDPacket implements ServerPlayNetworking.PlayChannelHandler {

    public static void send(int id,boolean set) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(id);
        buf.writeBoolean(set);
        ClientPlayNetworking.send(DankPacketHandler.set_id, buf);
    }

    public void handle(ServerPlayer player, int id,boolean set) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractDankMenu dankMenu) {
            DankInventory inventory = dankMenu.dankInventory;

            int textColor;

            if (id > -1) {
                DankInventory targetInventory = DankStorage.instance.data.getInventory(id);

                if (targetInventory != null && targetInventory.dankStats == inventory.dankStats) {

                    if (targetInventory.locked_id) {
                     textColor = 0xff0000ff;
                    } else {
                        textColor = 0xff00ff00;
                        if (set) {
                            dankMenu.setID(id);
                            player.closeContainer();
                        }
                    }
                } else {
                    //orange if it doesn't exist, yellow if it does but wrong tier
                    textColor = targetInventory == null ? 0xffff8000 : 0xffffff00;
                }
            } else {
                textColor = 0xffff0000;
            }
             inventory.setTextColor(textColor);
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int id = buf.readInt();
        boolean set = buf.readBoolean();
        server.execute(() -> handle(player, id,set));
    }
}

