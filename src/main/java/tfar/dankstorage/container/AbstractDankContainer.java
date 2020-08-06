package tfar.dankstorage.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.S2CSyncExtendedSlotContents;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

public abstract class AbstractDankContainer extends ScreenHandler {

  public final int rows;
  public final PlayerInventory playerInventory;
  public DankInventory dankInventory;
  public final PropertyDelegate propertyDelegate;

  //client
  public AbstractDankContainer(ScreenHandlerType<?> type, int p_i50105_2_, PlayerInventory playerInventory, int rows) {
    this(type, p_i50105_2_,playerInventory,rows,new DankInventory(Utils.getStatsfromRows(rows)), new ArrayPropertyDelegate(rows * 9));
  }

  public AbstractDankContainer(ScreenHandlerType<?> type, int p_i50105_2_, PlayerInventory playerInventory, int rows, DankInventory dankInventory, PropertyDelegate propertyDelegate) {
    super(type, p_i50105_2_);
    this.rows = rows;
    this.playerInventory = playerInventory;
    this.dankInventory = dankInventory;
    this.propertyDelegate = propertyDelegate;
    addProperties(propertyDelegate);
  }

  protected void addDankSlots() {
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

  protected void addPlayerSlots(PlayerInventory playerinventory) {
    int yStart = 32 + 18 * rows;
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + yStart;
        this.addSlot(new Slot(playerinventory, col + row * 9 + 9, x, y) {
          @Override
          public int getMaxStackAmount(ItemStack stack) {
            return Math.min(this.getMaxStackAmount(), stack.getMaxCount());
          }
        });
      }
    }

