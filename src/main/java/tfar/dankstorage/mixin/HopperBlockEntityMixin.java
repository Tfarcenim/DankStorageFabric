package tfar.dankstorage.mixin;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.tile.DankBlockEntity;
import tfar.dankstorage.utils.Utils;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

	/**
	 * @param inv
	 * @param slot
	 * @param cir
	 * @see HopperBlockEntity#isInventoryFull(Inventory, Direction)
	 */
	@Inject(method = "method_17769", at = @At("HEAD"), cancellable = true)
	private static void patchHopper(Inventory inv, int slot, CallbackInfoReturnable<Boolean> cir) {
		if (inv instanceof DankBlockEntity) {
			ItemStack stack = inv.getStack(slot);
			if (inv.getMaxCountPerStack() > stack.getCount()) cir.setReturnValue(false);
		}
	}

	@Inject(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;",
					at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;isEmpty()Z"), cancellable = true)
	private static void patchHopper1(Inventory from, Inventory to, ItemStack stack, int slot, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
		if (to instanceof DankBlockEntity) {
			ItemStack itemStack = to.getStack(slot);
			if (itemStack.isEmpty()) {
				//vanilla
			} else if (Utils.canMerge(itemStack, stack, to)) {//the patch
				int i = to.getMaxCountPerStack() - itemStack.getCount();
				int j = Math.min(stack.getCount(), i);
				stack.decrement(j);
				itemStack.increment(j);
				if (j > 0) {
					to.markDirty();
				}
			}
		}
	}
}
