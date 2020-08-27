package tfar.dankstorage.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.mixin.MinecraftClientAccessor;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class BigItemRenderer extends ItemRenderer {

  public static final BigItemRenderer INSTANCE = new BigItemRenderer(Minecraft.getInstance().getTextureManager(),Minecraft.getInstance().getModelManager()
          ,((MinecraftClientAccessor)Minecraft.getInstance()).getItemColors());

  protected BigItemRenderer(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn) {
    super(textureManagerIn, modelManagerIn, itemColorsIn);
  }

  @Override
  public void renderGuiItemDecorations(Font fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
    if (!stack.isEmpty()) {
      PoseStack matrixstack = new PoseStack();

      if (stack.getCount() != 1 || text != null) {
        String s = text == null ? getStringFromInt(stack.getCount()) : text;
        matrixstack.translate(0.0D, 0.0D, this.blitOffset + 200.0F);
        MultiBufferSource.BufferSource irendertypebuffer$impl = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        RenderSystem.pushMatrix();
        float scale = .75f;
        RenderSystem.scalef(scale, scale, 1.0F);
        fr.drawInBatch(s, (xPosition + 19 - 2 - (fr.width(s)*scale)) /scale,
                (yPosition + 6 + 3 + (1 / (scale * scale) - 1) ) /scale, 16777215,true, matrixstack.last().pose(), irendertypebuffer$impl, false, 0, 15728880);
                //true, matrixstack.getLast().getNormal(), irendertypebuffer$impl, false, 0, 15728880);
        irendertypebuffer$impl.endBatch();
        RenderSystem.popMatrix();
      }


       /*if (stack.getItem().showDurabilityBar(stack)) {
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        double health = stack.getItem().getDurabilityForDisplay(stack);
        int rgbfordisplay = stack.getItem().getRGBDurabilityForDisplay(stack);
        int i = Math.round(13.0F - (float) health * 13.0F);
        this.renderGuiQuad(vertexbuffer, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
        this.renderGuiQuad(vertexbuffer, xPosition + 2, yPosition + 13, i, 1, rgbfordisplay >> 16 & 255, rgbfordisplay >> 8 & 255, rgbfordisplay & 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
      }*/

      LocalPlayer entityplayersp = Minecraft.getInstance().player;
      float f3 = entityplayersp == null ? 0.0F
              : entityplayersp.getCooldowns().getCooldownPercent(stack.getItem(),
              Minecraft.getInstance().getFrameTime());

      if (f3 > 0.0F) {
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        Tesselator tessellator1 = Tesselator.getInstance();
        BufferBuilder vertexbuffer1 = tessellator1.getBuilder();
        this.fillRect(vertexbuffer1, xPosition, yPosition + Mth.floor(16.0F * (1.0F - f3)), 16,
                Mth.ceil(16.0F * f3), 255, 255, 255, 127);
        RenderSystem.enableTexture();
        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
      }
    }
  }

  private static final DecimalFormat decimalFormat = new DecimalFormat("0.#");

  public String getStringFromInt(int number){

    if (number >= 1000000000) return decimalFormat.format(number / 1000000000f) + "b";
    if (number >= 1000000) return decimalFormat.format(number / 1000000f) + "m";
    if (number >= 1000) return decimalFormat.format(number / 1000f) + "k";

    return Float.toString(number).replaceAll("\\.?0*$", "");
  }

  private void fillRect(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue,
                    int alpha) {
    renderer.begin(7, DefaultVertexFormat.POSITION_COLOR);
    renderer.vertex(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
    renderer.vertex(x, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
    renderer.vertex(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
    renderer.vertex(x + width, y, 0.0D).color(red, green, blue, alpha).endVertex();
    Tesselator.getInstance().end();
  }

}