package tfar.dankstorage.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import javax.annotation.Nonnull;

public class DankSlot extends Slot {

  public DankSlot(DankInventory itemHandler, int index, int xPosition, int yPosition) {
    super(itemHandler, index, xPosition, yPosition);
  }

  @Override
  public boolean canTakeItems(PlayerEntity playerIn) {
    return true;
  }

  @Override
  public int getMaxStackAmount(@Nonnull ItemStack stack) {
    return this.inventory.getMaxCountPerStack();
  }
}