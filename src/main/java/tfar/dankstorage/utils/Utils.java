package tfar.dankstorage.utils;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.world.ClientData;
import tfar.dankstorage.world.DankInventory;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.server.C2SMessageToggleUseType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static tfar.dankstorage.network.server.C2SMessageToggleUseType.useTypes;

public class Utils {

    public static final Tag<Item> BLACKLISTED_STORAGE = TagRegistry.item(new ResourceLocation(DankStorage.MODID, "blacklisted_storage"));
    public static final Tag<Item> BLACKLISTED_USAGE = TagRegistry.item(new ResourceLocation(DankStorage.MODID, "blacklisted_usage"));

    public static final Tag<Item> WRENCHES = TagRegistry.item(new ResourceLocation("forge", "wrenches"));

    public static final String ID = "dankstorage:id";
    public static final Set<ResourceLocation> taglist = new HashSet<>();
    public static boolean DEV = FabricLoader.getInstance().isDevelopmentEnvironment();


    public static PickupMode getPickupMode(ItemStack bag) {
        return PickupMode.PICKUP_MODES[bag.getOrCreateTag().getInt("mode")];
    }

    public static boolean isConstruction(ItemStack bag) {
        return bag.getItem() instanceof DankItem && bag.hasTag()
                && bag.getTag().contains("construction")
                && bag.getTag().getInt("construction") == C2SMessageToggleUseType.UseType.construction.ordinal();
    }

    public static DankStats getStatsfromRows(int rows) {
        switch (rows) {
            case 1:
                return DankStats.one;
            case 2:
                return DankStats.two;
            case 3:
                return DankStats.three;
            case 4:
                return DankStats.four;
            case 5:
                return DankStats.five;
            case 6:
                return DankStats.six;
            case 9:
                return DankStats.seven;
        }
        throw new IllegalStateException(String.valueOf(rows));
    }

    //0,1,2,3
    public static void cycleMode(ItemStack bag, Player player) {
        int ordinal = bag.getOrCreateTag().getInt("mode");
        ordinal++;
        if (ordinal > PickupMode.PICKUP_MODES.length - 1) ordinal = 0;
        bag.getOrCreateTag().putInt("mode", ordinal);
        player.displayClientMessage(
                new TranslatableComponent("dankstorage.mode." + PickupMode.PICKUP_MODES[ordinal].name()), true);
    }

    public static C2SMessageToggleUseType.UseType getUseType(ItemStack bag) {
        return useTypes[bag.getOrCreateTag().getInt("construction")];
    }

    //0,1,2
    public static void cyclePlacement(ItemStack bag, Player player) {
        int ordinal = bag.getOrCreateTag().getInt("construction");
        ordinal++;
        if (ordinal >= useTypes.length) ordinal = 0;
        bag.getOrCreateTag().putInt("construction", ordinal);
        player.displayClientMessage(
                new TranslatableComponent("dankstorage.usetype." + useTypes[ordinal].name()), true);
    }

    public static int getSelectedSlot(ItemStack bag) {
        return bag.getTag() != null ? bag.getTag().getInt("selectedSlot") : 0;
    }

    public static void setSelectedSlot(int slot,ItemStack bag) {
        bag.getOrCreateTag().putInt("selectedSlot",slot);
    }

    public static void sort(Player player) {
        if (player == null) return;
        AbstractContainerMenu openContainer = player.containerMenu;
        if (openContainer instanceof AbstractDankMenu) {
            DankInventory handler = ((AbstractDankMenu) openContainer).dankInventory;

            List<ItemStack> stacks = new ArrayList<>();

            for (ItemStack stack : handler.getContents()) {
                if (!stack.isEmpty()) {
                    merge(stacks, stack.copy(), handler.dankStats.stacklimit);
                }
            }

            List<ItemStackWrapper> wrappers = wrap(stacks);

            Collections.sort(wrappers);

            handler.clearContent();
            for (int i = 0; i < wrappers.size(); i++) {
                ItemStack stack = wrappers.get(i).stack;
                handler.setItem(i, stack);
            }
        }
    }

    public static ItemStack getSelectedItem(ItemStack bag,Level level) {
        int id = bag.getTag().getInt(ID);
        int selected = getSelectedSlot(bag);
        if (!level.isClientSide) {
            DankInventory dankInventory = getInventory(bag, level);
            return dankInventory.getItem(selected);
        }
        return ClientData.map.get(id).selectedItem;
    }

    public static void merge(List<ItemStack> stacks, ItemStack toMerge, int limit) {
        for (ItemStack stack : stacks) {
            if (ItemHandlerHelper.canItemStacksStack(stack, toMerge)) {
                int grow = Math.min(limit - stack.getCount(), toMerge.getCount());
                if (grow > 0) {
                    stack.grow(grow);
                    toMerge.shrink(grow);
                }
            }
        }

        if (!toMerge.isEmpty()) {
            stacks.add(toMerge);
        }
    }

