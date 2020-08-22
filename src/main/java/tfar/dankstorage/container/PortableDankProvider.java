package tfar.dankstorage.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nullable;

public class PortableDankProvider implements NamedScreenHandlerFactory {

  public final DankStats tier;
  public PortableDankProvider(DankStats tier){
    this.tier = tier;
  }

  @Override
  public Text getDisplayName() {
    return new LiteralText("Dank "+(tier.ordinal()+1));
  }

  @Nullable
  @Override
  public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity player) {

    DankInventory dankInventory = Utils.getHandler(player.getMainHandStack());

    PropertyDelegate propertyDelegate = new PropertyDelegate() {
      public int get(int index) {
        return dankInventory.lockedSlots[index];
      }

      public void set(int index, int value) {
        dankInventory.lockedSlots[index] = value;
      }

      public int size() {
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
