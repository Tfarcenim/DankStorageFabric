package tfar.dankstorage.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.inventory.PortableDankInventory;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.Utils;

public class FabricEvents
{

    static Minecraft mc = Minecraft.getInstance();

    public static void renderStack(PoseStack matrixStack, float v)
    {
        Player player = mc.player;
        if (player == null)
            return;
        if (!(player.containerMenu instanceof InventoryMenu)) return;
        ItemStack bag = player.getMainHandItem();
        if (!(bag.getItem() instanceof DankItem)) {
            bag = player.getOffhandItem();
            if (!(bag.getItem() instanceof DankItem))
                return;
        }
        int xStart = mc.getWindow().getGuiScaledWidth() / 2;
        int yStart = mc.getWindow().getGuiScaledHeight();
        if (Utils.isConstruction(bag)) {
            PortableDankInventory handler = Utils.getHandler(bag);
            ItemStack toPlace = handler.getItem(Utils.getSelectedSlot(bag));

            if (!toPlace.isEmpty()) {


                Integer color = toPlace.getItem().getRarity(toPlace).color.getColor();

                int c = color != null ? color : 0xFFFFFF;


                final int itemX = xStart - 150;
                final int itemY = yStart - 25;
                renderHotbarItem(itemX, itemY, 0, player, toPlace);
            }
        }
        final int stringX = xStart - 155;
        final int stringY = yStart - 10;
        String mode = Utils.getUseType(bag).name();
        mc.font.drawShadow(matrixStack, mode, stringX, stringY, 0xffffff);
        mc.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
    }

    private static void renderHotbarItem(int x, int y, float partialTicks, Player player, ItemStack stack)
    {
        float f = (float) stack.getPopTime() - partialTicks;
        if (f > 0.0F) {
            RenderSystem.pushMatrix();
            float f1 = 1.0F + f / 5.0F;
            RenderSystem.translatef((float) (x + 8), (float) (y + 12), 0.0F);
            RenderSystem.scalef(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
            RenderSystem.translatef((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
        }

        mc.getItemRenderer().renderAndDecorateItem(player, stack, x, y);
        if (f > 0.0F) {
            RenderSystem.popMatrix();
        }
        mc.getItemRenderer().renderGuiItemDecorations(mc.font, stack, x, y);
    }

}
