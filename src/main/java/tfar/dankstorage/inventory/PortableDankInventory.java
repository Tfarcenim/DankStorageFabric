package tfar.dankstorage.inventory;

import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

public class PortableDankInventory extends DankInventory {

    protected final ItemStack bag;

    public PortableDankInventory(ItemStack bag) {
        this(Utils.getStats(bag), bag);
    }

    public PortableDankInventory(DankStats stats, ItemStack bag) {
        super(stats);
        this.bag = bag;
        readItemStack();
    }

    public void readItemStack() {
        deserializeNBT(bag.getOrCreateTagElement(Utils.INV));
    }

    @Override
    public void setChanged() {
        bag.getOrCreateTag().put(Utils.INV, serializeNBT());
    }

}
