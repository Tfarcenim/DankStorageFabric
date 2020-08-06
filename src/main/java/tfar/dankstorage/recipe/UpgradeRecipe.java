package tfar.dankstorage.recipe;

import tfar.dankstorage.DankStorage;
import tfar.dankstorage.utils.Utils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import javax.annotation.Nonnull;

public class UpgradeRecipe extends ShapedRecipe {

  public UpgradeRecipe(ShapedRecipe recipe){
    super(recipe.getId(),recipe.getGroup(),recipe.getWidth(),recipe.getHeight(),recipe.getPreviewInputs(),recipe.getOutput());
  }

  @Nonnull
  @Override
  public ItemStack craft(CraftingInventory inv) {
    ItemStack bag = super.craft(inv).copy();
    ItemStack oldBag = inv.getStack(4).copy();
    if (!oldBag.hasTag())return bag;
    bag.setTag(oldBag.getTag());
    bag.getOrCreateSubTag(Utils.INV).putInt("Size",Utils.getSlotCount(bag));
    return bag;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return DankStorage.upgrade;
  }
}
