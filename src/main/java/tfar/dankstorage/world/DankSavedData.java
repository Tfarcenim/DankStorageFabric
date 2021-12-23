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
import java.io.File;
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

    public DankInventory getOrCreateInventory(int id, DankStats stats) {
        DankInventory dankInventory = getInventory(id);
        if (dankInventory == null) {

            int next = getNextID();
            DankInventory inventory = new DankInventory(stats, level);
            inventory.id = id;
            if (id >= next) {
                //uh oh, we have an id that's too high, so we make a bunch of null inventories to fill in
                //possibly caused by opening a dank with an id that's too high
                if (id > next) {
                    for (int i = 0; i < id - next; i++) {
                        storage.add(null);
                    }
                }
                storage.add(next, inventory);
            } else {
                //we have an id that's lower than the total, yet it's null due to filler
                //SET the inventory in the index instead of adding to the end of the list
                storage.set(id, inventory);
            }
            setDirty();
        }
        return getInventory(id);
    }

    //this is only for patching old worlds
    public DankInventory setFreshInventory(int id, DankStats stats) {
        int currentlyStored = storage.size();

        if (currentlyStored < id) {
            //make a bunch of dummies before setting the actual inventory
            //for example
            //if the id is 3, but there's only 1 inventory on record, dummies need to be added at 1 and 2

            for (int i = 0; i < id - currentlyStored; i++) {
                storage.add(null);
            }
        }

        DankInventory fresh = new DankInventory(stats, level);
        fresh.id = id;

        //add fixed inventory at the very end
        storage.add(id, fresh);

        setDirty();

        return getInventory(id);
    }

    public int getNextID() {
        return storage.size();
    }

    public void saveToId(int id, DankInventory inv) {
        if (id < storage.size()) {
            storage.set(id, inv);
            setDirty();
        } else {
            System.out.println("Invalid id: " + id);
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
        for (int i = 0; i < storage.size(); i++) {
            DankInventory inventory = storage.get(i);
            if (inventory != null) {
                listTag.add(inventory.save());
            } else {
                CompoundTag tag = new CompoundTag();
                tag.putBoolean("dummy", true);
                tag.putInt(Utils.ID, i);
                listTag.add(tag);
            }
        }
        compoundTag.put("contents", listTag);
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
            CompoundTag compoundTag1 = (CompoundTag) tag;
            int id = compoundTag1.getInt(Utils.ID);

            if (compoundTag1.contains("dummy")) {
                storage.add(id, null);
            } else {
                DankInventory dankInventory = readItems(compoundTag1);
                storage.add(id, dankInventory);
            }
        }
    }

    DankInventory readItems(CompoundTag tag) {
        DankInventory inventory = new DankInventory(DankStats.zero, level);
        inventory.read(tag);
        return inventory;
    }

    @Override
    public void save(File file) {
        super.save(file);
        System.out.println("Saving Dank Contents");
    }
}
