package tfar.dankstorage.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.network.client.S2CSyncExtendedSlotContents;
import tfar.dankstorage.network.client.S2CSyncSelected;
import tfar.dankstorage.network.server.C2SMessageToggleUseType;
import tfar.dankstorage.utils.PacketBufferEX;

public class ClientDankPacketHandler {

    public static void registerClientMessages() {
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.sync_stacks, new S2CSyncExtendedSlotContents());
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.sync_data, new S2CSyncSelected());
    }

    public static void send(ServerPlayer player, int id, int slot, ItemStack stack) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(id);
        buf.writeInt(slot);
        PacketBufferEX.writeExtendedItemStack(buf, stack);
        ServerPlayNetworking.send(player, DankPacketHandler.sync_stacks, buf);
    }

    public static void sendSelected(ServerPlayer player, int id, ItemStack stack, C2SMessageToggleUseType.UseType useType) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(id);
        PacketBufferEX.writeExtendedItemStack(buf, stack);
        buf.writeInt(useType.ordinal());
        ServerPlayNetworking.send(player, DankPacketHandler.sync_data, buf);
    }
}
