package tfar.dankstorage.client.button;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import tfar.dankstorage.client.screens.PortableDankStorageScreen;
import tfar.dankstorage.utils.PickupMode;
import tfar.dankstorage.utils.Utils;

public class TripleToggleButton extends SmallButton {

    protected PortableDankStorageScreen screen;

    public TripleToggleButton(int x, int y, int widthIn, int heightIn, Component component, OnPress callback, PortableDankStorageScreen screen) {
        super(x, y, widthIn, heightIn,component, callback);
        this.screen = screen;
    }

    @Override
    public void tint(GuiGraphics guiGraphics) {
        PickupMode mode = Utils.getPickupMode(screen.getMenu().bag);
        guiGraphics.setColor(mode.r(), mode.g(), mode.b(), 1);
    }
}
