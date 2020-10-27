package tfar.dankstorage.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.dankstorage.inventory.PortableDankInventory;
import tfar.dankstorage.network.server.C2SMessageScrollSlot;
import tfar.dankstorage.utils.Utils;

public class ClientMixinEvents {

    public static final Minecraft mc = Minecraft.getInstance();
    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean onScroll(MouseHandler mouse, long window, double horizontal, double vertical, double delta) {
        Player player = mc.player;
        if (player != null && player.isCrouching() && (Utils.isConstruction(player.getMainHandItem()) || Utils.isConstruction(player.getOffhandItem()))) {
            boolean right = delta < 0;
            C2SMessageScrollSlot.send(right);
            return true;
        }
        return false;
    }

    public static int pickItemFromDank(Player player) {
        InteractionHand hand = Utils.getHandWithDank(player);
        if (hand == null) return -1;
        ItemStack bag = player.getItemInHand(hand);
        PortableDankInventory handler = Utils.getHandler(bag);
        ItemStack pickblock = onPickBlock(player.pick(20.0D, 0, false), player, player.level);
        int slot = -1;
        if (!pickblock.isEmpty())
            for (int i = 0; i < handler.getContainerSize(); i++) {
                if (pickblock.getItem() == handler.getItem(i).getItem()) {
                    slot = i;
                    break;
                }
            }
        return slot;
    }

    public static ItemStack onPickBlock(HitResult target, Player player, Level world) {
        ItemStack result = ItemStack.EMPTY;

        if (target.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) target).getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (state.isAir()) return ItemStack.EMPTY;
            result = state.getBlock().getCloneItemStack(world, pos, state);

            if (result.isEmpty())
                LOGGER.warn("Picking on: [{}] {} gave null item", target.getType(), state.getBlock());
        }
        return result;
    }
}
