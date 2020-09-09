package tfar.dankstorage.container;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.S2CSyncExtendedSlotContents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractDankMenu extends AbstractContainerMenu
{

    public final int rows;
    public final Inventory playerInventory;
    public final ContainerData propertyDelegate;
    public DankInventory dankInventory;

    public AbstractDankMenu(MenuType<?> type, int p_i50105_2_, Inventory playerInventory, int rows, DankInventory dankInventory, ContainerData propertyDelegate)
    {
        super(type, p_i50105_2_);
        this.rows = rows;
        this.playerInventory = playerInventory;
        this.dankInventory = dankInventory;
        this.propertyDelegate = propertyDelegate;
        addDataSlots(propertyDelegate);
    }

    public static boolean canItemQuickReplace(@Nullable Slot slot, @Nonnull ItemStack stack, boolean stackSizeMatters)
    {
        boolean flag = slot == null || !slot.hasItem();
        if (slot != null) {
            ItemStack slotStack = slot.getItem();

            if (!flag && stack.sameItem(slotStack) && ItemStack.tagMatches(slotStack, stack)) {
                return slotStack.getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= slot.getMaxStackSize(slotStack);
            }
        }
        return flag;
    }

    protected void addDankSlots()
    {
        int slotIndex = 0;
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + 18;
                this.addSlot(new DankSlot(dankInventory, slotIndex, x, y));
                slotIndex++;
            }
        }
    }

    protected void addPlayerSlots(Inventory playerinventory)
    {
        int yStart = 32 + 18 * rows;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + yStart;
                this.addSlot(new Slot(playerinventory, col + row * 9 + 9, x, y)
                {
                    @Override
                    public int getMaxStackSize(ItemStack stack)
                    {
                        return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
                    }
                });
            }
        }

        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = yStart + 58;
            this.addSlot(new Slot(playerinventory, row, x, y)
            {
                @Override
                public int getMaxStackSize(ItemStack stack)
                {
                    return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
                }
            });
        }
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < rows * 9) {
                if (!this.moveItemStackTo(itemstack1, rows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, rows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Nonnull
    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, Player player)
    {
        boolean locked = slotId >= 0 && slotId < (rows * 9) && propertyDelegate.get(slotId) == 1;
        ItemStack itemstack = ItemStack.EMPTY;
        Inventory PlayerInventory = player.inventory;

        if (clickTypeIn == ClickType.QUICK_CRAFT) {
            int j1 = this.quickcraftStatus;
            this.quickcraftStatus = getQuickcraftHeader(dragType);

            if ((j1 != 1 || this.quickcraftStatus != 2) && j1 != this.quickcraftStatus) {
                this.resetQuickCraft();
            } else if (PlayerInventory.getCarried().isEmpty()) {
                this.resetQuickCraft();
            } else if (this.quickcraftStatus == 0) {
                this.quickcraftType = getQuickcraftType(dragType);

                if (isValidQuickcraftType(this.quickcraftType, player)) {
                    this.quickcraftStatus = 1;
                    this.quickcraftSlots.clear();
                } else {
                    this.resetQuickCraft();
                }
            } else if (this.quickcraftStatus == 1) {
                Slot slot7 = this.slots.get(slotId);
                ItemStack mouseStack = PlayerInventory.getCarried();

                if (slot7 != null && DockMenu.canItemQuickReplace(slot7, mouseStack, true) && slot7.mayPlace(mouseStack) && (this.quickcraftType == 2 || mouseStack.getCount() > this.quickcraftSlots.size()) && this.canDragTo(slot7)) {
                    this.quickcraftSlots.add(slot7);
                }
            } else if (this.quickcraftStatus == 2) {
                if (!this.quickcraftSlots.isEmpty()) {
                    ItemStack mouseStackCopy = PlayerInventory.getCarried().copy();
                    int k1 = PlayerInventory.getCarried().getCount();

                    for (Slot dragSlot : this.quickcraftSlots) {
                        ItemStack mouseStack = PlayerInventory.getCarried();

                        if (dragSlot != null && DockMenu.canItemQuickReplace(dragSlot, mouseStack, true) && dragSlot.mayPlace(mouseStack) && (this.quickcraftType == 2 || mouseStack.getCount() >= this.quickcraftSlots.size()) && this.canDragTo(dragSlot)) {
                            ItemStack itemstack14 = mouseStackCopy.copy();
                            int j3 = dragSlot.hasItem() ? dragSlot.getItem().getCount() : 0;
                            getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, itemstack14, j3);
                            int k3 = dragSlot.getMaxStackSize(itemstack14);

                            if (itemstack14.getCount() > k3) {
                                itemstack14.setCount(k3);
                            }

                            k1 -= itemstack14.getCount() - j3;
                            dragSlot.set(itemstack14);
                        }
                    }

                    mouseStackCopy.setCount(k1);
                    PlayerInventory.setCarried(mouseStackCopy);
                }

                this.resetQuickCraft();
            } else {
                this.resetQuickCraft();
            }
        } else if (this.quickcraftStatus != 0) {
            this.resetQuickCraft();
        } else if ((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1)) {
            if (slotId == -999) {
                if (!PlayerInventory.getCarried().isEmpty()) {
                    if (dragType == 0) {
                        player.drop(PlayerInventory.getCarried(), true);
                        PlayerInventory.setCarried(ItemStack.EMPTY);
                    }

                    if (dragType == 1) {
                        player.drop(PlayerInventory.getCarried().split(1), true);
                    }
                }
            } else if (clickTypeIn == ClickType.QUICK_MOVE) {
                if (slotId < 0) {
                    return ItemStack.EMPTY;
                }

                Slot slot5 = this.slots.get(slotId);

                if (slot5 == null || !slot5.mayPickup(player)) {
                    return ItemStack.EMPTY;
                }

                for (ItemStack itemstack7 = this.quickMoveStack(player, slotId); !itemstack7.isEmpty() && ItemStack.isSame(slot5.getItem(), itemstack7); itemstack7 = this.quickMoveStack(player, slotId)) {
                    itemstack = itemstack7.copy();
                }
            } else {
                if (slotId < 0) {
                    return ItemStack.EMPTY;
                }

                Slot slot6 = this.slots.get(slotId);

                if (slot6 != null) {
                    ItemStack slotStack = slot6.getItem();
                    ItemStack mouseStack = PlayerInventory.getCarried();

                    //account for locked slots
                    if (!slotStack.isEmpty()) {
                        if (slot6 instanceof DankSlot && locked) {
                            ItemStack stack = slotStack.copy();
                            stack.shrink(1);
                            itemstack = stack;
                        } else {
                            itemstack = slotStack.copy();
                        }
                    }

                    if (slotStack.isEmpty()) {
                        if (!mouseStack.isEmpty() && slot6.mayPlace(mouseStack)) {
                            int i3 = dragType == 0 ? mouseStack.getCount() : 1;

                            if (i3 > slot6.getMaxStackSize(mouseStack)) {
                                i3 = slot6.getMaxStackSize(mouseStack);
                            }

                            slot6.set(mouseStack.split(i3));
                        }
                    } else if (slot6.mayPickup(player)) {
                        if (mouseStack.isEmpty()) {
                            if (slotStack.isEmpty()) {
                                slot6.set(ItemStack.EMPTY);
                                PlayerInventory.setCarried(ItemStack.EMPTY);
                            } else {
                                int toMove;
                                if (slot6 instanceof DankSlot) {
                                    if (slotStack.getCount() >= slotStack.getMaxStackSize()) {
                                        if (dragType == 0) {
                                            if (locked && slotStack.getCount() == slotStack.getMaxStackSize()) {
                                                toMove = slotStack.getMaxStackSize() - 1;
                                            } else {
                                                toMove = slotStack.getMaxStackSize();
                                            }
                                        } else {
                                            toMove = (slotStack.getMaxStackSize() + 1) / 2;
                                        }
                                    } else {
                                        if (dragType == 0) {
                                            if (locked) {
                                                toMove = slotStack.getCount() - 1;
                                            } else {
                                                toMove = slotStack.getCount();
                                            }
                                        } else {
                                            if (locked && slotStack.getCount() == 1) {
                                                toMove = 0;
                                            } else {
                                                toMove = (slotStack.getCount() + 1) / 2;
                                            }
                                        }
                                    }
                                } else {
                                    toMove = dragType == 0 ? slotStack.getCount() : (slotStack.getCount() + 1) / 2;
                                }
                                //int toMove = dragType == 0 ? slotStack.getCount() : (slotStack.getCount() + 1) / 2;
                                PlayerInventory.setCarried(slot6.remove(toMove));

                                if (slotStack.isEmpty()) {
                                    slot6.set(ItemStack.EMPTY);
                                }

                                slot6.onTake(player, PlayerInventory.getCarried());
                            }
                        } else if (slot6.mayPlace(mouseStack)) {
                            if (slotStack.getItem() == mouseStack.getItem() && ItemStack.tagMatches(slotStack, mouseStack)) {
                                int k2 = dragType == 0 ? mouseStack.getCount() : 1;

                                if (k2 > slot6.getMaxStackSize(mouseStack) - slotStack.getCount()) {
                                    k2 = slot6.getMaxStackSize(mouseStack) - slotStack.getCount();
                                }

                                mouseStack.shrink(k2);
                                slotStack.grow(k2);
                            } else if (mouseStack.getCount() <= slot6.getMaxStackSize(mouseStack) && slotStack.getCount() <= slotStack.getMaxStackSize()) {
                                slot6.set(mouseStack);
                                PlayerInventory.setCarried(slotStack);
                            }
                        } else if (slotStack.getItem() == mouseStack.getItem() && mouseStack.getMaxStackSize() > 1 && ItemStack.tagMatches(slotStack, mouseStack) && !slotStack.isEmpty()) {
                            int j2 = slotStack.getCount();

                            if (j2 + mouseStack.getCount() <= mouseStack.getMaxStackSize()) {
                                mouseStack.grow(j2);
                                slotStack = slot6.remove(j2);

                                if (slotStack.isEmpty()) {
                                    slot6.set(ItemStack.EMPTY);
                                }

                                slot6.onTake(player, PlayerInventory.getCarried());
                            }
                        }
                    }

                    slot6.setChanged();
                }
            }
        } else if (clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9) {
            //just don't do anything as swapping slots is unsupported
      /*
      Slot slot4 = this.inventorySlots.get(slotId);
      ItemStack itemstack6 = PlayerInventory.getStackInSlot(dragType);
      ItemStack slotStack = slot4.getStack();

      if (!itemstack6.isEmpty() || !slotStack.isEmpty()) {
        if (itemstack6.isEmpty()) {
          if (slot4.canTakeStack(player)) {
            int maxAmount = slotStack.getMaxStackSize();
            if (slotStack.getCount() > maxAmount) {
              ItemStack newSlotStack = slotStack.copy();
              ItemStack takenStack = newSlotStack.split(maxAmount);
              PlayerInventory.setInventorySlotContents(dragType, takenStack);
              //slot4.onSwapCraft(takenStack.getCount());
              slot4.putStack(newSlotStack);
              slot4.onTake(player, takenStack);
            } else {
              PlayerInventory.setInventorySlotContents(dragType, slotStack);
              //slot4.onSwapCraft(slotStack.getCount());
              slot4.putStack(ItemStack.EMPTY);
              slot4.onTake(player, slotStack);
            }
          }
        } else if (slotStack.isEmpty()) {
          if (slot4.isItemValid(itemstack6)) {
            int l1 = slot4.getItemStackLimit(itemstack6);

            if (itemstack6.getCount() > l1) {
              slot4.putStack(itemstack6.split(l1));
            } else {
              slot4.putStack(itemstack6);
              PlayerInventory.setInventorySlotContents(dragType, ItemStack.EMPTY);
            }
          }
        } else if (slot4.canTakeStack(player) && slot4.isItemValid(itemstack6)) {
          int i2 = slot4.getItemStackLimit(itemstack6);

          if (itemstack6.getCount() > i2) {
            slot4.putStack(itemstack6.split(i2));
            slot4.onTake(player, slotStack);

            if (!PlayerInventory.addItemStackToInventory(slotStack)) {
              player.dropItem(slotStack, true);
            }
          } else {
            slot4.putStack(itemstack6);
            if (slotStack.getCount() > slotStack.getMaxStackSize()) {
              ItemStack remainder = slotStack.copy();
              PlayerInventory.setInventorySlotContents(dragType, remainder.split(slotStack.getMaxStackSize()));
              if (!PlayerInventory.addItemStackToInventory(remainder)) {
                player.dropItem(remainder, true);
              }
            } else {
              PlayerInventory.setInventorySlotContents(dragType, slotStack);
            }
            slot4.onTake(player, slotStack);
          }
        }
      }*/
        } else if (clickTypeIn == ClickType.CLONE && player.abilities.instabuild && PlayerInventory.getCarried().isEmpty() && slotId >= 0) {
            Slot slot3 = this.slots.get(slotId);

            if (slot3 != null && slot3.hasItem()) {
                ItemStack itemstack5 = slot3.getItem().copy();
                itemstack5.setCount(itemstack5.getMaxStackSize());
                PlayerInventory.setCarried(itemstack5);
            }
        } else if (clickTypeIn == ClickType.THROW && PlayerInventory.getCarried().isEmpty() && slotId >= 0) {
            Slot slot2 = this.slots.get(slotId);

            if (slot2 != null && slot2.hasItem() && slot2.mayPickup(player)) {
                ItemStack itemstack4 = slot2.remove(dragType == 0 ? 1 : slot2.getItem().getCount());
                slot2.onTake(player, itemstack4);
                player.drop(itemstack4, true);
            }
        } else if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0) {
            Slot slot = this.slots.get(slotId);
            ItemStack mouseStack = PlayerInventory.getCarried();

            if (!mouseStack.isEmpty() && (slot == null || !slot.hasItem() || !slot.mayPickup(player))) {
                int i = dragType == 0 ? 0 : this.slots.size() - 1;
                int j = dragType == 0 ? 1 : -1;

                for (int k = 0; k < 2; ++k) {
                    for (int l = i; l >= 0 && l < this.slots.size() && mouseStack.getCount() < mouseStack.getMaxStackSize(); l += j) {
                        Slot slot1 = this.slots.get(l);

                        if (slot1.hasItem() && DockMenu.canItemQuickReplace(slot1, mouseStack, true) && slot1.mayPickup(player) && this.canTakeItemForPickAll(mouseStack, slot1)) {
                            ItemStack itemstack2 = slot1.getItem();

                            if (k != 0 || itemstack2.getCount() < slot1.getMaxStackSize(itemstack2)) {
                                int i1 = Math.min(mouseStack.getMaxStackSize() - mouseStack.getCount(), itemstack2.getCount());
                                ItemStack itemstack3 = slot1.remove(i1);
                                mouseStack.grow(i1);

                                if (itemstack3.isEmpty()) {
                                    slot1.set(ItemStack.EMPTY);
                                }

                                slot1.onTake(player, itemstack3);
                            }
                        }
                    }
                }
            }

            this.broadcastChanges();
        }

        if (itemstack.getCount() > 64) {
            itemstack = itemstack.copy();
            itemstack.setCount(64);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn)
    {
        return true;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection)
    {
        boolean flag = false;
        int i = startIndex;

        if (reverseDirection) {
            i = endIndex - 1;
        }

        while (!stack.isEmpty()) {
            if (reverseDirection) {
                if (i < startIndex) break;
            } else {
                if (i >= endIndex) break;
            }

            Slot slot = this.slots.get(i);
            ItemStack itemstack = slot.getItem();

            if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem() && ItemStack.tagMatches(stack, itemstack)) {
                int j = itemstack.getCount() + stack.getCount();
                int maxSize = slot.getMaxStackSize(itemstack);

                if (j <= maxSize) {
                    stack.setCount(0);
                    itemstack.setCount(j);
                    slot.setChanged();
                    flag = true;
                } else if (itemstack.getCount() < maxSize) {
                    stack.shrink(maxSize - itemstack.getCount());
                    itemstack.setCount(maxSize);
                    slot.setChanged();
                    flag = true;
                }
            }

            i += (reverseDirection) ? -1 : 1;
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) i = endIndex - 1;
            else i = startIndex;

            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) break;
                } else {
                    if (i >= endIndex) break;
                }

                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();

                if (itemstack1.isEmpty() && slot1.mayPlace(stack)) {
                    if (stack.getCount() > slot1.getMaxStackSize(stack)) {
                        slot1.set(stack.split(slot1.getMaxStackSize(stack)));
                    } else {
                        slot1.set(stack.split(stack.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                i += (reverseDirection) ? -1 : 1;
            }
        }

        return flag;
    }

    //don't touch this
    @Override
    public void broadcastChanges()
    {
        for (int i = 0; i < this.slots.size(); ++i) {
            ItemStack itemstack = this.slots.get(i).getItem();
            ItemStack itemstack1 = this.lastSlots.get(i);

            if (!ItemStack.matches(itemstack1, itemstack)) {
                itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
                this.lastSlots.set(i, itemstack1);

                for (ContainerListener listener : this.containerListeners) {
                    if (listener instanceof ServerPlayer) {
                        ServerPlayer player = (ServerPlayer) listener;

                        this.syncSlot(player, i, itemstack1);
                    }
                }
            }
        }

        for (int j = 0; j < this.dataSlots.size(); ++j) {
            DataSlot property = this.dataSlots.get(j);
            if (property.checkAndClearUpdateFlag()) {

                for (ContainerListener containerListener : this.containerListeners) {
                    containerListener.setContainerData(this, j, property.get());
                }
            }
        }

    }

    @Override
    public void addSlotListener(ContainerListener listener)
    {
        if (this.containerListeners.contains(listener)) {
            throw new IllegalArgumentException("Listener already listening");
        } else {
            this.containerListeners.add(listener);
            if (listener instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) listener;

                this.syncInventory(player);
            }
            this.broadcastChanges();
        }
    }

    public void syncInventory(ServerPlayer player)
    {
        for (int i = 0; i < this.slots.size(); i++) {
            ItemStack stack = (this.slots.get(i)).getItem();
            S2CSyncExtendedSlotContents.send(player, this.containerId, i, stack);
        }
        player.connection.send(new ClientboundContainerSetSlotPacket(-1, -1, player.inventory.getCarried()));
    }

    public void syncSlot(ServerPlayer player, int slot, ItemStack stack)
    {
        S2CSyncExtendedSlotContents.send(player, this.containerId, slot, stack);
    }
}
