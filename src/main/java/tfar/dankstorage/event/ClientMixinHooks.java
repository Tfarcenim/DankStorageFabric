package tfar.dankstorage.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerEntity;
import tfar.dankstorage.network.server.C2SMessageScrollSlot;
import tfar.dankstorage.utils.Utils;

public class ClientMixinHooks {
	public static boolean onScroll(Mouse mouse, long window, double horizontal, double vertical,double delta) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null && player.isInSneakingPose() && (Utils.isConstruction(player.getMainHandStack()) || Utils.isConstruction(player.getOffHandStack()))) {
			boolean right = delta < 0;
			C2SMessageScrollSlot.send(right);
			return true;
		}
		return false;
	}
}
