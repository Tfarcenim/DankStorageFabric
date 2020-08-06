package tfar.dankstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import tfar.dankstorage.utils.Constants;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

public class PortableDankInventory extends DankInventory {

	protected final ItemStack bag;

	public PortableDankInventory(ItemStack bag) {
		this(Utils.getStats(bag),bag);
	}

	public PortableDankInventory(DankStats stats, ItemStack bag) {
		super(stats);
		this.bag = bag;
		readItemStack();
	}

	public void readItemStack() {
		deserializeNBT(bag.getOrCreateSubTag(Utils.INV));
	}

	@Override
	public void markDirty() {
		bag.getOrCreateTag().put(Utils.INV,serializeNBT());
	}


	public void deserializeNBT(CompoundTag nbt) {
		ListTag tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");
			if (slot >= 0 && slot < size()) {
				if (itemTags.contains("StackList", Constants.NBT.TAG_LIST)) {
					ItemStack stack = ItemStack.EMPTY;
					ListTag stackTagList = itemTags.getList("StackList", Constants.NBT.TAG_COMPOUND);
					for (int j = 0; j < stackTagList.size(); j++) {
						CompoundTag itemTag = stackTagList.getCompound(j);
						ItemStack temp = ItemStack.fromTag(itemTag);
						if (!temp.isEmpty()) {
							if (stack.isEmpty()) stack = temp;
							else stack.increment(temp.getCount());
						}
					}
					if (!stack.isEmpty()) {
						int count = stack.getCount();
						count = Math.min(count, getMaxCountPerStack());
						stack.setCount(count);

						this.setStack(slot, stack);
					}
				} else {
					ItemStack stack = ItemStack.fromTag(itemTags);
					if (itemTags.contains("ExtendedCount", Constants.NBT.TAG_INT)) {
						stack.setCount(itemTags.getInt("ExtendedCount"));
					}
					this.setStack(slot, stack);
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
