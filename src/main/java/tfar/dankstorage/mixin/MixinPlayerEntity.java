package tfar.dankstorage.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.ducks.UseDankStorage;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.utils.Utils;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements UseDankStorage{
  @Shadow @Final public PlayerInventory inventory;

  public boolean useDankStorage = false;

  @Inject(method = "getArrowType", at = @At("HEAD"), cancellable = true)
  private void findAmmo(ItemStack shootable, CallbackInfoReturnable<ItemStack> cir) {
    ItemStack ammo = myFindAmmo(shootable);
    useDankStorage = !ammo.isEmpty();
    if (!ammo.isEmpty()) {
      cir.setReturnValue(ammo);
      cir.cancel();
    }
  }

  private ItemStack myFindAmmo(ItemStack bow) {
    Predicate<ItemStack> predicate = ((RangedWeaponItem) bow.getItem()).getProjectiles();

    ItemStack dank = getDankStorage(bow);

    return Utils.getHandler(dank).getContents().stream()
                    .filter(predicate).findFirst().orElse(ItemStack.EMPTY);
  }

  private ItemStack getDankStorage(ItemStack bow){
    return IntStream.range(0, this.inventory.size()).mapToObj(i -> this.inventory.getStack(i)).filter(stack -> stack.getItem() instanceof DankItem).findFirst().orElse(ItemStack.EMPTY);
  }

  @Override
  public boolean useDankStorage() {
    return useDankStorage;
  }
}
