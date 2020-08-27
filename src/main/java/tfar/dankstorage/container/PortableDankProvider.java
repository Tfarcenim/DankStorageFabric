package tfar.dankstorage.container;

import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;

public class PortableDankProvider implements MenuProvider {

  public final DankStats tier;
  public PortableDankProvider(DankStats tier){
    this.tier = tier;
  }

  @Override
  public Component getDisplayName() {
    return new TextComponent("Dank "+(tier.ordinal()+1));
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {

    DankInventory dankInventory = Utils.getHandler(player.getMainHandItem());

    ContainerData propertyDelegate = new ContainerData() {
      public int get(int index) {
        return dankInventory.lockedSlots[index];
      }

      public void set(int index, int value) {
        dankInventory.lockedSlots[index] = value;
      }

      public int getCount() {
        return dankInventory.lockedSlots.length;
      }
    };

    switch (tier) {
      case one:
      default:
        return DankContainer.t1s(i,playerInventory,dankInventory,propertyDelegate);
      case two:
        return DankContainer.t2s(i, playerInventory,dankInventory,propertyDelegate);
      case three:
        return DankContainer.t3s(i, playerInventory,dankInventory,propertyDelegate);
      case four:
        return DankContainer.t4s(i, playerInventory,dankInventory,propertyDelegate);
      case five:
        return DankContainer.t5s(i, playerInventory,dankInventory,propertyDelegate);
      case six:
        return DankContainer.t6s(i, playerInventory,dankInventory,propertyDelegate);
      case seven:
        return DankContainer.t7s(i, playerInventory,dankInventory,propertyDelegate);
    }
  }
}
