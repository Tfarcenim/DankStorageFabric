package tfar.dankstorage.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.container.DankMenu;
import tfar.dankstorage.ducks.UseDankStorage;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.PortableDankInventory;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.ItemHandlerHelper;
import tfar.dankstorage.utils.Mode;
import tfar.dankstorage.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class MixinHooks {
    public static <T extends LivingEntity> void actuallyBreakItem(int p_222118_1_, T livingEntity, Consumer<T> p_222118_3_, CallbackInfo ci) {
        ItemStack actualStack = livingEntity.getMainHandItem();
        if (actualStack.getItem() instanceof DankItem && Utils.isConstruction(actualStack)) {
            Utils.getHandler(actualStack).removeItem(Utils.getSelectedSlot(actualStack), 1);
        }
    }

    /**
     * @param inv      Player Inventory to add the item to
     * @param incoming the itemstack being picked up
     * @return if the item was completely picked up by the dank(s)
     */
    public static boolean interceptItem(Inventory inv, ItemStack incoming) {
        Player player = inv.player;
        if (player.containerMenu instanceof DankMenu) {
            return false;
        }
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack possibleDank = inv.getItem(i);
            if (possibleDank.getItem() instanceof DankItem && onItemPickup(player, incoming, possibleDank)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack myFindAmmo(Player player, ItemStack bow) {
        Predicate<ItemStack> predicate = ((ProjectileWeaponItem) bow.getItem()).getAllSupportedProjectiles();

        ItemStack dank = getDankStorage(player);
        if (!dank.isEmpty())
            return Utils.getHandler(dank).getContents().stream()
                    .filter(predicate).findFirst().orElse(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    private static ItemStack getDankStorage(Player player) {
        return IntStream.range(0, player.inventory.getContainerSize()).mapToObj(player.inventory::getItem)
                .filter(stack -> stack.getItem() instanceof DankItem).findFirst()
                .orElse(ItemStack.EMPTY);
    }

    public static void onStoppedUsing(ItemStack bow, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player && !worldIn.isClientSide) {
            Player player = (Player) entityLiving;
            Predicate<ItemStack> predicate = ((ProjectileWeaponItem) bow.getItem()).getAllSupportedProjectiles();
            if (((UseDankStorage) player).useDankStorage() && !player.abilities.instabuild) {
                ItemStack dank = getDankStorage(player);
                DankInventory dankInventory = Utils.getHandler(dank);
                for (int i = 0; i < dankInventory.getContainerSize(); i++) {
                    ItemStack stack = dankInventory.getItem(i);
                    if (predicate.test(stack)) {
                        dankInventory.removeItem(i, 1);
                        break;
                    }
                }
            }
        }
    }

    public static boolean onItemPickup(Player player, ItemStack pickup, ItemStack dank) {

        Mode mode = Utils.getMode(dank);
        if (mode == Mode.normal) return false;
        PortableDankInventory inv = Utils.getHandler(dank);
        int count = pickup.getCount();
        boolean oredict = Utils.oredict(dank);
        List<ItemStack> existing = new ArrayList<>();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) {

            } else {
                boolean exists = false;
                for (ItemStack stack1 : existing) {
                    if (areItemStacksCompatible(stack, stack1, oredict)) {
                        exists = true;
                    }
                }
                if (!exists) {
                    existing.add(stack.copy());
                }
            }
        }

        switch (mode) {
            case pickup_all: {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    allPickup(inv, i, pickup, oredict);
                    if (pickup.isEmpty()) break;
                }
            }
            break;

            case filtered_pickup: {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    filteredPickup(inv, i, pickup, oredict, existing);
                    if (pickup.isEmpty()) break;
                }
            }
            break;

            case void_pickup: {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    voidPickup(inv, i, pickup, oredict, existing);
                    if (pickup.isEmpty()) break;
                }
            }
            break;
        }

        //leftovers
        if (pickup.getCount() != count) {
            dank.setPopTime(5);
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        return pickup.isEmpty();
    }

    public static void voidPickup(PortableDankInventory inv, int slot, ItemStack toInsert, boolean oredict, List<ItemStack> filter) {
        ItemStack existing = inv.getItem(slot);

        if (doesItemStackExist(toInsert, filter, oredict) && areItemStacksCompatible(existing, toInsert, oredict)) {
            int stackLimit = inv.dankStats.stacklimit;
            int total = Math.min(toInsert.getCount() + existing.getCount(), stackLimit);
            //doesn't matter if it overflows cause it's all gone lmao
            inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
            toInsert.setCount(0);
        }
    }

    public static void allPickup(PortableDankInventory inv, int slot, ItemStack pickup, boolean oredict) {
        ItemStack existing = inv.getItem(slot);

        if (existing.isEmpty()) {
            int stackLimit = inv.dankStats.stacklimit;
            int total = pickup.getCount();
            int remainder = total - stackLimit;
            //no overflow
            if (remainder <= 0) {
                inv.setItem(slot, pickup.copy());
                pickup.setCount(0);
            } else {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(pickup, stackLimit));
                pickup.setCount(remainder);
            }
            return;
        }

        if (ItemHandlerHelper.canItemStacksStack(pickup, existing) || (oredict && Utils.areItemStacksConvertible(pickup, existing))) {
            int stackLimit = inv.dankStats.stacklimit;
            int total = pickup.getCount() + existing.getCount();
            int remainder = total - stackLimit;
            //no overflow
            if (remainder <= 0) {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
                pickup.setCount(0);
            } else {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(pickup, stackLimit));
                pickup.setCount(remainder);
            }
        }
    }

    public static void filteredPickup(PortableDankInventory inv, int slot, ItemStack toInsert, boolean oredict, List<ItemStack> filter) {
        ItemStack existing = inv.getItem(slot);

        if (existing.isEmpty() && doesItemStackExist(toInsert, filter, oredict)) {
            int stackLimit = inv.dankStats.stacklimit;
            int total = toInsert.getCount();
            int remainder = total - stackLimit;
            //no overflow
            if (remainder <= 0) {
                inv.setItem(slot, toInsert.copy());
                toInsert.setCount(0);
            } else {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
                toInsert.setCount(remainder);
            }
            return;
        }

        if (doesItemStackExist(toInsert, filter, oredict) && areItemStacksCompatible(existing, toInsert, oredict)) {
            int stackLimit = inv.dankStats.stacklimit;
            int total = toInsert.getCount() + existing.getCount();
            int remainder = total - stackLimit;
            //no overflow
            if (remainder <= 0) {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
                toInsert.setCount(0);
            } else {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
                toInsert.setCount(remainder);
            }
        }
    }

    public static boolean areItemStacksCompatible(ItemStack stackA, ItemStack stackB, boolean oredict) {
        return oredict ? ItemStack.tagMatches(stackA, stackB) && ItemStack.isSame(stackA, stackB) || Utils.areItemStacksConvertible(stackA, stackB) :
                ItemStack.tagMatches(stackA, stackB) && ItemStack.isSame(stackA, stackB);
    }

    public static boolean doesItemStackExist(ItemStack stack, List<ItemStack> filter, boolean oredict) {
        for (ItemStack filterStack : filter) {
            if (areItemStacksCompatible(stack, filterStack, oredict)) return true;
        }
        return false;
    }
}
