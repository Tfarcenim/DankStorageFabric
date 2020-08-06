package tfar.dankstorage.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.inventory.PortableDankInventory;
import tfar.dankstorage.utils.Utils;

public class FabricEvents {

	static MinecraftClient mc = MinecraftClient.getInstance();

	public static void renderStack(MatrixStack matrixStack, float v) {
		PlayerEntity player = mc.player;
		if (player == null)
			return;
		if (!(player.currentScreenHandler instanceof PlayerScreenHandler)) return;
		ItemStack bag = player.getMainHandStack();
		if (!(bag.getItem() instanceof DankItem)) {
			bag = player.getOffHandStack();
			if (!(bag.getItem() instanceof DankItem))
				return;
		}
		int xStart = mc.getWindow().getScaledWidth() / 2;
		int yStart = mc.getWindow().getScaledHeight();
		if (Utils.isConstruction(bag)) {
			PortableDankInventory handler = Utils.getHandler(bag);
			ItemStack toPlace = handler.getStack(Utils.getSelectedSlot(bag));

			if (!toPlace.isEmpty()) {


				Integer color = toPlace.getItem().getRarity(toPlace).formatting.getColorValue();

				int c = color != null ? color : 0xFFFFFF;


				final int itemX = xStart - 150;
				final int itemY = yStart - 25;
				renderHotbarItem(itemX, itemY, 0, player, toPlace);
			}
		}
		final int stringX = xStart - 155;
		final int stringY = yStart - 10;
		String mode = Utils.getUseType(bag).name();
		mc.textRenderer.drawWithShadow(matrixStack,mode,stringX,stringY,0xffffff);
		mc.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
	}

	private static void renderHotbarItem(int x, int y, float partialTicks, PlayerEntity player, ItemStack stack) {
		float f = (float) stack.getCooldown() - partialTicks;
		if (f > 0.0F) {
			RenderSystem.pushMatrix();
			float f1 = 1.0F + f / 5.0F;
			RenderSystem.translatef((float) (x + 8), (float) (y + 12), 0.0F);
			RenderSystem.scalef(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
			RenderSystem.translatef((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
		}

		mc.getItemRenderer().renderInGuiWithOverrides(player, stack, x, y);
		if (f > 0.0F) {
			RenderSystem.popMatrix();
		}
		mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, stack, x, y);
	}

}
