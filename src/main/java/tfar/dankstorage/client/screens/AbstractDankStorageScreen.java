package tfar.dankstorage.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import tfar.dankstorage.client.BigItemRenderer;
import tfar.dankstorage.client.button.SmallButton;
import tfar.dankstorage.container.AbstractDankContainer;
import tfar.dankstorage.container.DockContainer;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.server.C2SMessageLockSlot;
import tfar.dankstorage.network.server.C2SMessageSort;
import tfar.dankstorage.utils.Utils;
import org.lwjgl.opengl.GL13;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractDankStorageScreen<T extends AbstractDankContainer> extends AbstractContainerScreen<T> {

	final ResourceLocation background;//= new ResourceLocation("textures/gui/container/shulker_box.png");

	protected final boolean is7;

	public AbstractDankStorageScreen(T container, Inventory playerinventory, Component component, ResourceLocation background) {
		super(container, playerinventory, component);
		this.background = background;
		this.imageHeight = 114 + this.menu.rows * 18;
		this.skipNextRelease = true;
		this.is7 = this.menu.rows > 6;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new SmallButton(leftPos + 143, topPos + 4, 26, 12, new TextComponent("Sort"), b -> {
			C2SMessageSort.send();
		}));
	}

	@Override
	protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

		minecraft.getTextureManager().bind(background);
		if (is7)
			blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 512);
		else
			blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);


		for (int i = 0; i < (menu.rows * 9); i++) {
			int j = i % 9;
			int k = i / 9;
			int offsetx = 8;
			int offsety = 18;
			if (this.menu.propertyDelegate.get(i) == 1)
				fill(stack, leftPos + j * 18 + offsetx, topPos + k * 18 + offsety,
								leftPos + j * 18 + offsetx + 16, topPos + k * 18 + offsety + 16, 0xFFFF0000);
		}
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(stack);
		int i = this.leftPos;
		int j = this.topPos;
		this.renderBg(stack, partialTicks, mouseX, mouseY);
		RenderSystem.disableRescaleNormal();
		Lighting.turnOff();
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
		this.hoveredSlot = null;
		int k = 240;
		int l = 240;
		RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, k, l);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		for (int i1 = 0; i1 < this.menu.slots.size(); ++i1) {
			Slot slot = this.menu.slots.get(i1);

			if (slot.isActive()) {
				this.renderSlot(stack, slot);
			}

			if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.isActive()) {
				this.hoveredSlot = slot;
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

		Lighting.turnOff();
		this.renderLabels(stack, mouseX, mouseY);
		Inventory inventoryplayer = this.minecraft.player.inventory;
		ItemStack itemstack = this.draggingItem.isEmpty() ? inventoryplayer.getCarried() : this.draggingItem;

		if (!itemstack.isEmpty()) {
			int k2 = this.draggingItem.isEmpty() ? 8 : 16;
			String s = null;

			if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
				itemstack = itemstack.copy();
				itemstack.setCount(Mth.ceil((float) itemstack.getCount() / 2.0F));
			} else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
				itemstack = itemstack.copy();
				itemstack.setCount(this.quickCraftingRemainder);

				if (itemstack.isEmpty()) {
					s = "" + ChatFormatting.YELLOW + "0";
				}
			}

			this.renderFloatingItem(itemstack, mouseX - i - 8, mouseY - j - k2, s);
		}

		if (!this.snapbackItem.isEmpty()) {
			float f = (float) (System.currentTimeMillis() - this.snapbackTime) / 100.0F;

			if (f >= 1.0F) {
				f = 1.0F;
				this.snapbackItem = ItemStack.EMPTY;
			}

			int l2 = this.snapbackEnd.x - this.snapbackStartX;
			int i3 = this.snapbackEnd.y - this.snapbackStartY;
			int l1 = this.snapbackStartX + (int) ((float) l2 * f);
			int i2 = this.snapbackStartY + (int) ((float) i3 * f);
			this.renderFloatingItem(this.snapbackItem, l1, i2, null);
		}

		RenderSystem.popMatrix();
		RenderSystem.enableDepthTest();

		this.renderTooltip(stack, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(PoseStack matrices, ItemStack stack, int mouseX, int mouseY) {
		super.renderTooltip(matrices, stack, mouseX, mouseY);
		/*List<Text> tooltip = this.getTooltipFromItem(stack);
		if (focusedSlot != null) {
			appendDankInfo(tooltip,stack);
		}*/
	}

	public void appendDankInfo(List<Component> tooltip,ItemStack stack) {
		if (hoveredSlot.getItem().getItem().is(Utils.BLACKLISTED_STORAGE)) {
			Component component1 = new TranslatableComponent("text.dankstorage.blacklisted_storage").withStyle(ChatFormatting.DARK_RED);
			tooltip.add(component1);
		}
		if (hoveredSlot.getItem().getItem().is(Utils.BLACKLISTED_USAGE)) {
			Component component1 = new TranslatableComponent("text.dankstorage.blacklisted_usage").
							withStyle(ChatFormatting.DARK_RED);
			tooltip.add(component1);
		}
		if (hoveredSlot instanceof DankSlot) {
			Component component2 = new TranslatableComponent("text.dankstorage.lock",
							new TextComponent("ctrl").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);
			tooltip.add(component2);
			if (hoveredSlot.getItem().getCount() >= 1000) {
				Component component3 = new TranslatableComponent(
								"text.dankstorage.exact", new TextComponent(Integer.toString(hoveredSlot.getItem().getCount())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY);
				tooltip.add(component3);
			}
		}
	}

	private void renderFloatingItem(ItemStack stack, int x, int y, String altText) {
		RenderSystem.translatef(0.0F, 0.0F, 32.0F);
		this.setBlitOffset(200);
		this.itemRenderer.blitOffset = 200.0F;
		BigItemRenderer.INSTANCE.blitOffset = this.itemRenderer.blitOffset;
		this.itemRenderer.renderAndDecorateItem(stack, x, y);
		this.itemRenderer.renderGuiItemDecorations(font, stack, x, y - (this.draggingItem.isEmpty() ? 0 : 8), altText);
		this.setBlitOffset(0);
		this.itemRenderer.blitOffset = 0.0F;
		BigItemRenderer.INSTANCE.blitOffset = this.itemRenderer.blitOffset;
	}

	private void renderSlot(PoseStack matrices, Slot slotIn) {
		int i = slotIn.x;
		int j = slotIn.y;
		ItemStack itemstack = slotIn.getItem();
		boolean flag = false;
		boolean flag1 = slotIn == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
		ItemStack itemstack1 = this.minecraft.player.inventory.getCarried();
		String s = null;

		if (slotIn == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty()) {
			itemstack = itemstack.copy();
			itemstack.setCount(itemstack.getCount() / 2);
		} else if (this.isQuickCrafting && this.quickCraftSlots.contains(slotIn) && !itemstack1.isEmpty()) {
			if (this.quickCraftSlots.size() == 1) {
				return;
			}

			if (DockContainer.canItemQuickReplace(slotIn, itemstack1, true) && this.menu.canDragTo(slotIn)) {
				itemstack = itemstack1.copy();
				flag = true;
				AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack, slotIn.getItem().isEmpty() ? 0 : slotIn.getItem().getCount());
				int k = slotIn.getMaxStackSize(itemstack);

				if (itemstack.getCount() > k) {
					s = ChatFormatting.YELLOW.toString() + k;
					itemstack.setCount(k);
				}
			} else {
				this.quickCraftSlots.remove(slotIn);
				this.recalculateQuickCraftRemaining();
			}
		}

		this.setBlitOffset(100);
		this.itemRenderer.blitOffset = 100;

		if (itemstack.isEmpty() && slotIn.isActive()) {
			Pair<ResourceLocation, ResourceLocation> pair = slotIn.getNoItemIcon();

			if (pair != null) {
				TextureAtlasSprite textureatlassprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
				this.minecraft.getTextureManager().bind(textureatlassprite.atlas().location());
				blit(matrices, i, j, this.getBlitOffset(), 16, 16, textureatlassprite);
				flag1 = true;
			}
		}

		if (!flag1) {
			if (flag) {
				fill(matrices, i, j, i + 16, j + 16, 0x80ffffff);
			}

			RenderSystem.enableDepthTest();
			this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemstack, i, j);
			BigItemRenderer.INSTANCE.blitOffset = this.itemRenderer.blitOffset;
			if (slotIn instanceof DankSlot) {
				BigItemRenderer.INSTANCE.renderGuiItemDecorations(this.font, itemstack, i, j, s);
			} else {
				this.itemRenderer.renderGuiItemDecorations(this.font, itemstack, i, j, s);
			}
		}

		this.itemRenderer.blitOffset = 0.0F;
		this.setBlitOffset(0);
		BigItemRenderer.INSTANCE.blitOffset = itemRenderer.blitOffset;
	}

	private void recalculateQuickCraftRemaining() {
		ItemStack itemstack = this.minecraft.player.inventory.getCarried();

		if (!itemstack.isEmpty() && this.isQuickCrafting) {
			if (this.quickCraftingType == 2) {
				this.quickCraftingRemainder = itemstack.getMaxStackSize();
			} else {
				this.quickCraftingRemainder = itemstack.getCount();

				for (Slot slot : this.quickCraftSlots) {
					ItemStack itemstack1 = itemstack.copy();
					ItemStack itemstack2 = slot.getItem();
					int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
					AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack1, i);
					//int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));
					int j = slot.getMaxStackSize(itemstack1);

					if (itemstack1.getCount() > j) {
						itemstack1.setCount(j);
					}

					this.quickCraftingRemainder -= itemstack1.getCount() - i;
				}
			}
		}
	}

	private Slot getSlotAtPosition(double x, double y) {
		for (int i = 0; i < this.menu.slots.size(); ++i) {
			Slot slot = this.menu.slots.get(i);

			if (this.isMouseOverSlot(slot, (int) x, (int) y) && slot.isActive()) {
				return slot;
			}
		}

		return null;
	}

	public boolean mouseclicked(double mouseX, double mouseY, int mouseButton) {
		Iterator<GuiEventListener> var6 = this.children.iterator();

		GuiEventListener iGuiEventListener;
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
				C2SMessageLockSlot.send(slot.index);
				return true;
			}
		}

		InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(mouseButton);
		boolean isPickBlock = false;//this.client.options.keyPickItem.isActiveAndMatches(mouseKey);
		Slot slot = this.getSlotAtPosition(mouseX, mouseY);
		long i = System.currentTimeMillis();
		this.doubleclick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
		this.skipNextRelease = false;

		if (mouseButton == 0 || mouseButton == 1 || isPickBlock) {
			int j = this.leftPos;
			int k = this.topPos;
			boolean clickedOutsideGui = this.hasClickedOutside(mouseX, mouseY, j, k, mouseButton);
			if (slot != null)
				clickedOutsideGui = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
			int l = -1;

			if (slot != null) {
				l = slot.index;
			}

			if (clickedOutsideGui) {
				l = -999;
			}

			if (this.minecraft.options.touchscreen && clickedOutsideGui && this.minecraft.player.inventory.getCarried().isEmpty()) {
				this.minecraft.setScreen(null);
				return false;
			}

			if (l != -1) {
				if (this.minecraft.options.touchscreen) {
					if (slot != null && slot.hasItem()) {
						this.clickedSlot = slot;
						this.draggingItem = ItemStack.EMPTY;
						this.isSplittingStack = mouseButton == 1;
					} else {
						this.clickedSlot = null;
					}
				} else if (!this.isQuickCrafting) {
					if (this.minecraft.player.inventory.getCarried().isEmpty()) {
						if (/*this.client.options.keyPickItem.isActiveAndMatches(mouseKey)*/false) {
							this.slotClicked(slot, l, mouseButton, ClickType.CLONE);
						} else {
							boolean flag2 = l != -999 && Screen.hasShiftDown();
							ClickType clicktype = ClickType.PICKUP;

							if (flag2) {
								this.lastQuickMoved = slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
								clicktype = ClickType.QUICK_MOVE;
							} else if (l == -999) {
								clicktype = ClickType.THROW;
							}

							this.slotClicked(slot, l, mouseButton, clicktype);
						}

						this.skipNextRelease = true;
					} else {
						this.isQuickCrafting = true;
						this.quickCraftingButton = mouseButton;
						this.quickCraftSlots.clear();

						if (mouseButton == 0) {
							this.quickCraftingType = 0;
						} else if (mouseButton == 1) {
							this.quickCraftingType = 1;
						} else if (/*this.client.options.keyPickItem.isActiveAndMatches(mouseKey)*/false) {
							this.quickCraftingType = 2;
						}
					}
				}
			}
		}

		this.lastClickSlot = slot;
		this.lastClickTime = i;
		this.lastClickButton = mouseButton;
		return true;
	}


	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double timeSinceLastClick, double param1) {
		Slot slot = this.getSlotAtPosition(mouseX, mouseY);
		ItemStack itemstack = this.minecraft.player.inventory.getCarried();

		if (this.clickedSlot != null && this.minecraft.options.touchscreen) {
			if (clickedMouseButton == 0 || clickedMouseButton == 1) {
				if (this.draggingItem.isEmpty()) {
					if (slot != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
						this.draggingItem = this.clickedSlot.getItem().copy();
					}
				} else if (this.draggingItem.getCount() > 1 && slot != null && DockContainer.canItemQuickReplace(slot, this.draggingItem, false)) {
					long i = System.currentTimeMillis();

					if (this.quickdropSlot == slot) {
						if (i - this.quickdropTime > 500L) {
							this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
							this.slotClicked(slot, slot.index, 1, ClickType.PICKUP);
							this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
							this.quickdropTime = i + 750L;
							this.draggingItem.shrink(1);
						}
					} else {
						this.quickdropSlot = slot;
						this.quickdropTime = i;
					}
				}
			}
		} else if (this.isQuickCrafting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && DockContainer.canItemQuickReplace(slot, itemstack, true) && slot.mayPlace(itemstack) && this.menu.canDragTo(slot)) {
			this.quickCraftSlots.add(slot);
			this.recalculateQuickCraftRemaining();
		}
		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int state) {
		Slot slot = this.findSlot(mouseX, mouseY);

		if (slot != null && state == 0) {
			//  this.selectedButton.mouseReleased(mouseX, mouseY);
			//  this.selectedButton = null;
		}
		InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(state);

		Slot slot1 = this.getSlotAtPosition(mouseX, mouseY);
		int i = this.leftPos;
		int j = this.topPos;
		boolean flag = this.hasClickedOutside(mouseX, mouseY, i, j, state);
		if (slot1 != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
		int k = -1;

		if (slot1 != null) {
			k = slot1.index;
		}

		if (flag) {
			k = -999;
		}

		if (this.doubleclick && slot1 != null && state == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, slot1)) {
			if (Screen.hasShiftDown()) {
				if (!this.lastQuickMoved.isEmpty()) {
					for (Slot slot2 : this.menu.slots) {
						if (slot2 != null && slot2.mayPickup(this.minecraft.player) && slot2.hasItem() && DockContainer.canItemQuickReplace(slot2, this.lastQuickMoved, true)) {
							this.slotClicked(slot2, slot2.index, state, ClickType.QUICK_MOVE);
						}
					}
				}
			} else {
				this.slotClicked(slot1, k, state, ClickType.PICKUP_ALL);
			}

			this.doubleclick = false;
			this.lastClickTime = 0L;
		} else {
			if (this.isQuickCrafting && this.quickCraftingButton != state) {
				this.isQuickCrafting = false;
				this.quickCraftSlots.clear();
				this.skipNextRelease = true;
				return true;
			}

			if (this.skipNextRelease) {
				this.skipNextRelease = false;
				return true;
			}

			if (this.clickedSlot != null && this.minecraft.options.touchscreen) {
				if (state == 0 || state == 1) {
					if (this.draggingItem.isEmpty() && slot1 != this.clickedSlot) {
						this.draggingItem = this.clickedSlot.getItem();
					}

					boolean flag2 = DockContainer.canItemQuickReplace(slot1, this.draggingItem, false);

					if (k != -1 && !this.draggingItem.isEmpty() && flag2) {
						this.slotClicked(this.clickedSlot, this.clickedSlot.index, state, ClickType.PICKUP);
						this.slotClicked(slot1, k, 0, ClickType.PICKUP);

						if (this.minecraft.player.inventory.getCarried().isEmpty()) {
							this.snapbackItem = ItemStack.EMPTY;
						} else {
							this.slotClicked(this.clickedSlot, this.clickedSlot.index, state, ClickType.PICKUP);
							this.snapbackStartX = (int) (mouseX - i);
							this.snapbackStartY = (int) (mouseY - j);
							this.snapbackEnd = this.clickedSlot;
							this.snapbackItem = this.draggingItem;
							this.snapbackTime = System.currentTimeMillis();
						}
					} else if (!this.draggingItem.isEmpty()) {
						this.snapbackStartX = (int) (mouseX - i);
						this.snapbackStartY = (int) (mouseY - j);
						this.snapbackEnd = this.clickedSlot;
						this.snapbackItem = this.draggingItem;
						this.snapbackTime = System.currentTimeMillis();
					}

					this.draggingItem = ItemStack.EMPTY;
					this.clickedSlot = null;
				}
			} else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
				this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);

				for (Slot slot2 : this.quickCraftSlots) {
					this.slotClicked(slot2, slot2.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
				}

				this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
			} else if (!this.minecraft.player.inventory.getCarried().isEmpty()) {
				if (/*this.client.options.keyPickItem.isActiveAndMatches(mouseKey)*/ false) {
					this.slotClicked(slot1, k, state, ClickType.CLONE);
				} else {
					boolean flag1 = k != -999 && Screen.hasShiftDown();

					if (flag1) {
						this.lastQuickMoved = slot1 != null && slot1.hasItem() ? slot1.getItem().copy() : ItemStack.EMPTY;
					}

					this.slotClicked(slot1, k, state, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
				}
			}
		}

		if (this.minecraft.player.inventory.getCarried().isEmpty()) {
			this.lastClickTime = 0L;
		}

		this.isQuickCrafting = false;
		return true;
	}

	private boolean isMouseOverSlot(Slot slotIn, double mouseX, double mouseY) {
		return this.isHovering(slotIn.x, slotIn.y, 16, 16, mouseX, mouseY);
	}

	private Slot findSlot(double p_195360_1_, double p_195360_3_) {
		for (int i = 0; i < this.menu.slots.size(); ++i) {
			Slot slot = this.menu.slots.get(i);
			if (this.isMouseOverSlot(slot, p_195360_1_, p_195360_3_) && slot.isActive()) {
				return slot;
			}
		}
		return null;
	}
}