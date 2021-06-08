package tfar.dankstorage.world;

import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.network.server.C2SMessageToggleUseType;

import java.util.HashMap;
import java.util.Map;

public class ClientData {

    public static Map<Integer,ClientData> map = new HashMap<>();

    public ItemStack selectedItem = ItemStack.EMPTY;
    public C2SMessageToggleUseType.UseType useType;

    public ClientData(ItemStack selectedItem, C2SMessageToggleUseType.UseType useType) {
        this.selectedItem = selectedItem;
        this.useType = useType;
    }

    public static void addData(int id, ItemStack selected, C2SMessageToggleUseType.UseType useType) {
        map.put(id,new ClientData(selected,useType));
    }
}
