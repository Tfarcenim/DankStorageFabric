package tfar.dankstorage.recipe;

import tfar.dankstorage.DankStorage;
import tfar.dankstorage.utils.Utils;
import javax.annotation.Nonnull;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class UpgradeRecipe extends ShapedRecipe {

  public UpgradeRecipe(ShapedRecipe recipe){
    super(recipe.getId(),"dank",recipe.getWidth(),recipe.getHeight(),recipe.getIngredients(),recipe.getResultItem());
  }

  @Nonnull
  @Override
  public ItemStack assemble(CraftingContainer inv) {
    ItemStack bag = super.assemble(inv).copy();
    ItemStack oldBag = inv.getItem(4).copy();
    if (!oldBag.hasTag())return bag;
    bag.setTag(oldBag.getTag());
    bag.getOrCreateTagElement(Utils.INV).putInt("Size",Utils.getSlotCount(bag));
    return bag;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return DankStorage.upgrade;
  }
}
