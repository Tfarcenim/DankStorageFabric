package tfar.dankstorage.container;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nullable;

public class PortableDankProvider implements MenuProvider {

    public final DankStats tier;

    public PortableDankProvider(DankStats tier) {
        this.tier = tier;
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Dank " + tier.ordinal());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        InteractionHand hand = Utils.getHandWithDank(player);
        if (hand == null) return null;
        DankInventory dankInventory = Utils.getHandler(player.getItemInHand(hand));
        ContainerData propertyDelegate = new DankContainerData(dankInventory);

        switch (tier) {
            case one:
            default:
                return DankMenu.t1s(i, playerInventory, dankInventory, propertyDelegate);
            case two:
                return DankMenu.t2s(i, playerInventory, dankInventory, propertyDelegate);
            case three:
                return DankMenu.t3s(i, playerInventory, dankInventory, propertyDelegate);
            case four:
                return DankMenu.t4s(i, playerInventory, dankInventory, propertyDelegate);
            case five:
                return DankMenu.t5s(i, playerInventory, dankInventory, propertyDelegate);
            case six:
                return DankMenu.t6s(i, playerInventory, dankInventory, propertyDelegate);
            case seven:
                return DankMenu.t7s(i, playerInventory, dankInventory, propertyDelegate);
        }
    }
}
