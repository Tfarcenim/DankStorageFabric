package tfar.dankstorage.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.utils.Utils;

import java.util.List;

@Mixin(Block.class)
public class MixinBlock {
    @Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private static void hijacklootcontextbuilder(BlockState state, ServerLevel worldIn,
                                                 BlockPos pos, BlockEntity tileEntityIn,
                                                 Entity entityIn, ItemStack tool,
                                                 CallbackInfoReturnable<List<ItemStack>> drops) {
        if (Utils.isConstruction(tool)) {
            drops.setReturnValue(newlootcontext(state, worldIn, pos, tileEntityIn, entityIn, tool));
            drops.cancel();
        }
    }

    private static List<ItemStack> newlootcontext(BlockState state, ServerLevel worldIn,
                                                  BlockPos pos, BlockEntity tileEntityIn,
                                                  Entity entityIn, ItemStack dank) {
        ItemStack tool = Utils.getItemStackInSelectedSlot(dank,worldIn);
        LootContext.Builder lootcontext$builder = new LootContext.Builder(worldIn).withRandom(worldIn.random).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withOptionalParameter(LootContextParams.THIS_ENTITY, entityIn).withOptionalParameter(LootContextParams.BLOCK_ENTITY, tileEntityIn);
        return state.getDrops(lootcontext$builder);
    }

}
