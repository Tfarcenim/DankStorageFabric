package tfar.dankstorage.mixin;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleInventory.class)
public interface SimpleInventoryAccessor {

	@Accessor
	DefaultedList<ItemStack> getStacks();

	@Accessor
	void setStacks(DefaultedList<ItemStack> stacks);

	@Accessor void setSize(int slots);

}
