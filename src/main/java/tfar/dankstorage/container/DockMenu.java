package tfar.dankstorage.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.blockentity.DockBlockEntity;
import tfar.dankstorage.world.DankInventory;
import tfar.dankstorage.utils.Utils;

public class DockMenu extends AbstractDankMenu {

    private final DockBlockEntity dock;

    //clientside
    public DockMenu(MenuType<?> type, int windowId, Inventory playerInventory, int rows) {
        this(type, windowId, playerInventory, rows, new DankInventory(Utils.getStatsfromRows(rows), -1),null);
    }

    public DockMenu(MenuType<?> type, int id, Inventory playerInventory, int rows, DankInventory dankInventory,DockBlockEntity dock) {
        super(type, id, playerInventory, rows, dankInventory);
        addDankSlots();
        addPlayerSlots(playerInventory);
        this.dock = dock;
    }

    @Override
    public void setFrequency(int freq) {
        dock.settings.putInt(Utils.ID, freq);
        dock.setChanged();
    }

    public static DockMenu t1(int windowId, Inventory playerInventory) {
        return new DockMenu(DankStorage.dank_1_container, windowId, playerInventory, 1);
    }

    public static DockMenu t2(int windowId, Inventory playerInventory) {
        return new DockMenu(DankStorage.dank_2_container, windowId, playerInventory, 2);
    }

    public static DockMenu t3(int windowId, Inventory playerInventory) {
        return new DockMenu(DankStorage.dank_3_container, windowId, playerInventory, 3);
    }

    public static DockMenu t4(int windowId, Inventory playerInventory) {
        return new DockMenu(DankStorage.dank_4_container, windowId, playerInventory, 4);
    }

    public static DockMenu t5(int windowId, Inventory playerInventory) {
        return new DockMenu(DankStorage.dank_5_container, windowId, playerInventory, 5);
    }

    public static DockMenu t6(int windowId, Inventory playerInventory) {
        return new DockMenu(DankStorage.dank_6_container, windowId, playerInventory, 6);
    }

    public static DockMenu t7(int windowId, Inventory playerInventory) {
        return new DockMenu(DankStorage.dank_7_container, windowId, playerInventory, 9);
    }


    //server
    public static DockMenu t1s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(DankStorage.dank_1_container, windowId, playerInventory, 1, inventory,dock);
    }

    public static DockMenu t2s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(DankStorage.dank_2_container, windowId, playerInventory, 2, inventory,dock);
    }

    public static DockMenu t3s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(DankStorage.dank_3_container, windowId, playerInventory, 3, inventory,dock);
    }

    public static DockMenu t4s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(DankStorage.dank_4_container, windowId, playerInventory, 4, inventory,dock);
    }

    public static DockMenu t5s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(DankStorage.dank_5_container, windowId, playerInventory, 5, inventory,dock);
    }

    public static DockMenu t6s(int windowId, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(DankStorage.dank_6_container, windowId, playerInventory, 6, inventory,dock);
    }

    public static DockMenu t7s(int i, Inventory playerInventory, DankInventory inventory,DockBlockEntity dock) {
        return new DockMenu(DankStorage.dank_7_container, i, playerInventory, 9, inventory,dock);
    }
}

