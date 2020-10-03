package tfar.dankstorage.container;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CappedSlot extends Slot {
    public CappedSlot(Container container, int i, int j, int k) {
        super(container, i, j, k);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(getMaxStackSize(),stack.getMaxStackSize());
    }
}
