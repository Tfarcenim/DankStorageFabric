package tfar.dankstorage.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.inventory.LockedSlot;
import tfar.dankstorage.network.S2CSyncNBTSize;
import tfar.dankstorage.utils.Utils;

public class DankMenu extends AbstractDankMenu {

  protected ItemStack bag;
  public int nbtSize;

  public DankMenu(MenuType<?> type, int id, Inventory inv, int rows) {
    this(type, id, inv, rows,new DankInventory(Utils.getStatsfromRows(rows)),new SimpleContainerData(9 * rows));
  }

  public DankMenu(MenuType<?> type, int id, Inventory inv, int rows, DankInventory dankInventory, ContainerData propertyDelegate) {
    super(type, id, inv, rows,dankInventory, propertyDelegate);
    Player player = inv.player;
    this.bag = player.getMainHandItem().getItem() instanceof DankItem ? player.getMainHandItem() : player.getOffhandItem();
    nbtSize = getNBTSize();
    addDankSlots();
    addPlayerSlots(inv, inv.selected);
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
          public void onQuickCraft(ItemStack originalItem, ItemStack itemStack) {
            super.onQuickCraft(originalItem, itemStack);
            nbtSize = getNBTSize();
            S2CSyncNBTSize.send(playerInventory.player,nbtSize);
          }
        });
        slotIndex++;
      }
    }
  }

  @Override
  protected void addPlayerSlots(Inventory playerinventory) {}

  protected void addPlayerSlots(Inventory playerinventory, int locked) {
    int yStart = 32 + 18 * rows;
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + yStart;
        this.addSlot(new Slot(playerinventory, col + row * 9 + 9, x, y) {
          @Override
          public int getMaxStackSize(ItemStack stack) {
            return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
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
  public void broadcastChanges() {
    super.broadcastChanges();
  }

  public static DankMenu t1(int id, Inventory inv) {
    return new DankMenu(DankStorage.portable_dank_1_container,id,inv,1);
  }

  public static DankMenu t2(int id, Inventory inv) {
    return new DankMenu(DankStorage.portable_dank_2_container,id,inv,2);
  }
  public static DankMenu t3(int id, Inventory inv) {
    return new DankMenu(DankStorage.portable_dank_3_container,id,inv,3);
  }
  public static DankMenu t4(int id, Inventory inv) {
    return new DankMenu(DankStorage.portable_dank_4_container,id,inv,4);
  }
  public static DankMenu t5(int id, Inventory inv) {
    return new DankMenu(DankStorage.portable_dank_5_container,id,inv,5);
  }
  public static DankMenu t6(int id, Inventory inv) {
    return new DankMenu(DankStorage.portable_dank_6_container,id,inv,6);
  }

  public static DankMenu t7(int id, Inventory inv) {
    return new DankMenu(DankStorage.portable_dank_7_container,id,inv,9);
  }

  ////////////////////

  public static DankMenu t1s(int id, Inventory inv, DankInventory dankInventory, ContainerData propertyDelegate) {
    return new DankMenu(DankStorage.portable_dank_1_container,id,inv,1,dankInventory,propertyDelegate);
  }

  public static DankMenu t2s(int id, Inventory inv, DankInventory dankInventory, ContainerData propertyDelegate) {
    return new DankMenu(DankStorage.portable_dank_2_container,id,inv,2,dankInventory,propertyDelegate);
  }
  public static DankMenu t3s(int id, Inventory inv, DankInventory dankInventory, ContainerData propertyDelegate) {
    return new DankMenu(DankStorage.portable_dank_3_container,id,inv,3,dankInventory,propertyDelegate);
  }
  public static DankMenu t4s(int id, Inventory inv, DankInventory dankInventory, ContainerData propertyDelegate) {
    return new DankMenu(DankStorage.portable_dank_4_container,id,inv,4,dankInventory,propertyDelegate);
  }
  public static DankMenu t5s(int id, Inventory inv, DankInventory dankInventory, ContainerData propertyDelegate) {
    return new DankMenu(DankStorage.portable_dank_5_container,id,inv,5,dankInventory,propertyDelegate);
  }
  public static DankMenu t6s(int id, Inventory inv, DankInventory dankInventory, ContainerData propertyDelegate) {
    return new DankMenu(DankStorage.portable_dank_6_container,id,inv,6,dankInventory,propertyDelegate);
  }

  public static DankMenu t7s(int id, Inventory inv, DankInventory dankInventory, ContainerData propertyDelegate) {
    return new DankMenu(DankStorage.portable_dank_7_container,id,inv,9,dankInventory,propertyDelegate);
  }

}

