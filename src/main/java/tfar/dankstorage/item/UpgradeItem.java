package tfar.dankstorage.item;

import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.tile.DankBlockEntity;
import tfar.dankstorage.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class UpgradeItem extends Item {

  protected final UpgradeInfo upgradeInfo;

  public UpgradeItem(Settings properties, UpgradeInfo info) {
    super(properties);
    this.upgradeInfo = info;
  }

  @Nonnull
  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    PlayerEntity player = context.getPlayer();
    BlockPos pos = context.getBlockPos();
    World world = context.getWorld();
    ItemStack upgradeStack = context.getStack();
    BlockState state = world.getBlockState(pos);

    if (player == null || !(state.getBlock() instanceof DockBlock) || !upgradeInfo.canUpgrade(state)) {
      return ActionResult.FAIL;
    }
    if (world.isClient)
      return ActionResult.PASS;

      if (false) {
        player.sendMessage(new TranslatableText("metalbarrels.in_use")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(1))), true);
        return ActionResult.PASS;
  }

    BlockEntity oldDank = world.getBlockEntity(pos);

    //shortcut
    final List<ItemStack> oldDankContents = new ArrayList<>(((DankBlockEntity) oldDank).getHandler().getContents());

    oldDank.markRemoved();

    int newTier = upgradeInfo.end;

    BlockState newState = state.with(DockBlock.TIER,newTier);

    world.setBlockState(pos, newState, 3);
    BlockEntity newBarrel = world.getBlockEntity(pos);

    //newBarrel.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler -> IntStream.range(0, oldDankContents.size()).forEach(i -> itemHandler.insertItem(i, oldDankContents.get(i), false)));

    if (!player.abilities.creativeMode)
      upgradeStack.decrement(1);

    player.sendMessage(new TranslatableText("metalbarrels.upgrade_successful")
            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(1))), true);
    return ActionResult.SUCCESS;
  }
}