package tfar.dankstorage.recipe;

import com.google.gson.JsonObject;
import javax.annotation.Nonnull;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;

public class Serializer2 extends ShapedRecipe.Serializer {
   @Override
    public UpgradeRecipe read(Identifier location, JsonObject json) {
      return new UpgradeRecipe(super.read(location,json));
   }

  @Override
  @Nonnull
  @SuppressWarnings("ConstantConditions")
  public UpgradeRecipe read(@Nonnull Identifier p_199426_1_, PacketByteBuf p_199426_2_) {
    return new UpgradeRecipe(super.read(p_199426_1_, p_199426_2_));
  }
}