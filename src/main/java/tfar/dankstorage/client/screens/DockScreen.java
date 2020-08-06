package tfar.dankstorage.client.screens;

import tfar.dankstorage.DankStorage;
import tfar.dankstorage.container.DockContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DockScreen extends AbstractDankStorageScreen<DockContainer> {

  public DockScreen(DockContainer container, PlayerInventory playerinventory, Text component, Identifier background) {
    super(container,playerinventory, component,background);
  }

  static final Identifier background1 = new Identifier(DankStorage.MODID,
          "textures/container/gui/dank1.png");

  static final Identifier background2 = new Identifier(DankStorage.MODID,
          "textures/container/gui/dank2.png");

  static final Identifier background3 = new Identifier(DankStorage.MODID,
          "textures/container/gui/dank3.png");

  static final Identifier background4 = new Identifier(DankStorage.MODID,
          "textures/container/gui/dank4.png");

  static final Identifier background5 = new Identifier(DankStorage.MODID,
          "textures/container/gui/dank5.png");

  static final Identifier background6 = new Identifier("textures/gui/container/generic_54.png");

  static final Identifier background7 = new Identifier(DankStorage.MODID,
          "textures/container/gui/dank7.png");

  public static DockScreen t1(DockContainer container, PlayerInventory playerinventory, Text component) {
    return new DockScreen(container,playerinventory,component,background1);
  }

  public static DockScreen t2(DockContainer container, PlayerInventory playerinventory, Text component) {
    return new DockScreen(container,playerinventory,component,background2);
  }

  public static DockScreen t3(DockContainer container, PlayerInventory playerinventory, Text component) {
    return new DockScreen(container,playerinventory,component,background3);
  }

  public static DockScreen t4(DockContainer container, PlayerInventory playerinventory, Text component) {
    return new DockScreen(container,playerinventory,component,background4);
  }

  public static DockScreen t5(DockContainer container, PlayerInventory playerinventory, Text component) {
    return new DockScreen(container,playerinventory,component,background5);
  }

  public static DockScreen t6(DockContainer container, PlayerInventory playerinventory, Text component) {
    return new DockScreen(container,playerinventory,component,background6);
  }

  public static DockScreen t7(DockContainer container, PlayerInventory playerinventory, Text component) {
    return new DockScreen(container,playerinventory,component,background7);
  }

}