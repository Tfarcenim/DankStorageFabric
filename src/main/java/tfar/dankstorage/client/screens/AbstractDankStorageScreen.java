package tfar.dankstorage.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.anti_ad.mc.ipn.api.IPNIgnore;
import tfar.dankstorage.client.button.SmallButton;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.server.C2SMessageLockSlot;
import tfar.dankstorage.network.server.C2SMessageSort;
import tfar.dankstorage.utils.Utils;

import java.util.List;

@IPNIgnore
public abstract class AbstractDankStorageScreen<T extends AbstractDankMenu> extends AbstractContainerScreen<T> {

    protected final boolean is7;
    final ResourceLocation background;//= new ResourceLocation("textures/gui/container/shulker_box.png");

    public AbstractDankStorageScreen(T container, Inventory playerinventory, Component component, ResourceLocation background) {
        super(container, playerinventory, component);
        this.background = background;
        this.imageHeight = 114 + this.menu.rows * 18;
        this.is7 = this.menu.rows > 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new SmallButton(leftPos + 143, topPos + 4, 26, 12, new TextComponent("Sort"), b -> {
            C2SMessageSort.send();
        }));
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

        minecraft.getTextureManager().bind(background);
        if (is7)
            blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 512);
        else
            blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);


        for (int i = 0; i < (menu.rows * 9); i++) {
            int j = i % 9;
            int k = i / 9;
            int offsetx = 8;
            int offsety = 18;
            if (this.menu.propertyDelegate.get(i) == 1)
                fill(stack, leftPos + j * 18 + offsetx, topPos + k * 18 + offsety,
                        leftPos + j * 18 + offsetx + 16, topPos + k * 18 + offsety + 16, 0xFFFF0000);
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    public List<Component> getTooltipFromItem(ItemStack itemStack) {
        List<Component> tooltipFromItem = super.getTooltipFromItem(itemStack);
        appendDankInfo(tooltipFromItem, itemStack);
        return tooltipFromItem;
    }

    public void appendDankInfo(List<Component> tooltip, ItemStack stack) {
        if (stack.getItem().is(Utils.BLACKLISTED_STORAGE)) {
            Component component = new TranslatableComponent("text.dankstorage.blacklisted_storage").withStyle(ChatFormatting.DARK_RED);
            tooltip.add(component);
        }
        if (stack.getItem().is(Utils.BLACKLISTED_USAGE)) {
            Component component = new TranslatableComponent("text.dankstorage.blacklisted_usage").
                    withStyle(ChatFormatting.DARK_RED);
            tooltip.add(component);
        }
        if (hoveredSlot instanceof DankSlot) {
            Component component1 = new TranslatableComponent("text.dankstorage.lock",
                    new TextComponent("ctrl").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);
            tooltip.add(component1);
            if (stack.getCount() >= 1000) {
                Component component2 = new TranslatableComponent(
                        "text.dankstorage.exact", new TextComponent(Integer.toString(stack.getCount())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY);
                tooltip.add(component2);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (Screen.hasControlDown()) {
            Slot slot = findSlot(mouseX, mouseY);
            if (slot instanceof DankSlot) {
                C2SMessageLockSlot.send(slot.index);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}