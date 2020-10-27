package tfar.dankstorage.recipe;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nonnull;

public class UpgradeRecipe extends ShapedRecipe {

    public UpgradeRecipe(ShapedRecipe recipe) {
        super(recipe.getId(), "dank", recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem());
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack newBag = super.assemble(inv).copy();
        ItemStack oldBag = inv.getItem(4);
        if (!oldBag.hasTag()) return newBag;
        newBag.setTag(oldBag.getTag());
        DankInventory dankInventory = Utils.getHandler(newBag);
        dankInventory.setDankStats(Utils.getStats(newBag));

        return newBag;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DankStorage.upgrade;
    }
}
