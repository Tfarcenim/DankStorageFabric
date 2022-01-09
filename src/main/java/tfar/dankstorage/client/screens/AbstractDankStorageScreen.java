package tfar.dankstorage.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import tfar.dankstorage.client.Client;
import tfar.dankstorage.client.button.SmallButton;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.server.C2SMessageLockFrequency;
import tfar.dankstorage.network.server.C2SMessageLockSlot;
import tfar.dankstorage.network.server.C2SMessageSort;
import tfar.dankstorage.network.server.C2SSetIDPacket;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventory;

import java.util.List;

public abstract class AbstractDankStorageScreen<T extends AbstractDankMenu> extends AbstractContainerScreen<T> {

    protected final boolean is7;
    final ResourceLocation background;//= new ResourceLocation("textures/gui/container/shulker_box.png");
    private EditBox id;


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
        this.addRenderableWidget(new SmallButton(leftPos + 143, topPos + 4, 26, 12, new TextComponent("Sort"), b -> {
            C2SMessageSort.send();
        }));


        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.id = new EditBox(this.font, i + 92, j + inventoryLabelY, 56, 12, new TranslatableComponent("dank"));
        this.id.setCanLoseFocus(false);
        this.id.setTextColor(-1);
        this.id.setTextColorUneditable(-1);
        this.id.setBordered(false);
        this.id.setMaxLength(10);
        this.id.setResponder(this::onNameChanged);
        this.id.setValue("");
        this.id.setTextColor(0xff00ff00);
        this.addWidget(this.id);
        this.setInitialFocus(this.id);

        Button.OnTooltip onTooltip2 = (button, poseStack, x, y) -> {
//todo make this fancy
            this.renderTooltip(poseStack,
                    this.minecraft.font.split(buildSaveComponent()
                            , Math.max(this.width / 2 - 43, 170)), x, y);

        };

        this.addRenderableWidget(new SmallButton(leftPos + 157, j + inventoryLabelY - 2, 12, 12,
                new TextComponent("s"), b -> {
            try {
                if (menu.dankInventory.idLocked()) return;
                int id1 = Integer.parseInt(id.getValue());
                C2SSetIDPacket.send(id1, true);
            } catch (NumberFormatException e) {

            }
        }, onTooltip2));

        Button.OnTooltip onTooltip = (button, poseStack, x, y) -> {

            this.renderTooltip(poseStack,
                    this.minecraft.font.split(
                            new TranslatableComponent("text.dankstorage.lock_button"), Math.max(this.width / 2 - 43, 170)), x, y);

        };


        this.addRenderableWidget(new SmallButton(leftPos + 129, topPos + 4, 12, 12,
                new TextComponent(""), button -> C2SMessageLockFrequency.send(true), onTooltip) {
            @Override
            public Component getMessage() {
                return menu.dankInventory.idLocked() ? new TextComponent("X").withStyle(ChatFormatting.RED) :
                        new TextComponent("O");
            }
        });
    }

    private static MutableComponent buildSaveComponent() {
        return new TranslatableComponent("text.dankstorage.save_frequency_button",
                new TranslatableComponent("text.dankstorage.save_frequency_button.invalid",
                        new TranslatableComponent("text.dankstorage.save_frequency_button.invalidtxt")
                                .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.INVALID.color)),
                new TranslatableComponent("text.dankstorage.save_frequency_button.too_high",
                        new TranslatableComponent("text.dankstorage.save_frequency_button.too_hightxt")
                                .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.TOO_HIGH.color)),
                new TranslatableComponent("text.dankstorage.save_frequency_button.different_tier",
                        new TranslatableComponent("text.dankstorage.save_frequency_button.different_tiertxt")
                                .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.DIFFERENT_TIER.color)),
                new TranslatableComponent("text.dankstorage.save_frequency_button.good",
                        new TranslatableComponent("text.dankstorage.save_frequency_button.goodtxt")
                                .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.GOOD.color))
                ,                new TranslatableComponent("text.dankstorage.save_frequency_button.locked_frequency",
                new TranslatableComponent("text.dankstorage.save_frequency_button.locked_frequencytxt")
                        .withStyle(ChatFormatting.GRAY))
                .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.LOCKED.color))
        );
    }


    private void onNameChanged(String string) {
        try {
            int i = Integer.parseInt(string);
            C2SSetIDPacket.send(i, false);
        } catch (NumberFormatException e) {
            C2SSetIDPacket.send(-1, false);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }
        //slot locking takes priority over frequency changing
        boolean match = Client.LOCK_SLOT.matches(keyCode,scanCode);
        if (match) {
            id.setFocus(false);
            if (hoveredSlot instanceof DankSlot) {
                C2SMessageLockSlot.send(hoveredSlot.index);
                return true;
            }
        } else {
            id.setFocus(true);
        }

        if (!match && (this.id.keyPressed(keyCode, scanCode, modifiers) || this.id.canConsumeInput())) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

        RenderSystem.setShaderTexture(0, background);
        if (is7)
            blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 512);
        else
            blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);


        for (int i = 0; i < (menu.rows * 9); i++) {
            int j = i % 9;
            int k = i / 9;
            int offsetx = 8;
            int offsety = 18;
            if (this.menu.dankInventory.get(i) == 1) {
                fill(stack, leftPos + j * 18 + offsetx, topPos + k * 18 + offsety,
                        leftPos + j * 18 + offsetx + 16, topPos + k * 18 + offsety + 16, 0xFFFF0000);
            }
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();

        int color = menu.dankInventory.getTextColor();
        this.id.setTextColor(color);
        this.id.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    public List<Component> getTooltipFromItem(ItemStack itemStack) {
        List<Component> tooltipFromItem = super.getTooltipFromItem(itemStack);
        appendDankInfo(tooltipFromItem, itemStack);
        return tooltipFromItem;
    }

    public void appendDankInfo(List<Component> tooltip, ItemStack stack) {
        if (stack.is(Utils.BLACKLISTED_STORAGE)) {
            Component component = new TranslatableComponent("text.dankstorage.blacklisted_storage").withStyle(ChatFormatting.DARK_RED);
            tooltip.add(component);
        }
        if (stack.is(Utils.BLACKLISTED_USAGE)) {
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
    protected void renderLabels(PoseStack poseStack, int i, int j) {
        super.renderLabels(poseStack, i, j);
        int id = menu.dankInventory.getId();//menu.dankInventory.get(menu.rows * 9);
        int color = 0x008000;
        this.font.draw(poseStack, "ID: " + id, 66, inventoryLabelY, color);
    }
}