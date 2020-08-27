package tfar.dankstorage.client.screens;

import tfar.dankstorage.client.button.RedGreenToggleButton;
import tfar.dankstorage.client.button.TripleToggleButton;
import tfar.dankstorage.container.DankMenu;
import tfar.dankstorage.network.server.C2SMessageTagMode;
import tfar.dankstorage.network.server.C2SMessageTogglePickup;
import tfar.dankstorage.utils.Utils;

import static tfar.dankstorage.client.screens.DockScreen.*;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PortableDankStorageScreen extends AbstractDankStorageScreen<DankMenu> {

  public PortableDankStorageScreen(DankMenu container, Inventory playerinventory, Component component, ResourceLocation background) {
    super(container,playerinventory, component,background);
  }

  @Override
  protected void init() {
    super.init();
    int start = this.titleLabelX;
    int namelength = font.width(title.getContents());
    start += namelength;
    this.addButton(new RedGreenToggleButton(leftPos + (start += 20), topPos + 6 ,8,8, b -> {
      ((RedGreenToggleButton)b).toggle();
      C2SMessageTagMode.send();
    }, Utils.oredict(menu.getBag())));
    this.addButton(new TripleToggleButton(leftPos + (start += 30), topPos + 6 ,8,8, b -> {
      ((TripleToggleButton)b).toggle();
      C2SMessageTogglePickup.send();
    }, Utils.getMode(menu.getBag())));
  }

  public static int max_nbt = 1048576;

  @Override
  protected void renderLabels(PoseStack stack,int mouseX, int mouseY) {
    super.renderLabels(stack,mouseX, mouseY);
    int namelength = font.width(title.getContents());
    int start = this.titleLabelX;
    start+= namelength;
    this.font.draw(stack,"Tag", start += 30, 6, 0x404040);
    this.font.draw(stack,"Pickup", start += 30, 6 , 0x404040);
    int nbt_size = menu.propertyDelegate.get(menu.rows * 9);
    int color = nbt_size > max_nbt ? 0x800000 : 0x008000;
    this.font.draw(stack,"NBT: " + nbt_size,70,this.imageHeight - 94,color);
  }

  public static PortableDankStorageScreen t1(DankMenu container, Inventory playerinventory, Component component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background1);
  }

  public static PortableDankStorageScreen t2(DankMenu container, Inventory playerinventory, Component component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background2);
  }

  public static PortableDankStorageScreen t3(DankMenu container, Inventory playerinventory, Component component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background3);
  }

  public static PortableDankStorageScreen t4(DankMenu container, Inventory playerinventory, Component component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background4);
  }

  public static PortableDankStorageScreen t5(DankMenu container, Inventory playerinventory, Component component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background5);
  }

  public static PortableDankStorageScreen t6(DankMenu container, Inventory playerinventory, Component component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background6);
  }

  public static PortableDankStorageScreen t7(DankMenu container, Inventory playerinventory, Component component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background7);
  }
}