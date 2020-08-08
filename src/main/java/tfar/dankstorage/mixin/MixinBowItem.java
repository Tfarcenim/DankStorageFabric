package tfar.dankstorage.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.ducks.UseDankStorage;
import tfar.dankstorage.event.MixinHooks;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.utils.Utils;

import java.util.function.Predicate;
import java.util.stream.IntStream;

@Mixin(BowItem.class)
public class MixinBowItem {
  @Inject(method = "onStoppedUsing",at = @At("TAIL"))
  private void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft, CallbackInfo callbackInfo){
    MixinHooks.onStoppedUsing(stack, worldIn, entityLiving, timeLeft);
  }



  private ItemStack getDankStorage(PlayerEntity player){
    return IntStream.range(0, player.inventory.size()).mapToObj(i -> player.inventory.getStack(i)).filter(stack -> stack.getItem() instanceof DankItem).findFirst().orElse(ItemStack.EMPTY);
  }

}
