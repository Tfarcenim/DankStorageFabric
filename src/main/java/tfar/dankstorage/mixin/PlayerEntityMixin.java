package tfar.dankstorage.mixin;

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
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Player.class)
public abstract class PlayerEntityMixin implements UseDankStorage {
  @Shadow @Final public Inventory inventory;
  
  public boolean useDankStorage = false;

  @Inject(method = "getProjectile", at = @At("HEAD"), cancellable = true)
  private void findAmmo(ItemStack shootable, CallbackInfoReturnable<ItemStack> cir) {
    ItemStack ammo = MixinHooks.myFindAmmo((Player)(Object)this,shootable);
    useDankStorage = !ammo.isEmpty();
    if (!ammo.isEmpty()) {
      cir.setReturnValue(ammo);
    }
  }

  @Override
  public boolean useDankStorage() {
    return useDankStorage;
  }
}
