package tfar.dankstorage.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DankSavedData extends SavedData {

    private final List<DankInventory> storage = new ArrayList<>();
    private final ServerLevel level;

    public DankSavedData(ServerLevel level) {
        this.level = level;
    }

    @Nullable
    public DankInventory getInventory(int id) {
        return (id < storage.size() && id > -1) ? storage.get(id) : null;
    }

    public DankInventory getOrCreateInventory(int id,DankStats stats) {
        DankInventory dankInventory = getInventory(id);
        if (dankInventory == null) {
            int next = getNextID();
            DankInventory inventory = new DankInventory(stats,level);
            inventory.id = next;
            storage.add(next,inventory);
        }
        return getInventory(id);
    }

    public int getNextID() {
        return storage.size();
    }

    public void saveToId(int id, DankInventory inv) {
        if (id < storage.size()) {
            storage.set(id, inv);
        setDirty(); }
        else {
             System.out.println("Invalid id: "+id);
        }
    }


    private static DankSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent((CompoundTag compoundTag) -> DankSavedData.loadStatic(level, compoundTag),
                () -> new DankSavedData(level), level.dimension().location().toString());
    }

    public static DankSavedData getDefault(ServerLevel level) {
        return get(level.getServer().getLevel(Level.OVERWORLD));
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();
        for (DankInventory inventory : storage) {
            listTag.add(inventory.save());
        }
        compoundTag.put("contents",listTag);
        return compoundTag;
    }

    public static DankSavedData loadStatic(ServerLevel serverLevel, CompoundTag compoundTag) {
        DankSavedData dankSavedData = new DankSavedData(serverLevel);
        dankSavedData.load(compoundTag);
        return dankSavedData;
    }

    protected void load(CompoundTag compoundTag) {
        ListTag invs = compoundTag.getList("contents", Tag.TAG_COMPOUND);
        for (Tag tag : invs) {
            CompoundTag compoundTag1 = (CompoundTag)tag;
            int id = compoundTag1.getInt(Utils.ID);
            DankInventory stacks = readItems(compoundTag1);
            storage.add(id,stacks);
        }
    }

    DankInventory readItems(CompoundTag tag) {
        DankInventory inventory = new DankInventory(DankStats.zero,level);
        inventory.read(tag);
        return inventory;
    }
}
