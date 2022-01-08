package tfar.dankstorage.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DankSavedData extends SavedData {

    private final List<DankInventory> storage = new ArrayList<>();

    public DankSavedData() {
    }

    @Nullable
    public DankInventory getInventory(int id) {
        return (id < storage.size() && id > -1) ? storage.get(id) : null;
    }

    public DankInventory getOrCreateInventory(int id, DankStats stats) {
        DankInventory dankInventory = getInventory(id);
        if (dankInventory == null) {
            int next = getNextID();
            DankInventory inventory = new DankInventory(stats, next);
            storage.add(next, inventory);
            setDirty();
        }
        return getInventory(id);
    }

    public int getNextID() {
        return storage.size();
    }

    private static DankSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent((CompoundTag compoundTag) -> DankSavedData.loadStatic(compoundTag),
                DankSavedData::new, level.dimension().location().toString());
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
        compoundTag.put("contents", listTag);
        return compoundTag;
    }

    public static DankSavedData loadStatic(CompoundTag compoundTag) {
        DankSavedData dankSavedData = new DankSavedData();
        dankSavedData.load(compoundTag);
        return dankSavedData;
    }

    protected void load(CompoundTag compoundTag) {
        ListTag invs = compoundTag.getList("contents", Tag.TAG_COMPOUND);
        for (Tag tag : invs) {
            CompoundTag compoundTag1 = (CompoundTag) tag;
            int id = compoundTag1.getInt(Utils.ID);
            DankInventory dankInventory = readItems(compoundTag1, id);
            storage.add(id, dankInventory);
        }
    }

    DankInventory readItems(CompoundTag tag, int id) {
        DankInventory inventory = new DankInventory(DankStats.zero, id);
        inventory.read(tag);
        return inventory;
    }

    @Override
    public void save(File file) {
        super.save(file);
        DankStorage.LOGGER.info("Saving Dank Contents");
    }

    public void clearAll() {
        storage.clear();
        setDirty();
    }

    public boolean clearId(int id) {
        if (id < getNextID()) {
         DankInventory dankInventory = getInventory(id);
         dankInventory.clearContent();
            return true;
        }
        return false;
    }

    public boolean setTier(int id,int tier) {
        if (id < getNextID()) {
            DankInventory dankInventory = getInventory(id);
            dankInventory.setTo(DankStats.values()[tier]);
            return true;
        }
        return false;
    }
}
