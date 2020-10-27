package tfar.dankstorage.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.utils.Constants;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

public class PortableDankInventory extends DankInventory {

    protected final ItemStack bag;

    public PortableDankInventory(ItemStack bag) {
        this(Utils.getStats(bag), bag);
    }

    public PortableDankInventory(DankStats stats, ItemStack bag) {
        super(stats);
        this.bag = bag;
        readItemStack();
    }

    public void readItemStack() {
        deserializeNBT(bag.getOrCreateTagElement(Utils.INV));
    }

    @Override
    public void setChanged() {
        bag.getOrCreateTag().put(Utils.INV, serializeNBT());
    }


    public void deserializeNBT(CompoundTag nbt) {
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
}
