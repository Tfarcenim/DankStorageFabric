package tfar.dankstorage.recipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.mixin.CraftingContainerAccess;
import tfar.dankstorage.mixin.CraftingMenuAccess;
import tfar.dankstorage.world.DankInventory;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankSavedData;

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

        AbstractContainerMenu menu = ((CraftingContainerAccess)inv).getMenu();


        CompoundTag settings = oldBag.getTag().getCompound("settings");


        int id = settings.contains(Utils.ID, Tag.TAG_INT) ? settings.getInt(Utils.ID) : -1;
        if (menu instanceof CraftingMenu && id > -1) {
            if (!((CraftingMenuAccess) menu).getPlayer().level.isClientSide) {
                ServerLevel level = (ServerLevel) ((CraftingMenuAccess) menu).getPlayer().level;
                DankInventory inventory = DankSavedData.getDefault(level).getInventory(id);
                inventory.setDankStats(Utils.getStats(newBag));
            } else {
                System.out.println("Why is someone trying to craft items clientside, now they won't have the correct data.");
                new RuntimeException().printStackTrace();
            }
        }
        newBag.setTag(oldBag.getTag());
        return newBag;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DankStorage.upgrade;
    }
}
