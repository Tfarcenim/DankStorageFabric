package tfar.dankstorage.event;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.dankstorage.inventory.PortableDankInventory;
import tfar.dankstorage.network.server.C2SMessageScrollSlot;
import tfar.dankstorage.utils.Utils;

public class ClientMixinEvents {

	public static final MinecraftClient mc = MinecraftClient.getInstance();
	private static final Logger LOGGER = LogManager.getLogger();

	public static boolean onScroll(Mouse mouse, long window, double horizontal, double vertical,double delta) {
		PlayerEntity player = mc.player;
		if (player != null && player.isInSneakingPose() && (Utils.isConstruction(player.getMainHandStack()) || Utils.isConstruction(player.getOffHandStack()))) {
			boolean right = delta < 0;
			C2SMessageScrollSlot.send(right);
			return true;
		}
		return false;
	}

	public static int pickItemFromDank(ItemStack bag) {
		PortableDankInventory handler = Utils.getHandler(bag);
		PlayerEntity player = mc.player;
		ItemStack pickblock = onPickBlock(player.rayTrace(20.0D,0,false),player,player.world);
		int slot = -1;
		if (!pickblock.isEmpty())
			for (int i = 0; i < handler.size(); i++) {
				if (pickblock.getItem() == handler.getStack(i).getItem()){
					slot = i;
					break;
				}
			}
		return slot;
	}

	public static ItemStack onPickBlock(HitResult target, PlayerEntity player, World world) {
		ItemStack result = ItemStack.EMPTY;

		if (target.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ((BlockHitResult) target).getBlockPos();
			BlockState state = world.getBlockState(pos);

			if (state.isAir()) return ItemStack.EMPTY;
			result = state.getBlock().getPickStack(world, pos, state);

			if (result.isEmpty())
				LOGGER.warn("Picking on: [{}] {} gave null item", target.getType(), state.getBlock());
		}
		return result;
	}
}
