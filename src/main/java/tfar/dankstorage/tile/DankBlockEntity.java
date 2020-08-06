
package tfar.dankstorage.tile;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.container.DockContainer;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DankBlockEntity extends BlockEntity implements Nameable, NamedScreenHandlerFactory {

  protected final PropertyDelegate propertyDelegate;
  public int numPlayersUsing = 0;
  protected Text customName;
  public int mode = 0;
  public int selectedSlot;

  private final DankInventory handler = new DankInventory(DankStats.zero) {
    @Override
    public void markDirty() {
      super.markDirty();
      DankBlockEntity.this.markDirty();
    }
  };

  public DankBlockEntity() {
    super(DankStorage.dank_tile);
    this.propertyDelegate = new PropertyDelegate() {
      public int get(int index) {
          return DankBlockEntity.this.handler.lockedSlots[index];
      }

      public void set(int index, int value) {
        DankBlockEntity.this.handler.lockedSlots[index] = value;
      }

      public int size() {
        return DankBlockEntity.this.handler.lockedSlots.length;
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
  public boolean onSyncedBlockEvent(int id, int type) {
    if (id == 1) {
      this.numPlayersUsing = type;
      this.markDirty();
      return true;
    } else {
      return super.onSyncedBlockEvent(id, type);
    }
  }

  public void openInventory(PlayerEntity player) {
    if (!player.isSpectator()) {
      if (this.numPlayersUsing < 0) {
        this.numPlayersUsing = 0;
      }

      ++this.numPlayersUsing;
      this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.numPlayersUsing);
      this.world.updateNeighborsAlways(this.pos, this.getCachedState().getBlock());
      markDirty();
    }
  }

  public void closeInventory(PlayerEntity player) {
    if (!player.isSpectator() && this.getCachedState().getBlock() instanceof DockBlock) {
      --this.numPlayersUsing;
      this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.numPlayersUsing);
      this.world.updateNeighborsAlways(this.pos, this.getCachedState().getBlock());
      markDirty();
    }
  }

  @Override
  public void fromTag(BlockState state,CompoundTag compound) {
    super.fromTag(state,compound);
    this.mode = compound.getInt("mode");
    this.selectedSlot = compound.getInt("selectedSlot");
    if (compound.contains(Utils.INV)) {
      handler.deserializeNBT(compound.getCompound(Utils.INV));
    }
    if (compound.contains("CustomName", 8)) {
      this.setCustomName(Text.Serializer.fromJson(compound.getString("CustomName")));
    }
  }

  @Nonnull
  @Override
  public CompoundTag toTag(CompoundTag tag) {
    super.toTag(tag);
    tag.putInt("mode",mode);
    tag.putInt("selectedSlot",selectedSlot);
    tag.put(Utils.INV, handler.serializeNBT());
    if (this.hasCustomName()) {
      tag.putString("CustomName", Text.Serializer.toJson(this.customName));
    }
    return tag;
  }

  @Nonnull
  @Override
  public CompoundTag toInitialChunkDataTag() {
    return toTag(new CompoundTag());
  }

  @Nullable
  @Override
  public BlockEntityUpdateS2CPacket toUpdatePacket() {
    return new BlockEntityUpdateS2CPacket(getPos(), 1, toInitialChunkDataTag());
  }

  @Override
  public void markDirty() {
    super.markDirty();
    if (getWorld() != null) {
      getWorld().updateListeners(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
      this.world.updateNeighborsAlways(this.pos, this.getCachedState().getBlock());
    }
  }

  public void setCustomName(Text text) {
    this.customName = text;
  }

  @Override
  public Text getName() {
    return customName != null ? customName : getDefaultName();
  }

  Text getDefaultName() {
    return new TranslatableText("container.dankstorage.dank_"+getCachedState().get(DockBlock.TIER));
  }

  @Override
  public Text getDisplayName() {
    return this.getName();
  }

  @Nullable
  @Override
  public Text getCustomName() {
    return customName;
  }

  public void setContents(CompoundTag nbt){
    handler.deserializeNBT(nbt);
  }

  @Nullable
  @Override
  public DockContainer createMenu(int syncId, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
    switch (getCachedState().get(DockBlock.TIER)) {
      case 1:return DockContainer.t1s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 2:return DockContainer.t2s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 3:return DockContainer.t3s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 4:return DockContainer.t4s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 5:return DockContainer.t5s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 6:return DockContainer.t6s(syncId,p_createMenu_2_,handler,propertyDelegate);
      case 7:return DockContainer.t7s(syncId,p_createMenu_2_,handler,propertyDelegate);
    }
    return null;
  }

  public void removeTank(){
    int tier = getCachedState().get(DockBlock.TIER);
    CompoundTag nbt = handler.serializeNBT();
    world.setBlockState(pos,getCachedState().with(DockBlock.TIER,0));
    ItemStack stack = new ItemStack(Utils.getItemFromTier(tier));
    stack.getOrCreateTag().put(Utils.INV,nbt);
    ItemEntity entity = new ItemEntity(world,pos.getX(),pos.getY(),pos.getZ(),stack);
    world.spawnEntity(entity);
    handler.setDankStats(DankStats.zero);
  }

  public void addTank(ItemStack tank){
    if (tank.getItem() instanceof DankItem) {
      DankStats stats = ((DankItem)tank.getItem()).stats;
      world.setBlockState(pos,getCachedState().with(DockBlock.TIER,stats.ordinal()));
      handler.addTank(tank.getOrCreateTag().getCompound(Utils.INV),tank);
      tank.decrement(1);
    }
  }
}