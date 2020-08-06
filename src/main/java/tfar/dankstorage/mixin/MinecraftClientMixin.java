package tfar.dankstorage.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.network.server.C2SMessagePickBlock;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nullable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Shadow @Nullable public ClientPlayerEntity player;

	@Inject(method = "doItemPick",at = @At("HEAD"),cancellable = true)
	private void dankPickBlock(CallbackInfo ci){
		if (Utils.isHoldingDank(player)) {
			C2SMessagePickBlock.send();
			ci.cancel();
		}
	}
}
