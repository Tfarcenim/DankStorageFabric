package tfar.dankstorage.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.event.ClientMixinEvents;

@Mixin(MouseHandler.class)
public class MouseMixin
{

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"), cancellable = true)
    private void onScroll(long window, double horizontal, double vertical, CallbackInfo ci)
    {
        double delta = (this.minecraft.options.discreteMouseScroll ? Math.signum(horizontal) : vertical) * this.minecraft.options.mouseWheelSensitivity;

        if (ClientMixinEvents.onScroll((MouseHandler) (Object) this, window, horizontal, vertical, delta)) ci.cancel();
    }
}
