package tfar.dankstorage.mixin;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(BowItem.class)
public class MixinBowItem {
  @Inject(method = "releaseUsing",at = @At("TAIL"))
  private void onPlayerStoppedUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft, CallbackInfo callbackInfo){
    MixinHooks.onStoppedUsing(stack, worldIn, entityLiving, timeLeft);
  }



  private ItemStack getDankStorage(Player player){
    return IntStream.range(0, player.inventory.getContainerSize()).mapToObj(i -> player.inventory.getItem(i)).filter(stack -> stack.getItem() instanceof DankItem).findFirst().orElse(ItemStack.EMPTY);
  }

}
