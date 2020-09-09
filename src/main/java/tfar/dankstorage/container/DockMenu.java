package tfar.dankstorage.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.utils.Utils;

public class DockMenu extends AbstractDankMenu
{

    //clientside
    public DockMenu(MenuType<?> type, int p_i50105_2_, Inventory playerInventory, int rows)
    {
        this(type, p_i50105_2_, playerInventory, rows, new DankInventory(Utils.getStatsfromRows(rows)), new SimpleContainerData(9 * rows));
    }

    public DockMenu(MenuType<?> type, int id, Inventory playerInventory, int rows, DankInventory dankInventory, ContainerData propertyDelegate)
    {
        super(type, id, playerInventory, rows, dankInventory, propertyDelegate);
        addDankSlots();
        addPlayerSlots(playerInventory);
    }

    public static DockMenu t1(int p_i50105_2_, Inventory playerInventory)
    {
        return new DockMenu(DankStorage.dank_1_container, p_i50105_2_, playerInventory, 1);
    }

    public static DockMenu t2(int p_i50105_2_, Inventory playerInventory)
    {
        return new DockMenu(DankStorage.dank_2_container, p_i50105_2_, playerInventory, 2);
    }

    public static DockMenu t3(int p_i50105_2_, Inventory playerInventory)
    {
        return new DockMenu(DankStorage.dank_3_container, p_i50105_2_, playerInventory, 3);
    }

    public static DockMenu t4(int p_i50105_2_, Inventory playerInventory)
    {
        return new DockMenu(DankStorage.dank_4_container, p_i50105_2_, playerInventory, 4);
    }

    public static DockMenu t5(int p_i50105_2_, Inventory playerInventory)
    {
        return new DockMenu(DankStorage.dank_5_container, p_i50105_2_, playerInventory, 5);
    }

    public static DockMenu t6(int p_i50105_2_, Inventory playerInventory)
    {
        return new DockMenu(DankStorage.dank_6_container, p_i50105_2_, playerInventory, 6);
    }

    public static DockMenu t7(int p_i50105_2_, Inventory playerInventory)
    {
        return new DockMenu(DankStorage.dank_7_container, p_i50105_2_, playerInventory, 9);
    }


    //server
    public static DockMenu t1s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate)
    {
        return new DockMenu(DankStorage.dank_1_container, p_i50105_2_, playerInventory, 1, inventory, propertyDelegate);
    }

    public static DockMenu t2s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate)
    {
        return new DockMenu(DankStorage.dank_2_container, p_i50105_2_, playerInventory, 2, inventory, propertyDelegate);
    }

    public static DockMenu t3s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate)
    {
        return new DockMenu(DankStorage.dank_3_container, p_i50105_2_, playerInventory, 3, inventory, propertyDelegate);
    }

    public static DockMenu t4s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate)
    {
        return new DockMenu(DankStorage.dank_4_container, p_i50105_2_, playerInventory, 4, inventory, propertyDelegate);
    }

    public static DockMenu t5s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate)
    {
        return new DockMenu(DankStorage.dank_5_container, p_i50105_2_, playerInventory, 5, inventory, propertyDelegate);
    }

    public static DockMenu t6s(int p_i50105_2_, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate)
    {
        return new DockMenu(DankStorage.dank_6_container, p_i50105_2_, playerInventory, 6, inventory, propertyDelegate);
    }

    public static DockMenu t7s(int i, Inventory playerInventory, DankInventory inventory, ContainerData propertyDelegate)
    {
        return new DockMenu(DankStorage.dank_7_container, i, playerInventory, 9, inventory, propertyDelegate);
    }

}

