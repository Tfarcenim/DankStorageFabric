package tfar.dankstorage.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.DankItem;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

	@Shadow private int age;

	@Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V",at = @At("RETURN"))
	private void noDespawn(World world, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
		if (stack.getItem() instanceof DankItem) {
			this.age = -32768;
		}
	}
}
