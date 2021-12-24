package tfar.dankstorage.network.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.network.server.C2SMessageToggleUseType;
import tfar.dankstorage.utils.PacketBufferEX;
import tfar.dankstorage.world.ClientData;

public class S2CSyncSelected implements ClientPlayNetworking.PlayChannelHandler {

    public void handle(@Nullable LocalPlayer player, ItemStack stack, C2SMessageToggleUseType.UseType useType) {
        if (player != null) {
            ClientData.setData(stack,useType);
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
      //  int windowId = buf.readInt();
        ItemStack stack = PacketBufferEX.readExtendedItemStack(buf);
        C2SMessageToggleUseType.UseType useType = C2SMessageToggleUseType.UseType.values()[buf.readInt()];
        client.execute(() -> handle(client.player, stack,useType));
    }
}