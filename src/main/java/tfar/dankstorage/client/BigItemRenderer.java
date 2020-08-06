package tfar.dankstorage.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import tfar.dankstorage.mixin.MinecraftClientAccessor;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class BigItemRenderer extends ItemRenderer {

  public static final BigItemRenderer INSTANCE = new BigItemRenderer(MinecraftClient.getInstance().getTextureManager(),MinecraftClient.getInstance().getBakedModelManager()
          ,((MinecraftClientAccessor)MinecraftClient.getInstance()).getItemColors());

  protected BigItemRenderer(TextureManager textureManagerIn, BakedModelManager modelManagerIn, ItemColors itemColorsIn) {
    super(textureManagerIn, modelManagerIn, itemColorsIn);
  }

  @Override
  public void renderGuiItemOverlay(TextRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
    if (!stack.isEmpty()) {
      MatrixStack matrixstack = new MatrixStack();

      if (stack.getCount() != 1 || text != null) {
        String s = text == null ? getStringFromInt(stack.getCount()) : text;
        matrixstack.translate(0.0D, 0.0D, this.zOffset + 200.0F);
        VertexConsumerProvider.Immediate irendertypebuffer$impl = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

        RenderSystem.pushMatrix();
        float scale = .75f;
        RenderSystem.scalef(scale, scale, 1.0F);
        fr.draw(s, (xPosition + 19 - 2 - (fr.getWidth(s)*scale)) /scale,
                (yPosition + 6 + 3 + (1 / (scale * scale) - 1) ) /scale, 16777215,true, matrixstack.peek().getModel(), irendertypebuffer$impl, false, 0, 15728880);
                //true, matrixstack.getLast().getNormal(), irendertypebuffer$impl, false, 0, 15728880);
        irendertypebuffer$impl.draw();
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

      ClientPlayerEntity entityplayersp = MinecraftClient.getInstance().player;
      float f3 = entityplayersp == null ? 0.0F
              : entityplayersp.getItemCooldownManager().getCooldownProgress(stack.getItem(),
              MinecraftClient.getInstance().getTickDelta());

      if (f3 > 0.0F) {
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        Tessellator tessellator1 = Tessellator.getInstance();
        BufferBuilder vertexbuffer1 = tessellator1.getBuffer();
        this.renderGuiQuad(vertexbuffer1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16,
                MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
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

  private void renderGuiQuad(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue,
                    int alpha) {
    renderer.begin(7, VertexFormats.POSITION_COLOR);
    renderer.vertex(x, y, 0.0D).color(red, green, blue, alpha).next();
    renderer.vertex(x, y + height, 0.0D).color(red, green, blue, alpha).next();
    renderer.vertex(x + width, y + height, 0.0D).color(red, green, blue, alpha).next();
    renderer.vertex(x + width, y, 0.0D).color(red, green, blue, alpha).next();
    Tessellator.getInstance().draw();
  }

}