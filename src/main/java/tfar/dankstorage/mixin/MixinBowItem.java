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

import java.util.function.Predicate;
import java.util.stream.IntStream;

@Mixin(BowItem.class)
public class MixinBowItem {
  @Inject(method = "onStoppedUsing",at = @At("TAIL"))
  private void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft, CallbackInfo callbackInfo){
    a(stack, worldIn, entityLiving, timeLeft);
  }

  private void a(ItemStack bow, World worldIn, LivingEntity entityLiving, int timeLeft){
    if (entityLiving instanceof PlayerEntity && !worldIn.isClient){
      PlayerEntity player = (PlayerEntity) entityLiving;
      Predicate<ItemStack> predicate = ((RangedWeaponItem) bow.getItem()).getProjectiles();
      if (((UseDankStorage)player).useDankStorage() && !player.abilities.creativeMode) {
        /*ItemStack dank = getDankStorage(player);
        dank.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
          for (int i = 0;i < iItemHandler.getSlots();i++){
            ItemStack stack = iItemHandler.getStackInSlot(i);
            if (predicate.test(stack)){
              iItemHandler.extractItem(i,1,false);
              break;
            }
          }
        });*/
      }
    }
  }

  private ItemStack getDankStorage(PlayerEntity player){
    return IntStream.range(0, player.inventory.size()).mapToObj(i -> player.inventory.getStack(i)).filter(stack -> stack.getItem() instanceof DankItem).findFirst().orElse(ItemStack.EMPTY);
  }

}
