package tfar.dankstorage.utils;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.container.AbstractDankContainer;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.PortableDankInventory;
import tfar.dankstorage.network.server.C2SMessageToggleUseType;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static tfar.dankstorage.network.server.C2SMessageTogglePickup.modes;
import static tfar.dankstorage.network.server.C2SMessageToggleUseType.useTypes;

public class Utils {

	public static final Tag<Item> BLACKLISTED_STORAGE = TagRegistry.item(new Identifier(DankStorage.MODID, "blacklisted_storage"));
	public static final Tag<Item> BLACKLISTED_USAGE = TagRegistry.item(new Identifier(DankStorage.MODID, "blacklisted_usage"));

	public static final Tag<Item> WRENCHES = TagRegistry.item(new Identifier("forge", "wrenches"));

	public static final String INV = "inv";

	public static Mode getMode(ItemStack bag) {
		return modes[bag.getOrCreateTag().getInt("mode")];
	}

	public static boolean isConstruction(ItemStack bag) {
		return bag.getItem() instanceof DankItem && bag.hasTag()
						&& bag.getTag().contains("construction")
						&& bag.getTag().getInt("construction") == C2SMessageToggleUseType.UseType.construction.ordinal();
	}

	public static DankStats getStatsfromRows(int rows) {
		switch (rows) {
			case 1:return DankStats.one;
			case 2:return DankStats.two;
			case 3:return DankStats.three;
			case 4:return DankStats.four;
			case 5:return DankStats.five;
			case 6:return DankStats.six;
			case 9:return DankStats.seven;
		}
		throw new IllegalStateException(String.valueOf(rows));
	}

	//0,1,2,3
	public static void cycleMode(ItemStack bag, PlayerEntity player) {
		int ordinal = bag.getOrCreateTag().getInt("mode");
		ordinal++;
		if (ordinal > modes.length - 1) ordinal = 0;
		bag.getOrCreateTag().putInt("mode", ordinal);
		player.sendMessage(
						new TranslatableText("dankstorage.mode." + modes[ordinal].name()), true);
	}

	public static C2SMessageToggleUseType.UseType getUseType(ItemStack bag) {
		return useTypes[bag.getOrCreateTag().getInt("construction")];
	}

	//0,1,2
	public static void cyclePlacement(ItemStack bag, PlayerEntity player) {
		int ordinal = bag.getOrCreateTag().getInt("construction");
		ordinal++;
		if (ordinal >= useTypes.length) ordinal = 0;
		bag.getOrCreateTag().putInt("construction", ordinal);
		player.sendMessage(
						new TranslatableText("dankstorage.usetype." + useTypes[ordinal].name()), true);
	}

	public static int getSelectedSlot(ItemStack bag) {
		return bag.getOrCreateTag().getInt("selectedSlot");
	}

	public static void setSelectedSlot(ItemStack bag, int slot) {
		bag.getOrCreateTag().putInt("selectedSlot", slot);
	}


	public static void sort(PlayerEntity player) {
		if (player == null) return;
		ScreenHandler openContainer = player.currentScreenHandler;
		if (openContainer instanceof AbstractDankContainer) {
			List<SortingData> itemlist = new ArrayList<>();
			DankInventory handler = ((AbstractDankContainer) openContainer).dankInventory;

			for (int i = 0; i < handler.size(); i++) {
				ItemStack stack = handler.getStack(i);
				if (stack.isEmpty()) continue;
				boolean exists = SortingData.exists(itemlist, stack.copy());
				if (exists) {
					int rem = SortingData.addToList(itemlist, stack.copy());
					if (rem > 0) {
						ItemStack bigstack = stack.copy();
						bigstack.setCount(Integer.MAX_VALUE);
						ItemStack smallstack = stack.copy();
						smallstack.setCount(rem);
						itemlist.add(new SortingData(bigstack));
						itemlist.add(new SortingData(smallstack));
					}
				} else {
					itemlist.add(new SortingData(stack.copy()));
				}
			}
			handler.getContents().clear();
			Collections.sort(itemlist);
			for (SortingData data : itemlist) {
				ItemStack stack = data.stack.copy();
				ItemStack rem = stack.copy();
				for (int i = 0; i < handler.size(); i++) {
					rem = handler.addStack(rem);
					if (rem.isEmpty()) break;
				}
			}
		}
	}

