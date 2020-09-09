package tfar.dankstorage.item;

import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.blockentity.DockBlockEntity;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.List;

public class UpgradeItem extends Item {

  protected final UpgradeInfo upgradeInfo;

  public UpgradeItem(Properties properties, UpgradeInfo info) {
    super(properties);
    this.upgradeInfo = info;
  }

  @Nonnull
  @Override
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    BlockPos pos = context.getClickedPos();
    Level world = context.getLevel();
    ItemStack upgradeStack = context.getItemInHand();
    BlockState state = world.getBlockState(pos);

    if (player == null || !(state.getBlock() instanceof DockBlock) || !upgradeInfo.canUpgrade(state)) {
      return InteractionResult.FAIL;
    }
    if (world.isClientSide)
      return InteractionResult.PASS;

      if (false) {
        player.displayClientMessage(new TranslatableComponent("metalbarrels.in_use")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(1))), true);
        return InteractionResult.PASS;
  }

    BlockEntity oldDank = world.getBlockEntity(pos);

    //shortcut
    final List<ItemStack> oldDankContents = new ArrayList<>(((DockBlockEntity) oldDank).getHandler().getContents());

    oldDank.setRemoved();

    int newTier = upgradeInfo.end;

    BlockState newState = state.setValue(DockBlock.TIER,newTier);

    world.setBlock(pos, newState, 3);
    BlockEntity newBarrel = world.getBlockEntity(pos);

    //newBarrel.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler -> IntStream.range(0, oldDankContents.size()).forEach(i -> itemHandler.insertItem(i, oldDankContents.get(i), false)));

    if (!player.abilities.instabuild)
      upgradeStack.shrink(1);

    player.displayClientMessage(new TranslatableComponent("metalbarrels.upgrade_successful")
            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(1))), true);
    return InteractionResult.SUCCESS;
  }
}