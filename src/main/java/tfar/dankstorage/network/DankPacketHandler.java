package tfar.dankstorage.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.network.client.S2CSyncExtendedSlotContents;
import tfar.dankstorage.network.client.S2CSyncSelected;
import tfar.dankstorage.network.server.*;

public class DankPacketHandler {

    public static final ResourceLocation toggle_pickup = new ResourceLocation(DankStorage.MODID, "toggle_pickup");
    public static final ResourceLocation tag_mode = new ResourceLocation(DankStorage.MODID, "tag_mode");
    public static final ResourceLocation sort = new ResourceLocation(DankStorage.MODID, "sort");
    public static final ResourceLocation lock_slot = new ResourceLocation(DankStorage.MODID, "lock_slot");
    public static final ResourceLocation sync_stacks = new ResourceLocation(DankStorage.MODID, "sync_stacks");
    public static final ResourceLocation pick_block = new ResourceLocation(DankStorage.MODID, "pick_block");
    public static ResourceLocation toggle_use = new ResourceLocation(DankStorage.MODID, "toggle_use");
    public static ResourceLocation scroll = new ResourceLocation(DankStorage.MODID, "scroll");
    public static final ResourceLocation sync_data = new ResourceLocation(DankStorage.MODID, "sync_data");



    public static void registerMessages() {
        ServerPlayNetworking.registerGlobalReceiver(scroll, new C2SMessageScrollSlot());
        ServerPlayNetworking.registerGlobalReceiver(lock_slot, new C2SMessageLockSlot());
        ServerPlayNetworking.registerGlobalReceiver(sort, new C2SMessageSort());
        ServerPlayNetworking.registerGlobalReceiver(tag_mode, new C2SMessageTagMode());
        ServerPlayNetworking.registerGlobalReceiver(toggle_pickup, new C2SMessageTogglePickup());
        ServerPlayNetworking.registerGlobalReceiver(toggle_use, new C2SMessageToggleUseType());
        ServerPlayNetworking.registerGlobalReceiver(pick_block, new C2SMessagePickBlock());
    }
}