	public static int getStackLimit(ItemStack bag) {
		return getStats(bag).stacklimit;
	}

	public static int getSlotCount(ItemStack bag) {
		return getStats(bag).slots;
	}

	public static DankStats getStats(ItemStack bag) {
		return ((DankItem)bag.getItem()).stats;
	}

	public static void changeSlot(ItemStack bag, boolean right) {
		PortableDankInventory handler = getHandler(bag);
			//don't change slot if empty
		if (handler.noValidSlots()) return;
			int selectedSlot = getSelectedSlot(bag);
			int size = handler.size();
			//keep iterating until a valid slot is found (not empty and not blacklisted from usage)
			while (true) {
				if (right) {
					selectedSlot++;
					if (selectedSlot >= size) selectedSlot = 0;
				} else {
					selectedSlot--;
					if (selectedSlot < 0) selectedSlot = size - 1;
				}
				if (!handler.getStack(selectedSlot).isEmpty() && !handler.getStack(selectedSlot).getItem().isIn(BLACKLISTED_USAGE))
					break;
			}
			setSelectedSlot(bag, selectedSlot);
		}

	public static boolean oredict(ItemStack bag) {
		return bag.getItem() instanceof DankItem && bag.hasTag() && bag.getTag().getBoolean("tag");
	}

	public static PortableDankInventory getHandler(ItemStack bag) {
		return new PortableDankInventory(bag);
	}

	public static int getNbtSize(ItemStack stack) {
		return getNbtSize(stack.getTag());
	}

	public static DankItem getItemFromTier(int tier) {
		return (DankItem) Registry.ITEM.get(new Identifier(DankStorage.MODID, "dank_" + tier));
	}

	public static int getNbtSize(@Nullable CompoundTag nbt) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		buffer.writeCompoundTag(nbt);
		buffer.release();
		return buffer.writerIndex();
	}

	public static ItemStack getItemStackInSelectedSlot(ItemStack bag) {
		PortableDankInventory inv = getHandler(bag);
		ItemStack stack = inv.getStack(Utils.getSelectedSlot(bag));
		return stack.getItem().isIn(BLACKLISTED_USAGE) ? ItemStack.EMPTY : stack;
	}

	public static final Set<Identifier> taglist = new HashSet<>();

	public static boolean areItemStacksConvertible(final ItemStack stack1, final ItemStack stack2) {
		if (stack1.hasTag() || stack2.hasTag()) return false;
		Set<Identifier> taglistofstack1 = getTags(stack1.getItem());
		Set<Identifier> taglistofstack2 = getTags(stack2.getItem());

		Set<Identifier> commontags = new HashSet<>(taglistofstack1);
		commontags.retainAll(taglistofstack2);
		commontags.retainAll(taglist);
		return !commontags.isEmpty();
	}

	public static Set<Identifier> getTags(Item item) {
		return ItemTags.getContainer().getEntries().entrySet().stream().filter(identifierTagEntry -> identifierTagEntry.getValue().contains(item))
						.map(Map.Entry::getKey).collect(Collectors.toSet());
	}

	public static boolean isHoldingDank(PlayerEntity player) {
		ItemStack stack = player.getMainHandStack();
		if (stack.getItem() instanceof DankItem)return true;
		stack = player.getOffHandStack();
		return stack.getItem() instanceof DankItem;
	}

	public static boolean canMerge(ItemStack first, ItemStack second, Inventory inventory) {
		if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getDamage() != second.getDamage()) {
			return false;
		} else if (first.getCount() > inventory.getMaxCountPerStack()) {
			return false;
		} else {
			return ItemStack.areTagsEqual(first, second);
		}
	}

	public static boolean DEV;

	static {
		try {
			Items.class.getField("field_8036");//empty in ItemStack
			DEV = false;
		} catch (NoSuchFieldException e) {
			DEV = true;
		}
	}
}
