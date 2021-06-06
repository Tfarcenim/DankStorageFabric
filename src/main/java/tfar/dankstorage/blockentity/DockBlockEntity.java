
package tfar.dankstorage.blockentity;

import net.minecraft.core.BlockPos;
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
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.container.DockMenu;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DockBlockEntity extends BlockEntity implements Nameable, MenuProvider, Container {

    private final DankInventory handler = new DankInventory(DankStats.zero) {
        @Override
        public void setChanged() {
            super.setChanged();
            DockBlockEntity.this.setChanged();
        }
    };

    protected final ContainerData propertyDelegate = new ContainerData() {
        public int get(int index) {
            if (handler.lockedSlots.length == 0)
                return -1;
            return handler.lockedSlots[index];
        }

        public void set(int index, int value) {
            handler.lockedSlots[index] = value;
        }

        public int getCount() {
            return handler.lockedSlots.length;
        }
    };

    public int numPlayersUsing = 0;
    public int mode = 0;
    public int selectedSlot;
    protected Component customName;

    public DockBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(DankStorage.dank_tile,blockPos, blockState);
    }

    public DankInventory getHandler() {
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
    public void load(CompoundTag compound) {
        super.load(compound);
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
        tag.putInt("mode", mode);
        tag.putInt("selectedSlot", selectedSlot);
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
        return handler.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return handler.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        handler.setItem(slot, stack);
    }

    @Override
    public int getMaxStackSize() {
        return handler.getMaxStackSize();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public Component getName() {
        return customName != null ? customName : getDefaultName();
    }

    public Component getDefaultName() {
        return new TranslatableComponent("container.dankstorage.dank_" + getBlockState().getValue(DockBlock.TIER));
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

    public void setCustomName(Component text) {
        this.customName = text;
    }

    public void setContents(CompoundTag nbt) {
        handler.deserializeNBT(nbt);
    }

    @Nullable
    @Override
    public DockMenu createMenu(int syncId, Inventory inventory, Player player) {
        return switch (getBlockState().getValue(DockBlock.TIER)) {
            case 1 -> DockMenu.t1s(syncId, inventory, handler, propertyDelegate);
            case 2 -> DockMenu.t2s(syncId, inventory, handler, propertyDelegate);
            case 3 -> DockMenu.t3s(syncId, inventory, handler, propertyDelegate);
            case 4 -> DockMenu.t4s(syncId, inventory, handler, propertyDelegate);
            case 5 -> DockMenu.t5s(syncId, inventory, handler, propertyDelegate);
            case 6 -> DockMenu.t6s(syncId, inventory, handler, propertyDelegate);
            case 7 -> DockMenu.t7s(syncId, inventory, handler, propertyDelegate);
            default -> null;
        };
    }

    public void removeTankWithItemSpawn() {
        ItemStack dankInStack = removeTankWithoutItemSpawn();
        ItemEntity entity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), dankInStack);
        level.addFreshEntity(entity);
    }

    public ItemStack removeTankWithoutItemSpawn() {
        int tier = getBlockState().getValue(DockBlock.TIER);
        CompoundTag nbt = handler.serializeNBT();
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(DockBlock.TIER, 0));
        ItemStack stack = new ItemStack(Utils.getItemFromTier(tier));
        stack.getOrCreateTag().put(Utils.INV, nbt);
        stack.setHoverName(getCustomName());
        setCustomName(null);
        handler.setDankStats(DankStats.zero);
        setChanged();
        return stack;
    }

    public void addTank(ItemStack tank) {
        if (tank.getItem() instanceof DankItem) {
            DankStats stats = ((DankItem) tank.getItem()).stats;
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(DockBlock.TIER, stats.ordinal()));
            handler.addTank(tank.getOrCreateTag().getCompound(Utils.INV), tank);
            setCustomName(tank.getHoverName());
            tank.shrink(1);
            setChanged();
        }
    }

    @Override
    public void clearContent() {

    }
}