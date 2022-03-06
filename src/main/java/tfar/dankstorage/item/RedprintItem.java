package tfar.dankstorage.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.utils.Utils;

import java.util.List;

public class RedprintItem extends Item {
    public RedprintItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(new TranslatableComponent("text.dankstorage.red_print.tooltip0"));
        list.add(new TranslatableComponent("text.dankstorage.red_print.tooltip1"));

        int frequency = getFrequency(stack);

        list.add(new TextComponent("ID: "+frequency));

    }

    private static int getFrequency(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("frequency")) {
            return stack.getTag().getInt("frequency");
        }
        return Utils.INVALID;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        return super.useOn(useOnContext);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickAction, Player player) {
        ItemStack otherStack = slot.getItem();

        if (otherStack.getItem() instanceof DankItem) {

            int dankF = Utils.getFrequency(otherStack);
            int redF = getFrequency(stack);

            if (clickAction == ClickAction.PRIMARY) {
                if (dankF != Utils.INVALID)
                stack.getOrCreateTag().putInt("frequency",dankF);
            } else {
                if (redF != Utils.INVALID) {
                    Utils.setFrequency(otherStack,redF);
                }
            }
            return true;
        }
        return false;
    }
}
