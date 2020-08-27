package tfar.dankstorage.network;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.resources.ResourceLocation;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.network.server.*;

public class DankPacketHandler {

  public static final ResourceLocation sync_nbt = new ResourceLocation(DankStorage.MODID,"sync_nbt");
  public static final ResourceLocation toggle_pickup = new ResourceLocation(DankStorage.MODID,"toggle_pickup");
  public static final ResourceLocation tag_mode = new ResourceLocation(DankStorage.MODID,"tag_mode");
  public static final ResourceLocation sort = new ResourceLocation(DankStorage.MODID,"sort");
  public static final ResourceLocation lock_slot = new ResourceLocation(DankStorage.MODID,"lock_slot");
  public static final ResourceLocation sync_stacks = new ResourceLocation(DankStorage.MODID,"sync_stacks");
  public static final ResourceLocation pick_block = new ResourceLocation(DankStorage.MODID,"pick_block");
  public static ResourceLocation toggle_use = new ResourceLocation(DankStorage.MODID,"toggle_use");
  public static ResourceLocation scroll = new ResourceLocation(DankStorage.MODID,"scroll");


	public static void registerMessages() {
    ServerSidePacketRegistry.INSTANCE.register(scroll,new C2SMessageScrollSlot());
    ServerSidePacketRegistry.INSTANCE.register(lock_slot,new C2SMessageLockSlot());
    ServerSidePacketRegistry.INSTANCE.register(sort,new C2SMessageSort());
    ServerSidePacketRegistry.INSTANCE.register(tag_mode,new C2SMessageTagMode());
    ServerSidePacketRegistry.INSTANCE.register(toggle_pickup,new C2SMessageTogglePickup());
    ServerSidePacketRegistry.INSTANCE.register(toggle_use,new C2SMessageToggleUseType());
    ServerSidePacketRegistry.INSTANCE.register(pick_block,new C2SMessagePickBlock());
  }

  public static void registerClientMessages() {
    ClientSidePacketRegistry.INSTANCE.register(sync_nbt,new S2CSyncNBTSize());
    ClientSidePacketRegistry.INSTANCE.register(sync_stacks,new S2CSyncExtendedSlotContents());
  }
}
