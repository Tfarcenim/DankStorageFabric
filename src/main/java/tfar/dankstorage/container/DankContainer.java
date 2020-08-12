package tfar.dankstorage.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.inventory.LockedSlot;
import tfar.dankstorage.network.S2CSyncNBTSize;
import tfar.dankstorage.utils.Utils;

public class DankContainer extends AbstractDankContainer {

  protected ItemStack bag;
  public int nbtSize;

  public DankContainer(ScreenHandlerType<?> type, int id, PlayerInventory inv, int rows) {
    this(type, id, inv, rows,new DankInventory(Utils.getStatsfromRows(rows)),new ArrayPropertyDelegate(9 * rows));
  }

  public DankContainer(ScreenHandlerType<?> type, int id, PlayerInventory inv, int rows,DankInventory dankInventory,PropertyDelegate propertyDelegate) {
    super(type, id, inv, rows,dankInventory, propertyDelegate);
    PlayerEntity player = inv.player;
    this.bag = player.getMainHandStack().getItem() instanceof DankItem ? player.getMainHandStack() : player.getOffHandStack();
    nbtSize = getNBTSize();
    addDankSlots();
    addPlayerSlots(inv, inv.selectedSlot);
  }

  @Override
  protected void addDankSlots() {
    int slotIndex = 0;
    for (int row = 0; row < rows; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + 18;
        this.addSlot(new DankSlot(dankInventory, slotIndex, x, y){
          @Override
          public void onStackChanged(ItemStack originalItem, ItemStack itemStack) {
            super.onStackChanged(originalItem, itemStack);
            nbtSize = getNBTSize();
            S2CSyncNBTSize.send(playerInventory.player,nbtSize);
          }
        });
        slotIndex++;
      }
    }
  }

  @Override
  protected void addPlayerSlots(PlayerInventory playerinventory) {}

  protected void addPlayerSlots(PlayerInventory playerinventory, int locked) {
    int yStart = 32 + 18 * rows;
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + yStart;
        this.addSlot(new Slot(playerinventory, col + row * 9 + 9, x, y) {
          @Override
          public int getMaxItemCount(ItemStack stack) {
            return Math.min(this.getMaxItemCount(), stack.getMaxCount());
          }
        });
      }
    }

    for (int row = 0; row < 9; ++row) {
      int x = 8 + row * 18;
      int y = yStart + 58;
      if (row != locked)
      this.addSlot(new Slot(playerinventory, row, x, y));
      else
        this.addSlot(new LockedSlot(playerinventory, row, x, y));
    }
  }

  private int getNBTSize() {
    return Utils.getNbtSize(bag);
  }

  public ItemStack getBag() {
    return bag;
  }

  @Override
  public void sendContentUpdates() {
    super.sendContentUpdates();
  }

  public static DankContainer t1(int id, PlayerInventory inv) {
    return new DankContainer(DankStorage.portable_dank_1_container,id,inv,1);
  }

  public static DankContainer t2(int id, PlayerInventory inv) {
    return new DankContainer(DankStorage.portable_dank_2_container,id,inv,2);
  }
  public static DankContainer t3(int id, PlayerInventory inv) {
    return new DankContainer(DankStorage.portable_dank_3_container,id,inv,3);
  }
  public static DankContainer t4(int id, PlayerInventory inv) {
    return new DankContainer(DankStorage.portable_dank_4_container,id,inv,4);
  }
  public static DankContainer t5(int id, PlayerInventory inv) {
    return new DankContainer(DankStorage.portable_dank_5_container,id,inv,5);
  }
  public static DankContainer t6(int id, PlayerInventory inv) {
    return new DankContainer(DankStorage.portable_dank_6_container,id,inv,6);
  }

  public static DankContainer t7(int id, PlayerInventory inv) {
    return new DankContainer(DankStorage.portable_dank_7_container,id,inv,9);
  }

  ////////////////////

  public static DankContainer t1s(int id, PlayerInventory inv, DankInventory dankInventory, PropertyDelegate propertyDelegate) {
    return new DankContainer(DankStorage.portable_dank_1_container,id,inv,1,dankInventory,propertyDelegate);
  }

  public static DankContainer t2s(int id, PlayerInventory inv, DankInventory dankInventory, PropertyDelegate propertyDelegate) {
    return new DankContainer(DankStorage.portable_dank_2_container,id,inv,2,dankInventory,propertyDelegate);
  }
  public static DankContainer t3s(int id, PlayerInventory inv, DankInventory dankInventory, PropertyDelegate propertyDelegate) {
    return new DankContainer(DankStorage.portable_dank_3_container,id,inv,3,dankInventory,propertyDelegate);
  }
  public static DankContainer t4s(int id, PlayerInventory inv, DankInventory dankInventory, PropertyDelegate propertyDelegate) {
    return new DankContainer(DankStorage.portable_dank_4_container,id,inv,4,dankInventory,propertyDelegate);
  }
  public static DankContainer t5s(int id, PlayerInventory inv, DankInventory dankInventory, PropertyDelegate propertyDelegate) {
    return new DankContainer(DankStorage.portable_dank_5_container,id,inv,5,dankInventory,propertyDelegate);
  }
  public static DankContainer t6s(int id, PlayerInventory inv, DankInventory dankInventory, PropertyDelegate propertyDelegate) {
    return new DankContainer(DankStorage.portable_dank_6_container,id,inv,6,dankInventory,propertyDelegate);
  }

  public static DankContainer t7s(int id, PlayerInventory inv, DankInventory dankInventory, PropertyDelegate propertyDelegate) {
    return new DankContainer(DankStorage.portable_dank_7_container,id,inv,9,dankInventory,propertyDelegate);
  }

}

