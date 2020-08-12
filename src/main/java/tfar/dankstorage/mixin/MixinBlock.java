package tfar.dankstorage.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.utils.Utils;

import java.util.List;

@Mixin(Block.class)
public class MixinBlock {
  @Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",at = @At("HEAD"),cancellable = true)
  private static void hijacklootcontextbuilder(BlockState state, ServerWorld worldIn,
                                       BlockPos pos, BlockEntity tileEntityIn,
                                       Entity entityIn, ItemStack tool,
                                       CallbackInfoReturnable<List<ItemStack>> drops){
    if (Utils.isConstruction(tool)) {
      drops.setReturnValue(newlootcontext(state, worldIn, pos, tileEntityIn, entityIn, tool));
      drops.cancel();
    }
  }
  private static List<ItemStack> newlootcontext(BlockState state, ServerWorld worldIn,
                                     BlockPos pos, BlockEntity tileEntityIn,
                                     Entity entityIn, ItemStack dank){
    ItemStack tool = Utils.getItemStackInSelectedSlot(dank);
    LootContext.Builder lootcontext$builder = (new LootContext.Builder(worldIn)).random(worldIn.random).parameter(LootContextParameters.TOOL, tool).optionalParameter(LootContextParameters.THIS_ENTITY, entityIn).optionalParameter(LootContextParameters.BLOCK_ENTITY, tileEntityIn);
    return state.getDroppedStacks(lootcontext$builder);
  }

}
