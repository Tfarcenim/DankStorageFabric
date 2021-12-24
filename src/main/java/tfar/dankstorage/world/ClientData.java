package tfar.dankstorage.world;

import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.network.server.C2SMessageToggleUseType;

import java.util.HashMap;
import java.util.Map;

public class ClientData {

    public static ItemStack selectedItem = ItemStack.EMPTY;
    public static C2SMessageToggleUseType.UseType useType;

    public static void setData(ItemStack selected, C2SMessageToggleUseType.UseType useType) {
        selectedItem = selected;
        ClientData.useType = useType;
    }
}
