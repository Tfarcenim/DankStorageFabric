package tfar.dankstorage.network;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.network.server.*;

public class DankPacketHandler {

  public static final Identifier sync_nbt = new Identifier(DankStorage.MODID,"sync_nbt");
  public static final Identifier toggle_pickup = new Identifier(DankStorage.MODID,"toggle_pickup");
  public static final Identifier tag_mode = new Identifier(DankStorage.MODID,"tag_mode");
  public static final Identifier sort = new Identifier(DankStorage.MODID,"sort");
  public static final Identifier lock_slot = new Identifier(DankStorage.MODID,"lock_slot");
  public static final Identifier sync_stacks = new Identifier(DankStorage.MODID,"sync_stacks");
  public static final Identifier pick_block = new Identifier(DankStorage.MODID,"pick_block");
  public static Identifier toggle_use = new Identifier(DankStorage.MODID,"toggle_use");
  public static Identifier scroll = new Identifier(DankStorage.MODID,"scroll");


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
