package tfar.dankstorage.inventory;

import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.mixin.SimpleInventoryAccessor;
import tfar.dankstorage.utils.Constants;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import java.util.stream.IntStream;

public class DankInventory extends SimpleContainer {

    public DankStats dankStats;
    public int[] lockedSlots;

    public DankInventory(DankStats stats) {
        super(stats.slots);
        this.dankStats = stats;
        this.lockedSlots = new int[stats.slots];
    }

    public void setDankStats(DankStats stats) {
        this.dankStats = stats;
        setSize();
    }

    private void setSize() {
        ((SimpleInventoryAccessor) this).setSize(dankStats.slots);

        NonNullList<ItemStack> newStacks = NonNullList.withSize(dankStats.slots, ItemStack.EMPTY);
        int max = Math.min(lockedSlots.length, dankStats.slots);

        for (int i = 0; i < max; i++) {
            newStacks.set(i, getContents().get(i));
        }

        ((SimpleInventoryAccessor) this).setItems(newStacks);

        int[] newLockedSlots = new int[dankStats.slots];
        if (max >= 0) System.arraycopy(lockedSlots, 0, newLockedSlots, 0, max);
        lockedSlots = newLockedSlots;
        setChanged();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (!isLocked(slot)) {
            return super.removeItem(slot, amount);
        }

        int amountInSlot = getItem(slot).getCount();

        if (amountInSlot < amount) {
            return super.removeItem(slot, amount);
        }

        amount = Math.min(amount, amountInSlot - 1);

        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = ContainerHelper.removeItem(getContents(), slot, amount);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }

        return itemStack;
    }

    @Override
    public int getMaxStackSize() {
        return dankStats.stacklimit;
    }

    public NonNullList<ItemStack> getContents() {
        return ((SimpleInventoryAccessor) this).getItems();
    }

    public boolean noValidSlots() {
        return IntStream.range(0, getContainerSize())
                .mapToObj(this::getItem)
                .allMatch(stack -> stack.isEmpty() || stack.getItem().is(Utils.BLACKLISTED_USAGE));
    }

    public boolean isLocked(int slot) {
        return lockedSlots[slot] == 1;
    }

    public void toggleLock(int slot) {
        lockedSlots[slot] = 1 - lockedSlots[slot];
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return !itemStack.getItem().is(Utils.BLACKLISTED_STORAGE);
    }

    //paranoia
    @Override
    public boolean canAddItem(ItemStack stack) {
        return !stack.getItem().is(Utils.BLACKLISTED_STORAGE) && super.canAddItem(stack);
    }

    //returns the portion of the itemstack that was NOT placed into the storage
    @Override
    public ItemStack addItem(ItemStack itemStack) {
        return itemStack.getItem().is(Utils.BLACKLISTED_STORAGE) ? itemStack : super.addItem(itemStack);
    }

    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < this.getContents().size(); i++) {
            if (!getContents().get(i).isEmpty()) {
                int realCount = Math.min(dankStats.stacklimit, getContents().get(i).getCount());
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                getContents().get(i).save(itemTag);
                itemTag.putInt("ExtendedCount", realCount);
                nbtTagList.add(itemTag);
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.putString("DankStats", dankStats.toString());
        nbt.put("Items", nbtTagList);
        nbt.putIntArray("LockedSlots", lockedSlots);
        nbt.putString("stats", dankStats.toString());
        return nbt;
    }

    public void addTank(CompoundTag nbt, ItemStack bag) {
        this.setDankStats(Utils.getStats(bag));
        ListTag tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < getContainerSize()) {
                if (itemTags.contains("StackList", Constants.NBT.TAG_LIST)) {
                    ItemStack stack = ItemStack.EMPTY;
                    ListTag stackTagList = itemTags.getList("StackList", Constants.NBT.TAG_COMPOUND);
                    for (int j = 0; j < stackTagList.size(); j++) {
                        CompoundTag itemTag = stackTagList.getCompound(j);
                        ItemStack temp = ItemStack.of(itemTag);
                        if (!temp.isEmpty()) {
                            if (stack.isEmpty()) stack = temp;
                            else stack.grow(temp.getCount());
                        }
                    }
                    if (!stack.isEmpty()) {
                        int count = stack.getCount();
                        count = Math.min(count, getMaxStackSize());
                        stack.setCount(count);

                        this.setItem(slot, stack);
                    }
                } else {
                    ItemStack stack = ItemStack.of(itemTags);
                    if (itemTags.contains("ExtendedCount", Constants.NBT.TAG_INT)) {
                        stack.setCount(itemTags.getInt("ExtendedCount"));
                    }
                    this.setItem(slot, stack);
                }
            }
        }
        lockedSlots = nbt.getIntArray("LockedSlots");
    }

    public void deserializeNBT(CompoundTag nbt) {
        DankStats stats = DankStats.valueOf(nbt.getString("DankStats"));
        this.setDankStats(stats);
        ListTag tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < getContainerSize()) {
                if (itemTags.contains("StackList", Constants.NBT.TAG_LIST)) {
                    ItemStack stack = ItemStack.EMPTY;
                    ListTag stackTagList = itemTags.getList("StackList", Constants.NBT.TAG_COMPOUND);
                    for (int j = 0; j < stackTagList.size(); j++) {
                        CompoundTag itemTag = stackTagList.getCompound(j);
                        ItemStack temp = ItemStack.of(itemTag);
                        if (!temp.isEmpty()) {
                            if (stack.isEmpty()) stack = temp;
                            else stack.grow(temp.getCount());
                        }
                    }
                    if (!stack.isEmpty()) {
                        int count = stack.getCount();
                        count = Math.min(count, getMaxStackSize());
                        stack.setCount(count);

                        this.setItem(slot, stack);
                    }
                } else {
                    ItemStack stack = ItemStack.of(itemTags);
                    if (itemTags.contains("ExtendedCount", Constants.NBT.TAG_INT)) {
                        stack.setCount(itemTags.getInt("ExtendedCount"));
                    }
                    this.setItem(slot, stack);
                }
            }
        }
        int[] listTag = nbt.getIntArray("LockedSlots");
        if (listTag.length == 0) {
            lockedSlots = new int[dankStats.slots];
        } else {
            lockedSlots = nbt.getIntArray("LockedSlots");
        }
    }

    public int calcRedstone() {
        int numStacks = 0;
        float f = 0F;

        for (int slot = 0; slot < this.getContainerSize(); slot++) {
            ItemStack stack = this.getItem(slot);

            if (!stack.isEmpty()) {
                f += (float) stack.getCount() / (float) this.getMaxStackSize();
                numStacks++;
            }
        }

        f /= this.getContainerSize();
        return Mth.floor(f * 14F) + (numStacks > 0 ? 1 : 0);
    }

}
