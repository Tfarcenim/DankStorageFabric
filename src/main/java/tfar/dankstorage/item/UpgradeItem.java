package tfar.dankstorage.item;

import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.blockentity.DockBlockEntity;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.world.DankInventory;

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

        if (player == null || !(state.getBlock() instanceof DockBlock) || !upgradeInfo.canUpgrade(state))
            return InteractionResult.FAIL;
        if (world.isClientSide)
            return InteractionResult.PASS;

        else {
            player.displayClientMessage(new TranslatableComponent("dankstorage.in_use").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(1))), true);
        }

        DockBlockEntity oldDank = (DockBlockEntity) world.getBlockEntity(pos);

        //shortcut
        assert oldDank != null;
        final NonNullList<ItemStack> oldDankContents = oldDank.getInventory().getContents();

        int newTier = upgradeInfo.end;

        BlockState newState = state.setValue(DockBlock.TIER, newTier);

        world.setBlock(pos, newState, 3);

        DockBlockEntity newBarrel = (DockBlockEntity) world.getBlockEntity(pos);
        DankInventory dankInventory = newBarrel.getInventory();

        dankInventory.upgradeTo(DankStats.values()[newTier]);
        for (int i = 0; i < oldDankContents.size() && i < newBarrel.getContainerSize(); ++i)
            dankInventory.setItem(i, oldDankContents.get(i));
        if (!player.getAbilities().instabuild)
            upgradeStack.shrink(1);
        player.displayClientMessage(new TranslatableComponent("dankstorage.upgrade_successful")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(1))), true);
        return InteractionResult.SUCCESS;
    }
}