package tfar.dankstorage.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.utils.Utils;

public class DockContainer extends AbstractDankContainer {

  //clientside
  public DockContainer(MenuType<?> type, int p_i50105_2_, Inventory playerInventory, int rows) {
    this(type, p_i50105_2_, playerInventory,rows,new DankInventory(Utils.getStatsfromRows(rows)),new SimpleContainerData(9 * rows));
  }

  public DockContainer(MenuType<?> type, int id, Inventory playerInventory, int rows, DankInventory dankInventory,ContainerData propertyDelegate) {
    super(type, id,playerInventory,rows,dankInventory, propertyDelegate);
    addDankSlots();
    addPlayerSlots(playerInventory);
  }

  @Override
  public void removed(Player playerIn) {
    super.removed(playerIn);
  }

    public static DockContainer t1(int p_i50105_2_, Inventory playerInventory) {
      return new DockContainer(DankStorage.dank_1_container, p_i50105_2_, playerInventory, 1);
    }

    public static DockContainer t2(int p_i50105_2_, Inventory playerInventory) {
      return new DockContainer(DankStorage.dank_2_container, p_i50105_2_, playerInventory, 2);
    }

    public static DockContainer t3(int p_i50105_2_, Inventory playerInventory) {
      return new DockContainer(DankStorage.dank_3_container, p_i50105_2_, playerInventory, 3);
    }

    public static DockContainer t4(int p_i50105_2_, Inventory playerInventory) {
      return new DockContainer(DankStorage.dank_4_container, p_i50105_2_, playerInventory, 4);
    }

    public static DockContainer t5(int p_i50105_2_, Inventory playerInventory) {
      return new DockContainer(DankStorage.dank_5_container, p_i50105_2_, playerInventory, 5);
    }

    public static DockContainer t6(int p_i50105_2_, Inventory playerInventory) {
      return new DockContainer(DankStorage.dank_6_container, p_i50105_2_, playerInventory, 6);
    }

    public static DockContainer t7(int p_i50105_2_, Inventory playerInventory) {
      return new DockContainer(DankStorage.dank_7_container, p_i50105_2_, playerInventory, 9);
    }


    //server
  public static DockContainer t1s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate) {
    return new DockContainer(DankStorage.dank_1_container, p_i50105_2_, playerInventory, 1,inventory,propertyDelegate);
  }

  public static DockContainer t2s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate) {
    return new DockContainer(DankStorage.dank_2_container, p_i50105_2_, playerInventory, 2,inventory,propertyDelegate);
  }

  public static DockContainer t3s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate) {
    return new DockContainer(DankStorage.dank_3_container, p_i50105_2_, playerInventory, 3,inventory,propertyDelegate);
  }

  public static DockContainer t4s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate) {
    return new DockContainer(DankStorage.dank_4_container, p_i50105_2_, playerInventory, 4,inventory,propertyDelegate);
  }

  public static DockContainer t5s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate) {
    return new DockContainer(DankStorage.dank_5_container, p_i50105_2_, playerInventory, 5,inventory,propertyDelegate);
  }

  public static DockContainer t6s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate) {
    return new DockContainer(DankStorage.dank_6_container, p_i50105_2_, playerInventory, 6,inventory,propertyDelegate);
  }

  public static DockContainer t7s(int i, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate) {
    return new DockContainer(DankStorage.dank_7_container, i, playerInventory, 9,inventory,propertyDelegate);
  }

}

