package tfar.dankstorage.client.screens;

import net.minecraft.client.MinecraftClient;
import tfar.dankstorage.client.button.RedGreenToggleButton;
import tfar.dankstorage.client.button.TripleToggleButton;
import tfar.dankstorage.container.DankContainer;
import tfar.dankstorage.network.server.C2SMessageTagMode;
import tfar.dankstorage.network.server.C2SMessageTogglePickup;
import tfar.dankstorage.utils.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static tfar.dankstorage.client.screens.DockScreen.*;

public class PortableDankStorageScreen extends AbstractDankStorageScreen<DankContainer> {

  public PortableDankStorageScreen(DankContainer container, PlayerInventory playerinventory, Text component, Identifier background) {
    super(container,playerinventory, component,background);
  }

  @Override
  protected void init() {
    super.init();
    int start = this.titleX;
    int namelength = textRenderer.getWidth(title.asString());
    start += namelength;
    this.addButton(new RedGreenToggleButton(x + (start += 20), y + 6 ,8,8, b -> {
      ((RedGreenToggleButton)b).toggle();
      C2SMessageTagMode.send();
    }, Utils.oredict(handler.getBag())));
    this.addButton(new TripleToggleButton(x + (start += 30), y + 6 ,8,8, b -> {
      ((TripleToggleButton)b).toggle();
      C2SMessageTogglePickup.send();
    }, Utils.getMode(handler.getBag())));
  }

  @Override
  protected void drawForeground(MatrixStack stack,int mouseX, int mouseY) {
    super.drawForeground(stack,mouseX, mouseY);
    int namelength = textRenderer.getWidth(title.asString());
    int start = this.titleX;
    start+= namelength;
    this.textRenderer.draw(stack,"Tag", start += 30, 6, 0x404040);
    this.textRenderer.draw(stack,"Pickup", start += 30, 6 , 0x404040);
    int color = handler.nbtSize > 2000000 ? 0x800000 : 0x008000;
    this.textRenderer.draw(stack,"NBT: " + handler.nbtSize,70,this.backgroundHeight - 94,color);
  }

  public static PortableDankStorageScreen t1(DankContainer container, PlayerInventory playerinventory, Text component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background1);
  }

  public static PortableDankStorageScreen t2(DankContainer container, PlayerInventory playerinventory, Text component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background2);
  }

  public static PortableDankStorageScreen t3(DankContainer container, PlayerInventory playerinventory, Text component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background3);
  }

  public static PortableDankStorageScreen t4(DankContainer container, PlayerInventory playerinventory, Text component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background4);
  }

  public static PortableDankStorageScreen t5(DankContainer container, PlayerInventory playerinventory, Text component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background5);
  }

  public static PortableDankStorageScreen t6(DankContainer container, PlayerInventory playerinventory, Text component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background6);
  }

  public static PortableDankStorageScreen t7(DankContainer container, PlayerInventory playerinventory, Text component) {
    return new PortableDankStorageScreen(container,playerinventory,component,background7);
  }

}