    for (int row = 0; row < 9; ++row) {
      int x = 8 + row * 18;
      int y = yStart + 58;
      this.addSlot(new Slot(playerinventory, row, x, y) {
        @Override
        public int getMaxStackAmount(ItemStack stack) {
          return Math.min(this.getMaxStackAmount(), stack.getMaxCount());
        }
      });
    }
  }

  @Nonnull
  @Override
  public ItemStack transferSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);

    if (slot != null && slot.hasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();

      if (index < rows * 9) {
        if (!this.insertItem(itemstack1, rows * 9, this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.insertItem(itemstack1, 0, rows * 9, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.setStack(ItemStack.EMPTY);
      } else {
        slot.markDirty();
      }
    }

    return itemstack;
  }


  @Nonnull
  @Override
  public ItemStack onSlotClick(int slotId, int dragType, SlotActionType clickTypeIn, PlayerEntity player) {
    boolean locked = slotId >= 0 && slotId < (rows * 9) && propertyDelegate.get(slotId) == 1;
    ItemStack itemstack = ItemStack.EMPTY;
    PlayerInventory PlayerInventory = player.inventory;

    if (clickTypeIn == SlotActionType.QUICK_CRAFT) {
      int j1 = this.quickCraftButton;
      this.quickCraftButton = unpackQuickCraftStage(dragType);

      if ((j1 != 1 || this.quickCraftButton != 2) && j1 != this.quickCraftButton) {
        this.endQuickCraft();
      } else if (PlayerInventory.getCursorStack().isEmpty()) {
        this.endQuickCraft();
      } else if (this.quickCraftButton == 0) {
        this.quickCraftStage = unpackQuickCraftButton(dragType);

        if (shouldQuickCraftContinue(this.quickCraftStage, player)) {
          this.quickCraftButton = 1;
          this.quickCraftSlots.clear();
        } else {
          this.endQuickCraft();
        }
      } else if (this.quickCraftButton == 1) {
        Slot slot7 = this.slots.get(slotId);
        ItemStack mouseStack = PlayerInventory.getCursorStack();

        if (slot7 != null && DockContainer.canInsertItemIntoSlot(slot7, mouseStack, true) && slot7.canInsert(mouseStack) && (this.quickCraftStage == 2 || mouseStack.getCount() > this.quickCraftSlots.size()) && this.canInsertIntoSlot(slot7)) {
          this.quickCraftSlots.add(slot7);
        }
      } else if (this.quickCraftButton == 2) {
        if (!this.quickCraftSlots.isEmpty()) {
          ItemStack mouseStackCopy = PlayerInventory.getCursorStack().copy();
          int k1 = PlayerInventory.getCursorStack().getCount();

          for (Slot dragSlot : this.quickCraftSlots) {
            ItemStack mouseStack = PlayerInventory.getCursorStack();

            if (dragSlot != null && DockContainer.canInsertItemIntoSlot(dragSlot, mouseStack, true) && dragSlot.canInsert(mouseStack) && (this.quickCraftStage == 2 || mouseStack.getCount() >= this.quickCraftSlots.size()) && this.canInsertIntoSlot(dragSlot)) {
              ItemStack itemstack14 = mouseStackCopy.copy();
              int j3 = dragSlot.hasStack() ? dragSlot.getStack().getCount() : 0;
              calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemstack14, j3);
              int k3 = dragSlot.getMaxStackAmount(itemstack14);

              if (itemstack14.getCount() > k3) {
                itemstack14.setCount(k3);
              }

              k1 -= itemstack14.getCount() - j3;
              dragSlot.setStack(itemstack14);
            }
          }

          mouseStackCopy.setCount(k1);
          PlayerInventory.setCursorStack(mouseStackCopy);
        }

        this.endQuickCraft();
      } else {
        this.endQuickCraft();
      }
    } else if (this.quickCraftButton != 0) {
      this.endQuickCraft();
    } else if ((clickTypeIn == SlotActionType.PICKUP || clickTypeIn == SlotActionType.QUICK_MOVE) && (dragType == 0 || dragType == 1)) {
      if (slotId == -999) {
        if (!PlayerInventory.getCursorStack().isEmpty()) {
          if (dragType == 0) {
            player.dropItem(PlayerInventory.getCursorStack(), true);
            PlayerInventory.setCursorStack(ItemStack.EMPTY);
          }

          if (dragType == 1) {
            player.dropItem(PlayerInventory.getCursorStack().split(1), true);
          }
        }
      } else if (clickTypeIn == SlotActionType.QUICK_MOVE) {
        if (slotId < 0) {
          return ItemStack.EMPTY;
        }

        Slot slot5 = this.slots.get(slotId);

        if (slot5 == null || !slot5.canTakeItems(player)) {
          return ItemStack.EMPTY;
        }

        for (ItemStack itemstack7 = this.transferSlot(player, slotId); !itemstack7.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot5.getStack(), itemstack7); itemstack7 = this.transferSlot(player, slotId)) {
          itemstack = itemstack7.copy();
        }
      } else {
        if (slotId < 0) {
          return ItemStack.EMPTY;
        }

        Slot slot6 = this.slots.get(slotId);

        if (slot6 != null) {
          ItemStack slotStack = slot6.getStack();
          ItemStack mouseStack = PlayerInventory.getCursorStack();

          //account for locked slots
          if (!slotStack.isEmpty()) {
            if (slot6 instanceof DankSlot && locked) {
             ItemStack stack = slotStack.copy();
             stack.decrement(1);
             itemstack = stack;
            } else {
              itemstack = slotStack.copy();
            }
          }

          if (slotStack.isEmpty()) {
            if (!mouseStack.isEmpty() && slot6.canInsert(mouseStack)) {
              int i3 = dragType == 0 ? mouseStack.getCount() : 1;

              if (i3 > slot6.getMaxStackAmount(mouseStack)) {
                i3 = slot6.getMaxStackAmount(mouseStack);
              }

              slot6.setStack(mouseStack.split(i3));
            }
          } else if (slot6.canTakeItems(player)) {
            if (mouseStack.isEmpty()) {
              if (slotStack.isEmpty()) {
                slot6.setStack(ItemStack.EMPTY);
                PlayerInventory.setCursorStack(ItemStack.EMPTY);
              } else {
                int toMove;
                if (slot6 instanceof DankSlot) {
                  if (slotStack.getMaxCount() <= slotStack.getCount()) {
                    if (dragType == 0) {
                      if (locked) {
                        toMove = slotStack.getMaxCount() - 1;
                      } else {
                        toMove = slotStack.getMaxCount();
                      }
                    } else {
                      toMove = (slotStack.getMaxCount() + 1) / 2;
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
                PlayerInventory.setCursorStack(slot6.takeStack(toMove));

                if (slotStack.isEmpty()) {
                  slot6.setStack(ItemStack.EMPTY);
                }

                slot6.onTakeItem(player, PlayerInventory.getCursorStack());
              }
            } else if (slot6.canInsert(mouseStack)) {
              if (slotStack.getItem() == mouseStack.getItem() && ItemStack.areTagsEqual(slotStack, mouseStack)) {
                int k2 = dragType == 0 ? mouseStack.getCount() : 1;

                if (k2 > slot6.getMaxStackAmount(mouseStack) - slotStack.getCount()) {
                  k2 = slot6.getMaxStackAmount(mouseStack) - slotStack.getCount();
                }

                mouseStack.decrement(k2);
                slotStack.increment(k2);
              } else if (mouseStack.getCount() <= slot6.getMaxStackAmount(mouseStack) && slotStack.getCount() <= slotStack.getMaxCount()) {
                slot6.setStack(mouseStack);
                PlayerInventory.setCursorStack(slotStack);
              }
            } else if (slotStack.getItem() == mouseStack.getItem() && mouseStack.getMaxCount() > 1 && ItemStack.areTagsEqual(slotStack, mouseStack) && !slotStack.isEmpty()) {
              int j2 = slotStack.getCount();

              if (j2 + mouseStack.getCount() <= mouseStack.getMaxCount()) {
                mouseStack.increment(j2);
                slotStack = slot6.takeStack(j2);

                if (slotStack.isEmpty()) {
                  slot6.setStack(ItemStack.EMPTY);
                }

                slot6.onTakeItem(player, PlayerInventory.getCursorStack());
              }
            }
          }

          slot6.markDirty();
        }
      }
    } else if (clickTypeIn == SlotActionType.SWAP && dragType >= 0 && dragType < 9) {
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
    } else if (clickTypeIn == SlotActionType.CLONE && player.abilities.creativeMode && PlayerInventory.getCursorStack().isEmpty() && slotId >= 0) {
      Slot slot3 = this.slots.get(slotId);

      if (slot3 != null && slot3.hasStack()) {
        ItemStack itemstack5 = slot3.getStack().copy();
        itemstack5.setCount(itemstack5.getMaxCount());
        PlayerInventory.setCursorStack(itemstack5);
      }
    } else if (clickTypeIn == SlotActionType.THROW && PlayerInventory.getCursorStack().isEmpty() && slotId >= 0) {
      Slot slot2 = this.slots.get(slotId);

      if (slot2 != null && slot2.hasStack() && slot2.canTakeItems(player)) {
        ItemStack itemstack4 = slot2.takeStack(dragType == 0 ? 1 : slot2.getStack().getCount());
        slot2.onTakeItem(player, itemstack4);
        player.dropItem(itemstack4, true);
      }
    } else if (clickTypeIn == SlotActionType.PICKUP_ALL && slotId >= 0) {
      Slot slot = this.slots.get(slotId);
      ItemStack mouseStack = PlayerInventory.getCursorStack();

      if (!mouseStack.isEmpty() && (slot == null || !slot.hasStack() || !slot.canTakeItems(player))) {
        int i = dragType == 0 ? 0 : this.slots.size() - 1;
        int j = dragType == 0 ? 1 : -1;

        for (int k = 0; k < 2; ++k) {
          for (int l = i; l >= 0 && l < this.slots.size() && mouseStack.getCount() < mouseStack.getMaxCount(); l += j) {
            Slot slot1 = this.slots.get(l);

            if (slot1.hasStack() && DockContainer.canInsertItemIntoSlot(slot1, mouseStack, true) && slot1.canTakeItems(player) && this.canInsertIntoSlot(mouseStack, slot1)) {
              ItemStack itemstack2 = slot1.getStack();

              if (k != 0 || itemstack2.getCount() < slot1.getMaxStackAmount(itemstack2)) {
                int i1 = Math.min(mouseStack.getMaxCount() - mouseStack.getCount(), itemstack2.getCount());
                ItemStack itemstack3 = slot1.takeStack(i1);
                mouseStack.increment(i1);

                if (itemstack3.isEmpty()) {
                  slot1.setStack(ItemStack.EMPTY);
                }

                slot1.onTakeItem(player, itemstack3);
              }
            }
          }
        }
      }

      this.sendContentUpdates();
    }

    if (itemstack.getCount() > 64) {
      itemstack = itemstack.copy();
      itemstack.setCount(64);
    }

    return itemstack;
  }

  @Override
  public boolean canUse(@Nonnull PlayerEntity playerIn) {
    return true;
  }

  @Override
  protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
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
      ItemStack itemstack = slot.getStack();

      if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem() && ItemStack.areTagsEqual(stack, itemstack)) {
        int j = itemstack.getCount() + stack.getCount();
        int maxSize = slot.getMaxStackAmount(itemstack);

        if (j <= maxSize) {
          stack.setCount(0);
          itemstack.setCount(j);
          slot.markDirty();
          flag = true;
        } else if (itemstack.getCount() < maxSize) {
          stack.decrement(maxSize - itemstack.getCount());
          itemstack.setCount(maxSize);
          slot.markDirty();
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
        ItemStack itemstack1 = slot1.getStack();

        if (itemstack1.isEmpty() && slot1.canInsert(stack)) {
          if (stack.getCount() > slot1.getMaxStackAmount(stack)) {
            slot1.setStack(stack.split(slot1.getMaxStackAmount(stack)));
          } else {
            slot1.setStack(stack.split(stack.getCount()));
          }

          slot1.markDirty();
          flag = true;
          break;
        }

        i += (reverseDirection) ? -1 : 1;
      }
    }

    return flag;
  }

  public static boolean canInsertItemIntoSlot(@Nullable Slot slot, @Nonnull ItemStack stack, boolean stackSizeMatters) {
    boolean flag = slot == null || !slot.hasStack();
    if (slot != null) {
      ItemStack slotStack = slot.getStack();

      if (!flag && stack.isItemEqualIgnoreDamage(slotStack) && ItemStack.areTagsEqual(slotStack, stack)) {
        return slotStack.getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= slot.getMaxStackAmount(slotStack);
      }
    }
    return flag;
  }

  //don't touch this
  @Override
  public void sendContentUpdates() {
    for (int i = 0; i < this.slots.size(); ++i) {
      ItemStack itemstack = (this.slots.get(i)).getStack();
      ItemStack itemstack1 = this.trackedStacks.get(i);

      if (!ItemStack.areEqual(itemstack1, itemstack)) {
        itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
        this.trackedStacks.set(i, itemstack1);

        for (int j = 0; j < this.listeners.size(); ++j) {
          ScreenHandlerListener listener = this.listeners.get(j);
          if (listener instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) listener;

            this.syncSlot(player, i, itemstack1);
          }
        }
      }
    }

    for(int j = 0; j < this.properties.size(); ++j) {
      Property property = (Property)this.properties.get(j);
      if (property.hasChanged()) {
        Iterator var8 = this.listeners.iterator();

        while(var8.hasNext()) {
          ScreenHandlerListener screenHandlerListener2 = (ScreenHandlerListener)var8.next();
          screenHandlerListener2.onPropertyUpdate(this, j, property.get());
        }
      }
    }

  }

  @Override
  public void addListener(ScreenHandlerListener listener) {
    if (this.listeners.contains(listener)) {
      throw new IllegalArgumentException("Listener already listening");
    } else {
      this.listeners.add(listener);
      if (listener instanceof ServerPlayerEntity) {
        ServerPlayerEntity player = (ServerPlayerEntity) listener;

        this.syncInventory(player);
      }
      this.sendContentUpdates();
    }
  }

  public void syncInventory(ServerPlayerEntity player) {
    for (int i = 0; i < this.slots.size(); i++) {
      ItemStack stack = (this.slots.get(i)).getStack();
      S2CSyncExtendedSlotContents.send(player,this.syncId, i, stack);
    }
    player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, player.inventory.getCursorStack()));
  }

  public void syncSlot(ServerPlayerEntity player, int slot, ItemStack stack) {
    S2CSyncExtendedSlotContents.send(player,this.syncId, slot, stack);
  }
}
