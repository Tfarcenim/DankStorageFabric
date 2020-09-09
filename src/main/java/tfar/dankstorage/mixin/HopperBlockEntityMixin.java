package tfar.dankstorage.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.tile.DockBlockEntity;
import tfar.dankstorage.utils.Utils;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin
{

    /**
     * @param inv
     * @param slot
     * @param cir
     * @see HopperBlockEntity#isFullContainer(Container, Direction)
     */
    @Inject(method = {"method_17769", "lambda$isFullContainer$1(Lnet/minecraft/world/Container;I)Z"}, at = @At("HEAD"), cancellable = true, remap = false)
    private static void patchHopper(Container inv, int slot, CallbackInfoReturnable<Boolean> cir)
    {
        if (inv instanceof DockBlockEntity) {
            ItemStack stack = inv.getItem(slot);
            if (inv.getMaxStackSize() > stack.getCount()) cir.setReturnValue(false);
        }
    }

    @Inject(method = "tryMoveInItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;isEmpty()Z"))
    private static void patchHopper1(Container from, Container to, ItemStack stack, int slot, Direction direction, CallbackInfoReturnable<ItemStack> cir)
    {
        if (to instanceof DockBlockEntity) {
            ItemStack itemStack = to.getItem(slot);
            if (itemStack.isEmpty()) {
                //vanilla
            } else if (Utils.canMerge(itemStack, stack, to)) {//the patch
                int i = to.getMaxStackSize() - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemStack.grow(j);
                if (j > 0) {
                    to.setChanged();
                }
            }
        }
    }
}
