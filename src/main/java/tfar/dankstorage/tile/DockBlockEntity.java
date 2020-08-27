
package tfar.dankstorage.tile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.container.DockMenu;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DockBlockEntity extends BlockEntity implements Nameable, MenuProvider , Container {

  protected final ContainerData propertyDelegate;
  public int numPlayersUsing = 0;
  protected Component customName;
  public int mode = 0;
  public int selectedSlot;

  private final DankInventory handler = new DankInventory(DankStats.zero) {
    @Override
    public void setChanged() {
      super.setChanged();
      DockBlockEntity.this.setChanged();
    }
  };

  public DockBlockEntity() {
    super(DankStorage.dank_tile);
    this.propertyDelegate = new ContainerData() {
      public int get(int index) {
          return DockBlockEntity.this.handler.lockedSlots[index];
      }

      public void set(int index, int value) {
        DockBlockEntity.this.handler.lockedSlots[index] = value;
      }

      public int getCount() {
        return DockBlockEntity.this.handler.lockedSlots.length;
      }
    };
  }

  public DankInventory getHandler(){
    return handler;
  }

  public int getComparatorSignal() {
    return this.handler.calcRedstone();
  }

  @Override
  public boolean triggerEvent(int id, int type) {
    if (id == 1) {
      this.numPlayersUsing = type;
      this.setChanged();
      return true;
    } else {
      return super.triggerEvent(id, type);
    }
  }

  public void openInventory(Player player) {
    if (!player.isSpectator()) {
      if (this.numPlayersUsing < 0) {
        this.numPlayersUsing = 0;
      }

      ++this.numPlayersUsing;
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      setChanged();
    }
  }

  public void closeInventory(Player player) {
    if (!player.isSpectator() && this.getBlockState().getBlock() instanceof DockBlock) {
      --this.numPlayersUsing;
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      setChanged();
    }
  }

  @Override
  public void load(BlockState state,CompoundTag compound) {
    super.load(state,compound);
    this.mode = compound.getInt("mode");
    this.selectedSlot = compound.getInt("selectedSlot");
    if (compound.contains(Utils.INV)) {
      handler.deserializeNBT(compound.getCompound(Utils.INV));
    }
    if (compound.contains("CustomName", 8)) {
      this.setCustomName(Component.Serializer.fromJson(compound.getString("CustomName")));
    }
  }

  @Nonnull
  @Override
  public CompoundTag save(CompoundTag tag) {
    super.save(tag);
    tag.putInt("mode",mode);
    tag.putInt("selectedSlot",selectedSlot);
    tag.put(Utils.INV, handler.serializeNBT());
    if (this.hasCustomName()) {
      tag.putString("CustomName", Component.Serializer.toJson(this.customName));
    }
    return tag;
  }

  @Nonnull
  @Override
  public CompoundTag getUpdateTag() {
    return save(new CompoundTag());
  }

  @Nullable
  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return new ClientboundBlockEntityDataPacket(getBlockPos(), 1, getUpdateTag());
  }

  //Inventory nonsense

  @Override
  public int getContainerSize() {
    return handler.getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return handler.isEmpty();
  }

  @Override
  public ItemStack getItem(int slot) {
    return handler.getItem(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    return handler.removeItem(slot,amount);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return handler.removeItemNoUpdate(slot);
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    handler.setItem(slot,stack);
  }

  @Override
  public int getMaxStackSize() {
    return handler.getMaxStackSize();
  }

  @Override
  public void setChanged() {
    super.setChanged();
    if (getLevel() != null) {
      getLevel().sendBlockUpdated(worldPosition, getLevel().getBlockState(worldPosition), getLevel().getBlockState(worldPosition), 3);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
    }
  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }

  public void setCustomName(Component text) {
    this.customName = text;
  }

  @Override
  public Component getName() {
    return customName != null ? customName : getDefaultName();
  }

  public Component getDefaultName() {
    return new TranslatableComponent("container.dankstorage.dank_"+getBlockState().getValue(DockBlock.TIER));
  }

  @Override
  public Component getDisplayName() {
    return this.getName();
  }

  @Nullable
  @Override
  public Component getCustomName() {
    return customName;
  }

  public void setContents(CompoundTag nbt){
    handler.deserializeNBT(nbt);
  }

  @Nullable
  @Override
  public DockMenu createMenu(int syncId, Inventory p_createMenu_2_, Player p_createMenu_3_) {
    switch (getBlockState().getValue(DockBlock.TIER)) {
      case 1:return DockMenu.t1s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 2:return DockMenu.t2s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 3:return DockMenu.t3s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 4:return DockMenu.t4s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 5:return DockMenu.t5s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 6:return DockMenu.t6s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 7:return DockMenu.t7s(syncId,p_createMenu_2_,handler,propertyDelegate);
    }
    return null;
  }

  public void removeTank() {
    int tier = getBlockState().getValue(DockBlock.TIER);
    CompoundTag nbt = handler.serializeNBT();
    level.setBlockAndUpdate(worldPosition,getBlockState().setValue(DockBlock.TIER,0));
    ItemStack stack = new ItemStack(Utils.getItemFromTier(tier));
    stack.getOrCreateTag().put(Utils.INV,nbt);
    ItemEntity entity = new ItemEntity(level,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),stack);
    level.addFreshEntity(entity);
    handler.setDankStats(DankStats.zero);
  }

  public ItemStack removeTank0() {
    int tier = getBlockState().getValue(DockBlock.TIER);
    CompoundTag nbt = handler.serializeNBT();
    level.setBlockAndUpdate(worldPosition,getBlockState().setValue(DockBlock.TIER,0));
    ItemStack stack = new ItemStack(Utils.getItemFromTier(tier));
    stack.getOrCreateTag().put(Utils.INV,nbt);
    handler.setDankStats(DankStats.zero);
    return stack;
  }

  public void addTank(ItemStack tank){
    if (tank.getItem() instanceof DankItem) {
      DankStats stats = ((DankItem)tank.getItem()).stats;
      level.setBlockAndUpdate(worldPosition,getBlockState().setValue(DockBlock.TIER,stats.ordinal()));
      handler.addTank(tank.getOrCreateTag().getCompound(Utils.INV),tank);
      tank.shrink(1);
    }
  }

  @Override
  public void clearContent() {

  }
}