    public static List<ItemStackWrapper> wrap(List<ItemStack> stacks) {
        return stacks.stream().map(ItemStackWrapper::new).collect(Collectors.toList());
    }

    public static DankStats getStats(ItemStack bag) {
        return ((DankItem) bag.getItem()).stats;
    }

    public static void changeSelectedSlot(ItemStack bag, boolean right, ServerPlayer player) {
        DankInventory handler = getInventory(bag,player.getLevel());
        //don't change slot if empty
        if (handler.noValidSlots()) return;
        int selectedSlot = getSelectedSlot(bag);
        int size = handler.getContainerSize();
        //keep iterating until a valid slot is found (not empty and not blacklisted from usage)
        if (right) {
            selectedSlot++;
            if (selectedSlot >= size) selectedSlot = 0;
        } else {
            selectedSlot--;
            if (selectedSlot < 0) selectedSlot = size - 1;
        }
        ItemStack selected = handler.getItem(selectedSlot);

        while (selected.isEmpty() || selected.is(BLACKLISTED_USAGE)) {
            if (right) {
                selectedSlot++;
                if (selectedSlot >= size) selectedSlot = 0;
            } else {
                selectedSlot--;
                if (selectedSlot < 0) selectedSlot = size - 1;
            }
            selected = handler.getItem(selectedSlot);
        }
        if (selectedSlot != -1) {
            setSelectedSlot(selectedSlot,bag);
            DankPacketHandler.sendSelectedItem(player,getID(bag),selected,getUseType(bag));
        }
    }

    public static int getID(ItemStack bag) {
        CompoundTag tag = bag.getTag();
        if (bag.hasTag()) {
            return tag.getInt(Utils.ID);
        }
        return -1;
    }

    public static boolean oredict(ItemStack bag) {
        return bag.getItem() instanceof DankItem && bag.hasTag() && bag.getTag().getBoolean("tag");
    }

    public static DankInventory getInventory(ItemStack bag, Level level) {
        if (!level.isClientSide) {
            CompoundTag tag = bag.getTag();
            int id = tag.getInt(Utils.ID);
            return DankStorage.instance.data.getOrCreateInventory(id,getStats(bag));
        }
        throw new RuntimeException("Attempted to get inventory on client");
    }

    public static int getNbtSize(ItemStack stack) {
        return getNbtSize(stack.getTag());
    }

    public static DankItem getItemFromTier(int tier) {
        return (DankItem) Registry.ITEM.get(new ResourceLocation(DankStorage.MODID, "dank_" + tier));
    }

    public static int getNbtSize(@Nullable CompoundTag nbt) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeNbt(nbt);
        buffer.release();
        return buffer.writerIndex();
    }

    public static ItemStack getItemStackInSelectedSlot(ItemStack bag,ServerLevel level) {
        DankInventory inv = getInventory(bag,level);
        ItemStack stack = inv.getItem(Utils.getSelectedSlot(bag));
        return stack.is(BLACKLISTED_USAGE) ? ItemStack.EMPTY : stack;
    }

    public static boolean areItemStacksConvertible(final ItemStack stack1, final ItemStack stack2) {
        if (stack1.hasTag() || stack2.hasTag()) return false;
        Collection<ResourceLocation> taglistofstack1 = getTags(stack1.getItem());
        Collection<ResourceLocation> taglistofstack2 = getTags(stack2.getItem());

        Set<ResourceLocation> commontags = new HashSet<>(taglistofstack1);
        commontags.retainAll(taglistofstack2);
        commontags.retainAll(taglist);
        return !commontags.isEmpty();
    }

    public static Collection<ResourceLocation> getTags(Item item) {
        return getTagsFor(ItemTags.getAllTags(), item);
    }

    /**
     * can't use TagGroup#getTagsFor because it's client only
     */
    public static Collection<ResourceLocation> getTagsFor(TagCollection<Item> tagGroup, Item item) {
        return tagGroup.getAllTags().entrySet().stream()
                .filter(identifierTagEntry -> identifierTagEntry.getValue().contains(item))
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public static boolean isHoldingDank(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof DankItem) return true;
        stack = player.getOffhandItem();
        return stack.getItem() instanceof DankItem;
    }

    public static boolean canMerge(ItemStack first, ItemStack second, Container inventory) {
        if (first.getItem() != second.getItem()) {
            return false;
        } else if (first.getDamageValue() != second.getDamageValue()) {
            return false;
        } else if (first.getCount() > inventory.getMaxStackSize()) {
            return false;
        } else {
            return ItemStack.tagMatches(first, second);
        }
    }

    @Nullable
    public static InteractionHand getHandWithDank(Player player) {
        if (player.getMainHandItem().getItem() instanceof DankItem) return InteractionHand.MAIN_HAND;
        else if (player.getOffhandItem().getItem() instanceof DankItem) return InteractionHand.OFF_HAND;
        return null;
    }
}
