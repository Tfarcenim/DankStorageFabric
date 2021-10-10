package tfar.dankstorage.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tfar.dankstorage.client.button.TripleToggleButton;
import tfar.dankstorage.container.DankMenu;
import tfar.dankstorage.network.server.C2SMessageTogglePickup;
import tfar.dankstorage.utils.Utils;

import static tfar.dankstorage.client.screens.DockScreen.*;

public class PortableDankStorageScreen extends AbstractDankStorageScreen<DankMenu> {

    public static int max_nbt = 1048576;

    public PortableDankStorageScreen(DankMenu container, Inventory playerinventory, Component component, ResourceLocation background) {
        super(container, playerinventory, component, background);
    }

    public static PortableDankStorageScreen t1(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background1);
    }

    public static PortableDankStorageScreen t2(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background2);
    }

    public static PortableDankStorageScreen t3(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background3);
    }

    public static PortableDankStorageScreen t4(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background4);
    }

    public static PortableDankStorageScreen t5(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background5);
    }

    public static PortableDankStorageScreen t6(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background6);
    }

    public static PortableDankStorageScreen t7(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background7);
    }

    @Override
    protected void init() {
        super.init();
        int start = this.titleLabelX;
        int namelength = font.width(title);
        start += namelength;
        start-=10;
    //    this.addRenderableWidget(new RedGreenToggleButton(leftPos + (start += 8), topPos + 6, 8, 8, b -> {
     //       ((RedGreenToggleButton) b).toggle();
    //        C2SMessageTagMode.send();
    //    }, false));
        this.addRenderableWidget(new TripleToggleButton(leftPos + (start += 30), topPos + 6, 8, 8, b -> {
            Utils.cyclePickupMode(menu.bag, Minecraft.getInstance().player);
            C2SMessageTogglePickup.send();
        },this));
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        super.renderLabels(stack, mouseX, mouseY);
        int namelength = font.width(title);
        int start = this.titleLabelX;
        start += namelength;
     //   this.font.draw(stack, "Tag", start += 18, 6, 0x404040);
        this.font.draw(stack, "Pickup", start += 30, 6, 0x404040);

    }
}