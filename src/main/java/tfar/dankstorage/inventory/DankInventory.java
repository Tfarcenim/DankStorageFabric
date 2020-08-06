package tfar.dankstorage.inventory;

import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import tfar.dankstorage.mixin.ItemStackMixin;
import tfar.dankstorage.mixin.SimpleInventoryAccessor;
import tfar.dankstorage.utils.Constants;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import java.util.stream.IntStream;

public class DankInventory extends SimpleInventory {

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
		((SimpleInventoryAccessor)this).setSize(dankStats.slots);

		DefaultedList<ItemStack> newStacks = DefaultedList.ofSize(dankStats.slots,ItemStack.EMPTY);
		int max = Math.min(lockedSlots.length,dankStats.slots);

		for (int i = 0; i < max; i++) {
			newStacks.set(i,getContents().get(i));
		}

		((SimpleInventoryAccessor)this).setStacks(newStacks);

		int[] newLockedSlots = new int[dankStats.slots];
		if (max >= 0) System.arraycopy(lockedSlots, 0, newLockedSlots, 0, max);
		lockedSlots = newLockedSlots;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		if (!isLocked(slot)) {
			return super.removeStack(slot,amount);
		}

		int amountInSlot = getStack(slot).getCount();

		if (amountInSlot < amount) {
			return super.removeStack(slot,amount);
		}

		amount = Math.min(amount,amountInSlot - 1);

		if (amount == 0) {
			return ItemStack.EMPTY;
		}

		ItemStack itemStack = Inventories.splitStack(getContents(), slot, amount);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	@Override
	public int getMaxCountPerStack() {
		return dankStats.stacklimit;
	}

	public DefaultedList<ItemStack> getContents() {
		return ((SimpleInventoryAccessor)this).getStacks();
	}

	public boolean noValidSlots() {
		return IntStream.range(0,size())
						.mapToObj(this::getStack)
						.allMatch(stack -> stack.isEmpty() || stack.getItem().isIn(Utils.BLACKLISTED_USAGE));
	}

	public boolean isLocked(int slot){
		return lockedSlots[slot] == 1;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return !stack.getItem().isIn(Utils.BLACKLISTED_STORAGE) && super.canInsert(stack);
	}

	public CompoundTag serializeNBT() {
		ListTag nbtTagList = new ListTag();
		for (int i = 0; i < this.getContents().size(); i++) {
			if (!getContents().get(i).isEmpty()) {
				int realCount = Math.min(dankStats.stacklimit, getContents().get(i).getCount());
				CompoundTag itemTag = new CompoundTag();
				itemTag.putInt("Slot", i);
				getContents().get(i).toTag(itemTag);
				itemTag.putInt("ExtendedCount", realCount);
				nbtTagList.add(itemTag);
			}
		}
		CompoundTag nbt = new CompoundTag();
		nbt.putString("DankStats",dankStats.toString());
		nbt.put("Items", nbtTagList);
		nbt.putIntArray("LockedSlots",lockedSlots);
		nbt.putString("stats",dankStats.toString());
		return nbt;
	}

	public void addTank(CompoundTag nbt, ItemStack bag) {
		this.setDankStats(Utils.getStats(bag));
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
		lockedSlots = nbt.getIntArray("LockedSlots");
	}

	public void deserializeNBT(CompoundTag nbt) {
		DankStats stats = DankStats.valueOf(nbt.getString("DankStats"));
		this.setDankStats(stats);
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

	public int calcRedstone() {
		int numStacks = 0;
		float f = 0F;

		for (int slot = 0; slot < this.size(); slot++) {
			ItemStack stack = this.getStack(slot);

			if (!stack.isEmpty()) {
				f += (float) stack.getCount() / (float) this.getMaxCountPerStack();
				numStacks++;
			}
		}

		f /= this.size();
		return MathHelper.floor(f * 14F) + (numStacks > 0 ? 1 : 0);
	}

}
