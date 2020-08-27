package tfar.dankstorage.container;

import net.minecraft.world.inventory.ContainerData;
import tfar.dankstorage.inventory.DankInventory;

public class DankContainerData implements ContainerData {

	public int slots;
	public DankInventory dankInventory;
	public int nbtSize;

	public DankContainerData(DankInventory dankInventory) {
		this.slots = dankInventory.dankStats.slots;
		this.dankInventory = dankInventory;
	}

	@Override
	public int get(int i) {
		return i == slots ? nbtSize : dankInventory.lockedSlots[i];
	}

	@Override
	public void set(int i, int value) {
		if (i == slots) {
			this.nbtSize = value;
		} else {
			dankInventory.lockedSlots[i] = value;
		}
	}

	@Override
	public int getCount() {
		return slots + 1;
	}
}
