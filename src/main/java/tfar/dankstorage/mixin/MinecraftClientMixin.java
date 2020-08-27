package tfar.dankstorage.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.event.ClientMixinEvents;
import tfar.dankstorage.network.server.C2SMessagePickBlock;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

	@Shadow @Nullable public LocalPlayer player;

	@Inject(method = "pickBlock",at = @At("HEAD"),cancellable = true)
	private void dankPickBlock(CallbackInfo ci){
		if (Utils.isHoldingDank(player)) {
			int slot = ClientMixinEvents.pickItemFromDank(player.getMainHandItem());
			if (slot != -1) {
				C2SMessagePickBlock.send(slot);
				ci.cancel();
			}
		}
	}
}
