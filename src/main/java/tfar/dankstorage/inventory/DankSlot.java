package tfar.dankstorage.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class DankSlot extends Slot
{

    public DankSlot(DankInventory itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPickup(Player playerIn)
    {
        return true;
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack)
    {
        return this.container.getMaxStackSize();
    }
}