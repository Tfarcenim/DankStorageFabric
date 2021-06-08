
package tfar.dankstorage.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
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
import tfar.dankstorage.world.DankInventory;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankSavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DockBlockEntity extends BlockEntity implements Nameable, MenuProvider, Container {

    private int id = -1;

    public int numPlayersUsing = 0;
    public int mode = 0;
    public int selectedSlot;
    protected Component customName;

    public DockBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(DankStorage.dank_tile,blockPos, blockState);
    }

    private static final DankInventory DUMMY = new DankInventory(DankStats.zero,null);

    public DankInventory getInventory() {
        return id != -1 ? DankStorage.instance.data.getInventory(id) : DUMMY;
    }

    public int getComparatorSignal() {
        return this.getInventory().calcRedstone();
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

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.mode = compound.getInt("mode");
        this.selectedSlot = compound.getInt("selectedSlot");
        this.id = compound.getInt(Utils.ID);
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
        tag.putInt(Utils.ID,id);
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
        return getInventory().getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return getInventory().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return getInventory().getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return getInventory().removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return getInventory().removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        getInventory().setItem(slot, stack);
    }

    @Override
    public int getMaxStackSize() {
        return getInventory().getMaxStackSize();
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

    @Nullable
    @Override
    public DockMenu createMenu(int syncId, Inventory inventory, Player player) {
        return switch (getBlockState().getValue(DockBlock.TIER)) {
            case 1 -> DockMenu.t1s(syncId, inventory, this.getInventory());
            case 2 -> DockMenu.t2s(syncId, inventory, this.getInventory());
            case 3 -> DockMenu.t3s(syncId, inventory, this.getInventory());
            case 4 -> DockMenu.t4s(syncId, inventory, this.getInventory());
            case 5 -> DockMenu.t5s(syncId, inventory, this.getInventory());
            case 6 -> DockMenu.t6s(syncId, inventory, this.getInventory());
            case 7 -> DockMenu.t7s(syncId, inventory, this.getInventory());
            default -> null;
        };
    }

    public void removeTankWithItemSpawn() {
        ItemStack dankInStack = removeDankWithoutItemSpawn();
        ItemEntity entity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), dankInStack);
        level.addFreshEntity(entity);
    }

    public ItemStack removeDankWithoutItemSpawn() {
        DankInventory inventory = getInventory();
        int tier = inventory.dankStats.ordinal();

        if (tier == 0) {
            throw new RuntimeException("tried to remove a null dank?");
        }

        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(DockBlock.TIER, 0));
        ItemStack stack = new ItemStack(Utils.getItemFromTier(tier));
        stack.getOrCreateTag().putInt(Utils.ID,id);
        this.id = -1;
        stack.setHoverName(getCustomName());
        setCustomName(null);
        setChanged();
        return stack;
    }

    public void addDank(ItemStack tank) {
        if (tank.getItem() instanceof DankItem) {
            DankStats stats = ((DankItem) tank.getItem()).stats;
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(DockBlock.TIER, stats.ordinal()));
            setCustomName(tank.getHoverName());
            tank.shrink(1);

            CompoundTag tag = tank.getTag();

            if (tag != null) {
                this.id = tank.getTag().getInt(Utils.ID);
            } else {
                DankSavedData.getDefault((ServerLevel) level).getOrCreateInventory(DankSavedData.getDefault((ServerLevel) level).getNextID(),stats);
            }
            setChanged();
        }
    }

    @Override
    public void clearContent() {

    }
}