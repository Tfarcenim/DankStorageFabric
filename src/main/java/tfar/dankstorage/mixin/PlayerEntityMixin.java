package tfar.dankstorage.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.ducks.UseDankStorage;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.event.MixinHooks;
import tfar.dankstorage.utils.Utils;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements UseDankStorage {
  @Shadow @Final public PlayerInventory inventory;

  @Shadow public abstract void increaseStat(Stat<?> stat, int amount);

  public boolean useDankStorage = false;

  @Inject(method = "getArrowType", at = @At("HEAD"), cancellable = true)
  private void findAmmo(ItemStack shootable, CallbackInfoReturnable<ItemStack> cir) {
    ItemStack ammo = MixinHooks.myFindAmmo((PlayerEntity)(Object)this,shootable);
    useDankStorage = !ammo.isEmpty();
    if (!ammo.isEmpty()) {
      cir.setReturnValue(ammo);
      cir.cancel();
    }
  }

  @Override
  public boolean useDankStorage() {
    return useDankStorage;
  }
}
