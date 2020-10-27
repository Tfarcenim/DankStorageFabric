package tfar.dankstorage.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin {/*
	@Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V",at = @At(value = "INVOKE",target = "",shift = At.Shift.AFTER))
	private <T extends LivingEntity> void actuallyBreakItem(int p_222118_1_, T livingEntity, Consumer<T> p_222118_3_, CallbackInfo ci){
		MixinHooks.actuallyBreakItem(p_222118_1_,livingEntity,p_222118_3_,ci);
	}*/
}
