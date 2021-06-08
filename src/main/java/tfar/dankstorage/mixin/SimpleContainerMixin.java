package tfar.dankstorage.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import tfar.dankstorage.ducks.SimpleInventoryAccessor;

@Mixin(SimpleContainer.class)
public class SimpleContainerMixin implements SimpleInventoryAccessor {


    @Shadow @Final @Mutable private NonNullList<ItemStack> items;

    @Shadow @Final @Mutable private int size;

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void setItems(NonNullList<ItemStack> stacks) {
        this.items = stacks;
    }

    @Override
    public void setSize(int slots) {
        this.size = slots;
    }
}
