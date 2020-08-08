package tfar.dankstorage.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.container.DankContainer;
import tfar.dankstorage.ducks.UseDankStorage;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.PortableDankInventory;
import tfar.dankstorage.utils.ItemHandlerHelper;
import tfar.dankstorage.utils.Mode;
import tfar.dankstorage.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class MixinHooks {
	public static <T extends LivingEntity> void actuallyBreakItem(int p_222118_1_, T livingEntity, Consumer<T> p_222118_3_, CallbackInfo ci) {
		ItemStack actualStack = livingEntity.getMainHandStack();
		if (actualStack.getItem() instanceof DankItem && Utils.isConstruction(actualStack)) {
			Utils.getHandler(actualStack).removeStack(Utils.getSelectedSlot(actualStack), 1);
		}
	}

	/**
	 * @param inv      Player Inventory to add the item to
	 * @param incoming the itemstack being picked up
	 * @return if the item was completely picked up by the dank(s)
	 */
	public static boolean interceptItem(PlayerInventory inv, ItemStack incoming) {
		PlayerEntity player = inv.player;
		if (player.currentScreenHandler instanceof DankContainer) {
			return false;
		}
		for (int i = 0; i < inv.size(); i++) {
			ItemStack possibleDank = inv.getStack(i);
			if (possibleDank.getItem() instanceof DankItem && onItemPickup(player, incoming, possibleDank)) {
				return true;
			}
		}
		return false;
	}

	public static ItemStack myFindAmmo(PlayerEntity player, ItemStack bow) {
		Predicate<ItemStack> predicate = ((RangedWeaponItem) bow.getItem()).getProjectiles();

		ItemStack dank = getDankStorage(player);
		if (!dank.isEmpty())
			return Utils.getHandler(dank).getContents().stream()
							.filter(predicate).findFirst().orElse(ItemStack.EMPTY);
		return ItemStack.EMPTY;
	}

	private static ItemStack getDankStorage(PlayerEntity player){
		return IntStream.range(0, player.inventory.size()).mapToObj(player.inventory::getStack)
						.filter(stack -> stack.getItem() instanceof DankItem).findFirst()
						.orElse(ItemStack.EMPTY);
	}

	public static void onStoppedUsing(ItemStack bow, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof PlayerEntity && !worldIn.isClient){
			PlayerEntity player = (PlayerEntity) entityLiving;
			Predicate<ItemStack> predicate = ((RangedWeaponItem) bow.getItem()).getProjectiles();
			if (((UseDankStorage)player).useDankStorage() && !player.abilities.creativeMode) {
				ItemStack dank = getDankStorage(player);
				DankInventory dankInventory = Utils.getHandler(dank);
				for (int i = 0;i < dankInventory.size();i++){
					ItemStack stack = dankInventory.getStack(i);
					if (predicate.test(stack)){
						dankInventory.removeStack(i,1);
						break;
					}
				}
			}
		}
	}

	public static boolean onItemPickup(PlayerEntity player, ItemStack pickup, ItemStack dank) {

		Mode mode = Utils.getMode(dank);
		if (mode == Mode.NORMAL) return false;
		PortableDankInventory inv = Utils.getHandler(dank);
		int count = pickup.getCount();
		boolean oredict = Utils.oredict(dank);
		List<ItemStack> existing = new ArrayList<>();
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getStack(i);
			if (stack.isEmpty()) {

			} else {
				boolean exists = false;
				for (ItemStack stack1 : existing) {
					if (areItemStacksCompatible(stack, stack1, oredict)) {
						exists = true;
					}
				}
				if (!exists) {
					existing.add(stack.copy());
				}
			}
		}

		switch (mode) {
			case PICKUP_ALL: {
				for (int i = 0; i < inv.size(); i++) {
					allPickup(inv, i, pickup, false, oredict);
					if (pickup.isEmpty())break;
				}
			}
			break;

			case FILTERED_PICKUP: {
				for (int i = 0; i < inv.size(); i++) {
					filteredPickup(inv, i, pickup, false, oredict, existing);
				}
			}
			break;

			case VOID_PICKUP: {
				for (int i = 0; i < inv.size(); i++) {
					voidPickup(inv, i, pickup, false, oredict, existing);
				}
			}
			break;
		}

		//leftovers
		pickup.setCount(pickup.getCount());
		if (pickup.getCount() != count) {
			dank.setCooldown(5);
			player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
			inv.markDirty();
		}
		return pickup.isEmpty();
	}

	public static void voidPickup(PortableDankInventory inv, int slot, ItemStack toInsert, boolean simulate, boolean oredict, List<ItemStack> filter) {
		ItemStack existing = inv.getStack(slot);

		if (doesItemStackExist(toInsert, filter, oredict) && areItemStacksCompatible(existing,toInsert,oredict)) {
			int stackLimit = inv.dankStats.stacklimit;
			int total = toInsert.getCount() + existing.getCount();
			//doesn't matter if it overflows cause it's all gone lmao
			if (!simulate) {
				inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(existing, Math.min(total,stackLimit)));
				toInsert.setCount(0);
			}
		}
	}

	public static void allPickup(PortableDankInventory inv, int slot, ItemStack toInsert, boolean simulate, boolean oredict) {
		ItemStack existing = inv.getStack(slot);

		if (existing.isEmpty()) {
			int stackLimit = inv.dankStats.stacklimit;
			int total = toInsert.getCount();
			int remainder = total - stackLimit;
			//no overflow
			if (remainder <= 0) {
				if (!simulate) inv.getContents().set(slot, toInsert.copy());
				toInsert.setCount(0);
			} else {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
				toInsert.setCount(remainder);
			}
			return;
		}

		if (ItemHandlerHelper.canItemStacksStack(toInsert, existing) || (oredict && Utils.areItemStacksConvertible(toInsert, existing))) {
			int stackLimit = inv.dankStats.stacklimit;
			int total = toInsert.getCount() + existing.getCount();
			int remainder = total - stackLimit;
			//no overflow
			if (remainder <= 0) {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
				toInsert.setCount(0);
			} else {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
				toInsert.setCount(remainder);
			}
		}
	}

	public static void filteredPickup(PortableDankInventory inv, int slot, ItemStack toInsert, boolean simulate, boolean oredict, List<ItemStack> filter) {
		ItemStack existing = inv.getStack(slot);

		if (existing.isEmpty() && doesItemStackExist(toInsert,filter,oredict)) {
			int stackLimit = inv.dankStats.stacklimit;
			int total = toInsert.getCount();
			int remainder = total - stackLimit;
			//no overflow
			if (remainder <= 0) {
				if (!simulate) inv.getContents().set(slot, toInsert.copy());
				toInsert.setCount(0);
			} else {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
				toInsert.setCount(remainder);
			}
			return;
		}

		if (doesItemStackExist(toInsert, filter, oredict) && areItemStacksCompatible(existing,toInsert,oredict)) {
			int stackLimit = inv.dankStats.stacklimit;
			int total = toInsert.getCount() + existing.getCount();
			int remainder = total - stackLimit;
			//no overflow
			if (remainder <= 0) {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
				toInsert.setCount(0);
			} else {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
				toInsert.setCount(remainder);
			}
		}
	}

	public static boolean areItemStacksCompatible(ItemStack stackA, ItemStack stackB, boolean oredict) {
		return oredict ? ItemStack.areTagsEqual(stackA, stackB) && ItemStack.areItemsEqualIgnoreDamage(stackA, stackB) || Utils.areItemStacksConvertible(stackA, stackB) :
						ItemStack.areTagsEqual(stackA, stackB) && ItemStack.areItemsEqualIgnoreDamage(stackA, stackB);
	}

	public static boolean doesItemStackExist(ItemStack stack, List<ItemStack> filter, boolean oredict) {
		for (ItemStack filterStack : filter) {
			if (areItemStacksCompatible(stack, filterStack, oredict)) return true;
		}
		return false;
	}
}
