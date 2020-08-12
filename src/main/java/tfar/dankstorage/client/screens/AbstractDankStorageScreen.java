package tfar.dankstorage.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import tfar.dankstorage.client.BigItemRenderer;
import tfar.dankstorage.client.button.SmallButton;
import tfar.dankstorage.container.AbstractDankContainer;
import tfar.dankstorage.container.DockContainer;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.server.C2SMessageLockSlot;
import tfar.dankstorage.network.server.C2SMessageSort;
import tfar.dankstorage.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL13;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public abstract class AbstractDankStorageScreen<T extends AbstractDankContainer> extends HandledScreen<T> {

	final Identifier background;//= new ResourceLocation("textures/gui/container/shulker_box.png");

	protected final boolean is7;

	public AbstractDankStorageScreen(T container, PlayerInventory playerinventory, Text component, Identifier background) {
		super(container, playerinventory, component);
		this.background = background;
		this.backgroundHeight = 114 + this.handler.rows * 18;
		this.cancelNextRelease = true;
		this.is7 = this.handler.rows > 6;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new SmallButton(x + 143, y + 4, 26, 12, new LiteralText("Sort"), b -> {
			C2SMessageSort.send();
		}));
	}

	@Override
	protected void drawBackground(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {

		client.getTextureManager().bindTexture(background);
		if (is7)
			drawTexture(stack, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 512);
		else
			drawTexture(stack, x, y, 0, 0, backgroundWidth, backgroundHeight);


		for (int i = 0; i < (handler.rows * 9); i++) {
			int j = i % 9;
			int k = i / 9;
			int offsetx = 8;
			int offsety = 18;
			if (this.handler.propertyDelegate.get(i) == 1)
				fill(stack, x + j * 18 + offsetx, y + k * 18 + offsety,
								x + j * 18 + offsetx + 16, y + k * 18 + offsety + 16, 0xFFFF0000);
		}
	}

	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(stack);
		int i = this.x;
		int j = this.y;
		this.drawBackground(stack, partialTicks, mouseX, mouseY);
		RenderSystem.disableRescaleNormal();
		DiffuseLighting.disable();
		RenderSystem.disableLighting();
		RenderSystem.disableDepthTest();

		IntStream.range(0, this.buttons.size()).forEach(k -> this.buttons.get(k).render(stack, mouseX, mouseY, partialTicks));
		// for (int l = 0; l < this.buttons.size(); ++l) {
		// this.buttons.get(l).drawLabel(this.minecraft, mouseX, mouseY);
		// }

		RenderSystem.pushMatrix();
		RenderSystem.translatef((float) i, (float) j, 0.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableRescaleNormal();
		this.focusedSlot = null;
		int k = 240;
		int l = 240;
		RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, k, l);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		for (int i1 = 0; i1 < this.handler.slots.size(); ++i1) {
			Slot slot = this.handler.slots.get(i1);

			if (slot.doDrawHoveringEffect()) {
				this.drawSlot(stack, slot);
			}

			if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.doDrawHoveringEffect()) {
				this.focusedSlot = slot;
				RenderSystem.disableLighting();
				RenderSystem.disableDepthTest();
				int j1 = slot.x;
				int k1 = slot.y;
				RenderSystem.colorMask(true, true, true, false);
				this.fillGradient(stack, j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableLighting();
				RenderSystem.enableDepthTest();
			}
		}

		DiffuseLighting.disable();
		this.drawForeground(stack, mouseX, mouseY);
		PlayerInventory inventoryplayer = this.client.player.inventory;
		ItemStack itemstack = this.touchDragStack.isEmpty() ? inventoryplayer.getCursorStack() : this.touchDragStack;

		if (!itemstack.isEmpty()) {
			int k2 = this.touchDragStack.isEmpty() ? 8 : 16;
			String s = null;

			if (!this.touchDragStack.isEmpty() && this.touchIsRightClickDrag) {
				itemstack = itemstack.copy();
				itemstack.setCount(MathHelper.ceil((float) itemstack.getCount() / 2.0F));
			} else if (this.cursorDragging && this.cursorDragSlots.size() > 1) {
				itemstack = itemstack.copy();
				itemstack.setCount(this.draggedStackRemainder);

				if (itemstack.isEmpty()) {
					s = "" + Formatting.YELLOW + "0";
				}
			}

			this.drawItem(itemstack, mouseX - i - 8, mouseY - j - k2, s);
		}

		if (!this.touchDropReturningStack.isEmpty()) {
			float f = (float) (System.currentTimeMillis() - this.touchDropTime) / 100.0F;

			if (f >= 1.0F) {
				f = 1.0F;
				this.touchDropReturningStack = ItemStack.EMPTY;
			}

			int l2 = this.touchDropOriginSlot.x - this.touchDropX;
			int i3 = this.touchDropOriginSlot.y - this.touchDropY;
			int l1 = this.touchDropX + (int) ((float) l2 * f);
			int i2 = this.touchDropY + (int) ((float) i3 * f);
			this.drawItem(this.touchDropReturningStack, l1, i2, null);
		}

		RenderSystem.popMatrix();
		RenderSystem.enableDepthTest();

		this.drawMouseoverTooltip(stack, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(MatrixStack matrices, ItemStack stack, int mouseX, int mouseY) {
		super.renderTooltip(matrices, stack, mouseX, mouseY);
		/*List<Text> tooltip = this.getTooltipFromItem(stack);
		if (focusedSlot != null) {
			appendDankInfo(tooltip,stack);
		}*/
	}

	public void appendDankInfo(List<Text> tooltip,ItemStack stack) {
		if (focusedSlot.getStack().getItem().isIn(Utils.BLACKLISTED_STORAGE)) {
			Text component1 = new TranslatableText("text.dankstorage.blacklisted_storage").formatted(Formatting.DARK_RED);
			tooltip.add(component1);
		}
		if (focusedSlot.getStack().getItem().isIn(Utils.BLACKLISTED_USAGE)) {
			Text component1 = new TranslatableText("text.dankstorage.blacklisted_usage").
							formatted(Formatting.DARK_RED);
			tooltip.add(component1);
		}
		if (focusedSlot instanceof DankSlot) {
			Text component2 = new TranslatableText("text.dankstorage.lock",
							new LiteralText("ctrl").formatted(Formatting.YELLOW)).formatted(Formatting.GRAY);
			tooltip.add(component2);
			if (focusedSlot.getStack().getCount() >= 1000) {
				Text component3 = new TranslatableText(
								"text.dankstorage.exact", new LiteralText(Integer.toString(focusedSlot.getStack().getCount())).formatted(Formatting.AQUA)).formatted(Formatting.GRAY);
				tooltip.add(component3);
			}
		}
	}

	private void drawItem(ItemStack stack, int x, int y, String altText) {
		RenderSystem.translatef(0.0F, 0.0F, 32.0F);
		this.setZOffset(200);
		this.itemRenderer.zOffset = 200.0F;
		BigItemRenderer.INSTANCE.zOffset = this.itemRenderer.zOffset;
		this.itemRenderer.renderInGuiWithOverrides(stack, x, y);
		this.itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y - (this.touchDragStack.isEmpty() ? 0 : 8), altText);
		this.setZOffset(0);
		this.itemRenderer.zOffset = 0.0F;
		BigItemRenderer.INSTANCE.zOffset = this.itemRenderer.zOffset;
	}

	private void drawSlot(MatrixStack matrices, Slot slotIn) {
		int i = slotIn.x;
		int j = slotIn.y;
		ItemStack itemstack = slotIn.getStack();
		boolean flag = false;
		boolean flag1 = slotIn == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
		ItemStack itemstack1 = this.client.player.inventory.getCursorStack();
		String s = null;

		if (slotIn == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemstack.isEmpty()) {
			itemstack = itemstack.copy();
			itemstack.setCount(itemstack.getCount() / 2);
		} else if (this.cursorDragging && this.cursorDragSlots.contains(slotIn) && !itemstack1.isEmpty()) {
			if (this.cursorDragSlots.size() == 1) {
				return;
			}

			if (DockContainer.canInsertItemIntoSlot(slotIn, itemstack1, true) && this.handler.canInsertIntoSlot(slotIn)) {
				itemstack = itemstack1.copy();
				flag = true;
				ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemstack, slotIn.getStack().isEmpty() ? 0 : slotIn.getStack().getCount());
				int k = slotIn.getMaxItemCount(itemstack);

				if (itemstack.getCount() > k) {
					s = Formatting.YELLOW.toString() + k;
					itemstack.setCount(k);
				}
			} else {
				this.cursorDragSlots.remove(slotIn);
				this.calculateOffset();
			}
		}

		this.setZOffset(100);
		this.itemRenderer.zOffset = 100;

		if (itemstack.isEmpty() && slotIn.doDrawHoveringEffect()) {
			Pair<Identifier, Identifier> pair = slotIn.getBackgroundSprite();

			if (pair != null) {
				Sprite textureatlassprite = this.client.getSpriteAtlas(pair.getFirst()).apply(pair.getSecond());
				this.client.getTextureManager().bindTexture(textureatlassprite.getAtlas().getId());
				drawSprite(matrices, i, j, this.getZOffset(), 16, 16, textureatlassprite);
				flag1 = true;
			}
		}

		if (!flag1) {
			if (flag) {
				fill(matrices, i, j, i + 16, j + 16, 0x80ffffff);
			}

			RenderSystem.enableDepthTest();
			this.itemRenderer.renderInGuiWithOverrides(this.client.player, itemstack, i, j);
			BigItemRenderer.INSTANCE.zOffset = this.itemRenderer.zOffset;
			if (slotIn instanceof DankSlot) {
				BigItemRenderer.INSTANCE.renderGuiItemOverlay(this.textRenderer, itemstack, i, j, s);
			} else {
				this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemstack, i, j, s);
			}
		}

		this.itemRenderer.zOffset = 0.0F;
		this.setZOffset(0);
		BigItemRenderer.INSTANCE.zOffset = itemRenderer.zOffset;
	}

	private void calculateOffset() {
		ItemStack itemstack = this.client.player.inventory.getCursorStack();

		if (!itemstack.isEmpty() && this.cursorDragging) {
			if (this.heldButtonType == 2) {
				this.draggedStackRemainder = itemstack.getMaxCount();
			} else {
				this.draggedStackRemainder = itemstack.getCount();

				for (Slot slot : this.cursorDragSlots) {
					ItemStack itemstack1 = itemstack.copy();
					ItemStack itemstack2 = slot.getStack();
					int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
					ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemstack1, i);
					//int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));
					int j = slot.getMaxItemCount(itemstack1);

					if (itemstack1.getCount() > j) {
						itemstack1.setCount(j);
					}

					this.draggedStackRemainder -= itemstack1.getCount() - i;
				}
			}
		}
	}

	private Slot getSlotAtPosition(double x, double y) {
		for (int i = 0; i < this.handler.slots.size(); ++i) {
			Slot slot = this.handler.slots.get(i);

			if (this.isMouseOverSlot(slot, (int) x, (int) y) && slot.doDrawHoveringEffect()) {
				return slot;
			}
		}

		return null;
	}

	public boolean mouseclicked(double mouseX, double mouseY, int mouseButton) {
		Iterator<Element> var6 = this.children.iterator();

		Element iGuiEventListener;
		if (!var6.hasNext()) {
			return false;
		}

		iGuiEventListener = var6.next();
		while (!iGuiEventListener.mouseClicked(mouseX, mouseY, mouseButton)) {
			if (!var6.hasNext()) {
				return false;
			}
			iGuiEventListener = var6.next();
		}

		this.setFocused(iGuiEventListener);
		if (mouseButton == 0) {
			this.setDragging(true);
		}
		return true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

		if (mouseclicked(mouseX, mouseY, mouseButton)) return true;

		if (Screen.hasControlDown()) {
			Slot slot = getSlotAtPosition(mouseX, mouseY);
			if (slot instanceof DankSlot) {
				C2SMessageLockSlot.send(slot.id);
				return true;
			}
		}

		InputUtil.Key mouseKey = InputUtil.Type.MOUSE.createFromCode(mouseButton);
		boolean isPickBlock = false;//this.client.options.keyPickItem.isActiveAndMatches(mouseKey);
		Slot slot = this.getSlotAtPosition(mouseX, mouseY);
		long i = System.currentTimeMillis();
		this.doubleClicking = this.lastClickedSlot == slot && i - this.lastButtonClickTime < 250L && this.lastClickedButton == mouseButton;
		this.cancelNextRelease = false;

		if (mouseButton == 0 || mouseButton == 1 || isPickBlock) {
			int j = this.x;
			int k = this.y;
			boolean clickedOutsideGui = this.isClickOutsideBounds(mouseX, mouseY, j, k, mouseButton);
			if (slot != null)
				clickedOutsideGui = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
			int l = -1;

			if (slot != null) {
				l = slot.id;
			}

			if (clickedOutsideGui) {
				l = -999;
			}

			if (this.client.options.touchscreen && clickedOutsideGui && this.client.player.inventory.getCursorStack().isEmpty()) {
				this.client.openScreen(null);
				return false;
			}

			if (l != -1) {
				if (this.client.options.touchscreen) {
					if (slot != null && slot.hasStack()) {
						this.touchDragSlotStart = slot;
						this.touchDragStack = ItemStack.EMPTY;
						this.touchIsRightClickDrag = mouseButton == 1;
					} else {
						this.touchDragSlotStart = null;
					}
				} else if (!this.cursorDragging) {
					if (this.client.player.inventory.getCursorStack().isEmpty()) {
						if (/*this.client.options.keyPickItem.isActiveAndMatches(mouseKey)*/false) {
							this.onMouseClick(slot, l, mouseButton, SlotActionType.CLONE);
						} else {
							boolean flag2 = l != -999 && Screen.hasShiftDown();
							SlotActionType clicktype = SlotActionType.PICKUP;

							if (flag2) {
								this.quickMovingStack = slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
								clicktype = SlotActionType.QUICK_MOVE;
							} else if (l == -999) {
								clicktype = SlotActionType.THROW;
							}

							this.onMouseClick(slot, l, mouseButton, clicktype);
						}

						this.cancelNextRelease = true;
					} else {
						this.cursorDragging = true;
						this.heldButtonCode = mouseButton;
						this.cursorDragSlots.clear();

						if (mouseButton == 0) {
							this.heldButtonType = 0;
						} else if (mouseButton == 1) {
							this.heldButtonType = 1;
						} else if (/*this.client.options.keyPickItem.isActiveAndMatches(mouseKey)*/false) {
							this.heldButtonType = 2;
						}
					}
				}
			}
		}

		this.lastClickedSlot = slot;
		this.lastButtonClickTime = i;
		this.lastClickedButton = mouseButton;
		return true;
	}


	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double timeSinceLastClick, double param1) {
		Slot slot = this.getSlotAtPosition(mouseX, mouseY);
		ItemStack itemstack = this.client.player.inventory.getCursorStack();

		if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
			if (clickedMouseButton == 0 || clickedMouseButton == 1) {
				if (this.touchDragStack.isEmpty()) {
					if (slot != this.touchDragSlotStart && !this.touchDragSlotStart.getStack().isEmpty()) {
						this.touchDragStack = this.touchDragSlotStart.getStack().copy();
					}
				} else if (this.touchDragStack.getCount() > 1 && slot != null && DockContainer.canInsertItemIntoSlot(slot, this.touchDragStack, false)) {
					long i = System.currentTimeMillis();

					if (this.touchHoveredSlot == slot) {
						if (i - this.touchDropTimer > 500L) {
							this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
							this.onMouseClick(slot, slot.id, 1, SlotActionType.PICKUP);
							this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
							this.touchDropTimer = i + 750L;
							this.touchDragStack.decrement(1);
						}
					} else {
						this.touchHoveredSlot = slot;
						this.touchDropTimer = i;
					}
				}
			}
		} else if (this.cursorDragging && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.cursorDragSlots.size() || this.heldButtonType == 2) && DockContainer.canInsertItemIntoSlot(slot, itemstack, true) && slot.canInsert(itemstack) && this.handler.canInsertIntoSlot(slot)) {
			this.cursorDragSlots.add(slot);
			this.calculateOffset();
		}
		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int state) {
		Slot slot = this.getSlotAt(mouseX, mouseY);

		if (slot != null && state == 0) {
			//  this.selectedButton.mouseReleased(mouseX, mouseY);
			//  this.selectedButton = null;
		}
		InputUtil.Key mouseKey = InputUtil.Type.MOUSE.createFromCode(state);

		Slot slot1 = this.getSlotAtPosition(mouseX, mouseY);
		int i = this.x;
		int j = this.y;
		boolean flag = this.isClickOutsideBounds(mouseX, mouseY, i, j, state);
		if (slot1 != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
		int k = -1;

		if (slot1 != null) {
			k = slot1.id;
		}

		if (flag) {
			k = -999;
		}

		if (this.doubleClicking && slot1 != null && state == 0 && this.handler.canInsertIntoSlot(ItemStack.EMPTY, slot1)) {
			if (Screen.hasShiftDown()) {
				if (!this.quickMovingStack.isEmpty()) {
					for (Slot slot2 : this.handler.slots) {
						if (slot2 != null && slot2.canTakeItems(this.client.player) && slot2.hasStack() && DockContainer.canInsertItemIntoSlot(slot2, this.quickMovingStack, true)) {
							this.onMouseClick(slot2, slot2.id, state, SlotActionType.QUICK_MOVE);
						}
					}
				}
			} else {
				this.onMouseClick(slot1, k, state, SlotActionType.PICKUP_ALL);
			}

			this.doubleClicking = false;
			this.lastButtonClickTime = 0L;
		} else {
			if (this.cursorDragging && this.heldButtonCode != state) {
				this.cursorDragging = false;
				this.cursorDragSlots.clear();
				this.cancelNextRelease = true;
				return true;
			}

			if (this.cancelNextRelease) {
				this.cancelNextRelease = false;
				return true;
			}

			if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
				if (state == 0 || state == 1) {
					if (this.touchDragStack.isEmpty() && slot1 != this.touchDragSlotStart) {
						this.touchDragStack = this.touchDragSlotStart.getStack();
					}

					boolean flag2 = DockContainer.canInsertItemIntoSlot(slot1, this.touchDragStack, false);

					if (k != -1 && !this.touchDragStack.isEmpty() && flag2) {
						this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, state, SlotActionType.PICKUP);
						this.onMouseClick(slot1, k, 0, SlotActionType.PICKUP);

						if (this.client.player.inventory.getCursorStack().isEmpty()) {
							this.touchDropReturningStack = ItemStack.EMPTY;
						} else {
							this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, state, SlotActionType.PICKUP);
							this.touchDropX = (int) (mouseX - i);
							this.touchDropY = (int) (mouseY - j);
							this.touchDropOriginSlot = this.touchDragSlotStart;
							this.touchDropReturningStack = this.touchDragStack;
							this.touchDropTime = System.currentTimeMillis();
						}
					} else if (!this.touchDragStack.isEmpty()) {
						this.touchDropX = (int) (mouseX - i);
						this.touchDropY = (int) (mouseY - j);
						this.touchDropOriginSlot = this.touchDragSlotStart;
						this.touchDropReturningStack = this.touchDragStack;
						this.touchDropTime = System.currentTimeMillis();
					}

					this.touchDragStack = ItemStack.EMPTY;
					this.touchDragSlotStart = null;
				}
			} else if (this.cursorDragging && !this.cursorDragSlots.isEmpty()) {
				this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(0, this.heldButtonType), SlotActionType.QUICK_CRAFT);

				for (Slot slot2 : this.cursorDragSlots) {
					this.onMouseClick(slot2, slot2.id, ScreenHandler.packQuickCraftData(1, this.heldButtonType), SlotActionType.QUICK_CRAFT);
				}

				this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(2, this.heldButtonType), SlotActionType.QUICK_CRAFT);
			} else if (!this.client.player.inventory.getCursorStack().isEmpty()) {
				if (/*this.client.options.keyPickItem.isActiveAndMatches(mouseKey)*/ false) {
					this.onMouseClick(slot1, k, state, SlotActionType.CLONE);
				} else {
					boolean flag1 = k != -999 && Screen.hasShiftDown();

					if (flag1) {
						this.quickMovingStack = slot1 != null && slot1.hasStack() ? slot1.getStack().copy() : ItemStack.EMPTY;
					}

					this.onMouseClick(slot1, k, state, flag1 ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP);
				}
			}
		}

		if (this.client.player.inventory.getCursorStack().isEmpty()) {
			this.lastButtonClickTime = 0L;
		}

		this.cursorDragging = false;
		return true;
	}

	private boolean isMouseOverSlot(Slot slotIn, double mouseX, double mouseY) {
		return this.isPointWithinBounds(slotIn.x, slotIn.y, 16, 16, mouseX, mouseY);
	}

	private Slot getSlotAt(double p_195360_1_, double p_195360_3_) {
		for (int i = 0; i < this.handler.slots.size(); ++i) {
			Slot slot = this.handler.slots.get(i);
			if (this.isMouseOverSlot(slot, p_195360_1_, p_195360_3_) && slot.doDrawHoveringEffect()) {
				return slot;
			}
		}
		return null;
	}
}