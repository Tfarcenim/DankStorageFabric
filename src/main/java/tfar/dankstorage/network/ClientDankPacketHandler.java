package tfar.dankstorage.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import tfar.dankstorage.network.client.S2CSyncContainerContents;
import tfar.dankstorage.network.client.S2CSyncExtendedSlotContents;
import tfar.dankstorage.network.client.S2CSyncInventory;
import tfar.dankstorage.network.client.S2CSyncSelected;

public class ClientDankPacketHandler {

    public static void registerClientMessages() {
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.sync_slot, new S2CSyncExtendedSlotContents());
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.sync_data, new S2CSyncSelected());
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.sync_container, new S2CSyncContainerContents());
        ClientPlayNetworking.registerGlobalReceiver(DankPacketHandler.sync_inventory, new S2CSyncInventory());
    }
}
