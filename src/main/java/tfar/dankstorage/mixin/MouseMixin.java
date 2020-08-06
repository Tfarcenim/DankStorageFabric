package tfar.dankstorage.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.event.ClientMixinHooks;

@Mixin(Mouse.class)
public class MouseMixin {

	@Shadow @Final private MinecraftClient client;

	@Inject(method = "onMouseScroll",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z"),cancellable = true)
	private void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
		double delta = (this.client.options.discreteMouseScroll ? Math.signum(horizontal) : vertical) * this.client.options.mouseWheelSensitivity;

		if (ClientMixinHooks.onScroll((Mouse)(Object)this,window,horizontal,vertical,delta))ci.cancel();
	}
}
