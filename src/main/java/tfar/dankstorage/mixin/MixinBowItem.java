package tfar.dankstorage.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.event.MixinHooks;
import tfar.dankstorage.item.DankItem;

import java.util.stream.IntStream;

@Mixin(BowItem.class)
public class MixinBowItem
{
    @Inject(method = "releaseUsing", at = @At("TAIL"))
    private void onPlayerStoppedUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft, CallbackInfo callbackInfo)
    {
        MixinHooks.onStoppedUsing(stack, worldIn, entityLiving, timeLeft);
    }


    private ItemStack getDankStorage(Player player)
    {
        return IntStream.range(0, player.inventory.getContainerSize()).mapToObj(player.inventory::getItem).filter(stack -> stack.getItem() instanceof DankItem).findFirst().orElse(ItemStack.EMPTY);
    }

}
