package tfar.dankstorage.ducks;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface SimpleInventoryAccessor {

    NonNullList<ItemStack> getItems();

    void setItems(NonNullList<ItemStack> stacks);

    void setSize(int slots);

}
