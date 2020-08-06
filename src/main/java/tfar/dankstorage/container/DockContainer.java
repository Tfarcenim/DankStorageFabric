package tfar.dankstorage.container;

import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import tfar.dankstorage.utils.Utils;

public class DockContainer extends AbstractDankContainer {

  //clientside
  public DockContainer(ScreenHandlerType<?> type, int p_i50105_2_, PlayerInventory playerInventory, int rows) {
    this(type, p_i50105_2_, playerInventory,rows,new DankInventory(Utils.getStatsfromRows(rows)),new ArrayPropertyDelegate(9 * rows));
  }

  public DockContainer(ScreenHandlerType<?> type, int id, PlayerInventory playerInventory, int rows, DankInventory dankInventory,PropertyDelegate propertyDelegate) {
    super(type, id,playerInventory,rows,dankInventory, propertyDelegate);
    addDankSlots();
    addPlayerSlots(playerInventory);
  }

  @Override
  public void close(PlayerEntity playerIn) {
    super.close(playerIn);
  }

    public static DockContainer t1(int p_i50105_2_, PlayerInventory playerInventory) {
      return new DockContainer(DankStorage.dank_1_container, p_i50105_2_, playerInventory, 1);
    }

    public static DockContainer t2(int p_i50105_2_, PlayerInventory playerInventory) {
      return new DockContainer(DankStorage.dank_2_container, p_i50105_2_, playerInventory, 2);
    }

    public static DockContainer t3(int p_i50105_2_, PlayerInventory playerInventory) {
      return new DockContainer(DankStorage.dank_3_container, p_i50105_2_, playerInventory, 3);
    }

    public static DockContainer t4(int p_i50105_2_, PlayerInventory playerInventory) {
      return new DockContainer(DankStorage.dank_4_container, p_i50105_2_, playerInventory, 4);
    }

    public static DockContainer t5(int p_i50105_2_, PlayerInventory playerInventory) {
      return new DockContainer(DankStorage.dank_5_container, p_i50105_2_, playerInventory, 5);
    }

    public static DockContainer t6(int p_i50105_2_, PlayerInventory playerInventory) {
      return new DockContainer(DankStorage.dank_6_container, p_i50105_2_, playerInventory, 6);
    }

    public static DockContainer t7(int p_i50105_2_, PlayerInventory playerInventory) {
      return new DockContainer(DankStorage.dank_7_container, p_i50105_2_, playerInventory, 9);
    }


    //server
  public static DockContainer t1s(int p_i50105_2_, PlayerInventory playerInventory, DankInventory inventory, PropertyDelegate propertyDelegate) {
    return new DockContainer(DankStorage.dank_1_container, p_i50105_2_, playerInventory, 1,inventory,propertyDelegate);
  }

  public static DockContainer t2s(int p_i50105_2_, PlayerInventory playerInventory, DankInventory inventory, PropertyDelegate propertyDelegate) {
    return new DockContainer(DankStorage.dank_2_container, p_i50105_2_, playerInventory, 2,inventory,propertyDelegate);
  }

  public static DockContainer t3s(int p_i50105_2_, PlayerInventory playerInventory, DankInventory inventory, PropertyDelegate propertyDelegate) {
    return new DockContainer(DankStorage.dank_3_container, p_i50105_2_, playerInventory, 3,inventory,propertyDelegate);
  }

  public static DockContainer t4s(int p_i50105_2_, PlayerInventory playerInventory, DankInventory inventory, PropertyDelegate propertyDelegate) {
    return new DockContainer(DankStorage.dank_4_container, p_i50105_2_, playerInventory, 4,inventory,propertyDelegate);
  }

  public static DockContainer t5s(int p_i50105_2_, PlayerInventory playerInventory, DankInventory inventory, PropertyDelegate propertyDelegate) {
    return new DockContainer(DankStorage.dank_5_container, p_i50105_2_, playerInventory, 5,inventory,propertyDelegate);
  }

  public static DockContainer t6s(int p_i50105_2_, PlayerInventory playerInventory, DankInventory inventory, PropertyDelegate propertyDelegate) {
    return new DockContainer(DankStorage.dank_6_container, p_i50105_2_, playerInventory, 6,inventory,propertyDelegate);
  }

  public static DockContainer t7s(int i, PlayerInventory playerInventory, DankInventory inventory, PropertyDelegate propertyDelegate) {
    return new DockContainer(DankStorage.dank_7_container, i, playerInventory, 9,inventory,propertyDelegate);
  }

}

