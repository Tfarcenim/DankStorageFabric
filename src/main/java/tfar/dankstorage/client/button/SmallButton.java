package tfar.dankstorage.client.button;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SmallButton extends Button {

    public SmallButton(int x, int y, int widthIn, int heightIn, Component buttonText, OnPress callback) {
        super(x, y, widthIn, heightIn, buttonText, callback,DEFAULT_NARRATION);
    }

    public boolean shouldDrawText() {
        return !getMessage().getString().isEmpty();
    }

    public void tint(GuiGraphics guiGraphics) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        tint(guiGraphics);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        guiGraphics.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        int k = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.renderString(guiGraphics, minecraft.font, k | Mth.ceil(this.alpha * 255.0f) << 24);
    }

    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }
        return 46 + i * 20;
    }

    /*@Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0,WIDGETS_LOCATION);

        tint();

        int c = getYImage(isHovered);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.blendFunc(770, 771);

        int halfwidth1 = this.width / 2;
        int halfwidth2 = this.width - halfwidth1;
        int halfheight1 = this.height / 2;
        int halfheight2 = this.height - halfheight1;
        blit(matrices, getX(), getY(), 0,
                46 + c * 20, halfwidth1, halfheight1);
        blit(matrices, getX() + halfwidth1, getY(), 200 - halfwidth2,
                46 + c * 20, halfwidth2, halfheight1);

        blit(matrices, getX(), getY() + halfheight1,
                0, 46 + c * 20 + 20 - halfheight2, halfwidth1, halfheight2);
        blit(matrices, getX() + halfwidth1, getY() + halfheight1,
                200 - halfwidth2, 46 + c * 20 + 20 - halfheight2, halfwidth2, halfheight2);
        if (shouldDrawText()) drawText(matrices, halfwidth2);
    }*/

    /*public void drawText(PoseStack stack, int halfwidth2) {
        int textColor = 0xe0e0e0;

        if (1 != 0) {
            textColor = -1;
        } else if (!this.visible) {
            textColor = 0xa0a0a0;
        } else if (this.isHovered) {
            textColor = 0xffffa0;
        }
        drawCenteredString(stack, Client.mc.font, getMessage(), getX() + halfwidth2, getY() + (this.height - 8) / 2, textColor);
    }*/
}
