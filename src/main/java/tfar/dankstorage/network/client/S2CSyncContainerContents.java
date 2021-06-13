package tfar.dankstorage.network.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.container.AbstractDankMenu;

public class S2CSyncContainerContents implements ClientPlayNetworking.PlayChannelHandler {

    public void handle(@Nullable LocalPlayer player, int windowId, NonNullList<ItemStack> stacks) {
        if (player != null && player.containerMenu instanceof AbstractDankMenu && windowId == player.containerMenu.containerId) {
                player.containerMenu.setAll(stacks);
            }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int id = buf.readUnsignedByte();
        int i = buf.readShort();
        NonNullList<ItemStack> stacks = NonNullList.withSize(i, ItemStack.EMPTY);

        for(int j = 0; j < i; ++j) {
            stacks.set(j, buf.readItem());
        }
        client.execute(() -> handle(client.player, id,stacks));
    }
}