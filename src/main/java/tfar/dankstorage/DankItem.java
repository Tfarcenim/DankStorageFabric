package tfar.dankstorage;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.damage.DamageSource;
import tfar.dankstorage.client.Client;
import tfar.dankstorage.container.PortableDankProvider;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.PortableDankInventory;
import tfar.dankstorage.mixin.ItemUsageContextAccessor;
import tfar.dankstorage.network.server.C2SMessageToggleUseType;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Mode;
import tfar.dankstorage.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DankItem extends Item {
  public final DankStats stats;

  public DankItem(Settings p_i48527_2_, DankStats stats) {
    super( p_i48527_2_);
    this.stats = stats;
  }

  /*public static final Rarity GRAY = Rarity.create("dark_gray", Formatting.GRAY);
  public static final Rarity RED = Rarity.create("red", Formatting.RED);
  public static final Rarity GOLD = Rarity.create("gold", Formatting.GOLD);
  public static final Rarity GREEN = Rarity.create("green", Formatting.GREEN);
  public static final Rarity BLUE = Rarity.create("blue", Formatting.AQUA);
  public static final Rarity PURPLE = Rarity.create("purple", Formatting.DARK_PURPLE);
  public static final Rarity WHITE = Rarity.create("white", Formatting.WHITE);

  @Nonnull
  @Override
  public Rarity getRarity(ItemStack stack) {
    switch (tier) {
      case 1:
        return GRAY;
      case 2:
        return RED;
      case 3:
        return GOLD;
      case 4:
        return GREEN;
      case 5:
        return BLUE;
      case 6:
        return PURPLE;
      case 7:
        return WHITE;
    }
    return super.getRarity(stack);
  }*/

  @Override
  public int getMaxUseTime(ItemStack bag) {
    if (!Utils.isConstruction(bag))return 0;
    ItemStack stack = Utils.getItemStackInSelectedSlot(bag);
    return stack.getItem().getMaxUseTime(stack);
  }

  @Override
  public float getMiningSpeedMultiplier(ItemStack bag, BlockState p_150893_2_) {
    if (!Utils.isConstruction(bag))return 1;
    ItemStack tool = Utils.getItemStackInSelectedSlot(bag);
    return tool.getItem().getMiningSpeedMultiplier(tool, p_150893_2_);
  }

  //this is used to damage tools and stuff, we use it here to damage the internal item instead
  @Override
  public boolean postMine(ItemStack s, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
    if (!Utils.isConstruction(s))return super.postMine(s, p_179218_2_, p_179218_3_, p_179218_4_, p_179218_5_);

    ItemStack tool = Utils.getItemStackInSelectedSlot(s);

    return tool.getItem().postMine(tool, p_179218_2_, p_179218_3_, p_179218_4_, p_179218_5_);
  }

  @Nonnull
  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    ItemStack bag = player.getStackInHand(hand);
    if (!world.isClient){

      if (Utils.getUseType(bag) == C2SMessageToggleUseType.UseType.bag) {
        DankStats type = Utils.getStats(player.getStackInHand(hand));
        player.openHandledScreen(new PortableDankProvider(type));
        return super.use(world,player,hand);
      } else {
        ItemStack toPlace = Utils.getItemStackInSelectedSlot(bag);
        EquipmentSlot hand1 = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        //handle empty
        if (toPlace.isEmpty()){
          return TypedActionResult.pass(bag);
        }

        //handle food
        if (toPlace.getItem().isFood()) {
          if (player.canConsume(false)) {
            player.setCurrentHand(hand);
            return TypedActionResult.consume(bag);
          }
        }
        //handle potion
        else if (toPlace.getItem() instanceof PotionItem){
          player.setCurrentHand(hand);
          return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
        }
        //todo support other items?
        else {
          ItemStack newBag = bag.copy();
          player.equipStack(hand1, toPlace);
          TypedActionResult<ItemStack> actionResult = toPlace.getItem().use(world, player, hand);
          PortableDankInventory handler = Utils.getHandler(newBag);
          handler.setStack(Utils.getSelectedSlot(newBag), actionResult.getValue());
          player.equipStack(hand1, newBag);
        }
        }
      }
    return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
  }

  @Override
  public ActionResult useOnEntity(ItemStack bag, PlayerEntity player, LivingEntity entity, Hand hand) {
    if (!Utils.isConstruction(bag))return ActionResult.FAIL;
    PortableDankInventory handler = Utils.getHandler(bag);
    ItemStack toPlace = handler.getStack(Utils.getSelectedSlot(bag));
    EquipmentSlot hand1 = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    player.equipStack(hand1, toPlace);
    ActionResult result = toPlace.getItem().useOnEntity(toPlace, player, entity, hand);
    handler.setStack(Utils.getSelectedSlot(bag),toPlace);
    player.equipStack(hand1, bag);
    return result;
  }

  @Override
  public boolean hasGlint(ItemStack stack) {
    return stack.hasTag() && Utils.getMode(stack) != Mode.NORMAL;
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void appendTooltip(ItemStack bag, @Nullable World world, List<Text> tooltip, TooltipContext flag) {
    if (Utils.DEV && bag.hasTag()) {
      String s = bag.getTag().toString();

      List<String> bits = new ArrayList<>();

      int length = s.length();

      int itr = (int) Math.ceil(length / 40d);

      for (int i = 0; i < itr;i++) {

        int end = (i+1) * 40;

        if ((i+1) * 40 - 1 >= length) {
          end = length;
        }

        String s1 = s.substring(i * 40,end);
        bits.add(s1);
      }

      bits.forEach(s1 -> tooltip.add(new LiteralText(s1)));

    }


    if (!Screen.hasShiftDown()){
      tooltip.add(new TranslatableText("text.dankstorage.shift",
              new LiteralText("Shift").formatted(Formatting.YELLOW)).formatted(Formatting.GRAY));
    }

    if (Screen.hasShiftDown()) {
      tooltip.add(new TranslatableText("text.dankstorage.changemode",new LiteralText(Client.CONSTRUCTION.getBoundKeyTranslationKey()).formatted(Formatting.YELLOW)).formatted(Formatting.GRAY));
      C2SMessageToggleUseType.UseType mode = Utils.getUseType(bag);
      tooltip.add(
              new TranslatableText("text.dankstorage.currentusetype",new TranslatableText(
                      "dankstorage.usetype."+mode.name().toLowerCase(Locale.ROOT)).formatted(Formatting.YELLOW)).formatted(Formatting.GRAY));
      tooltip.add(
              new TranslatableText("text.dankstorage.stacklimit",new LiteralText(stats.stacklimit+"").formatted(Formatting.GREEN)).formatted(Formatting.GRAY));

      DankInventory handler = Utils.getHandler(bag);

      if (handler.isEmpty()){
        tooltip.add(
                new TranslatableText("text.dankstorage.empty").formatted(Formatting.ITALIC));
        return;
      }
      int count1 = 0;
      for (int i = 0; i < handler.size(); i++) {
        if (count1 > 10)break;
        ItemStack item = handler.getStack(i);
        if (item.isEmpty())continue;
        Text count = new LiteralText(Integer.toString(item.getCount())).formatted(Formatting.AQUA);
        //tooltip.add(new TranslationTextComponent("text.dankstorage.formatcontaineditems", count, item.getDisplayName().(item.getRarity().color)));
        count1++;
      }
    }
  }

  @Nonnull
  @Override
  public UseAction getUseAction(ItemStack stack) {
    if (!Utils.isConstruction(stack))return UseAction.NONE;
    ItemStack internal = Utils.getItemStackInSelectedSlot(stack);
    return internal.getItem().getUseAction(stack);
  }

  //called for stuff like food and potions
  @Nonnull
  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
    if (!Utils.isConstruction(stack))return stack;

    ItemStack internal = Utils.getItemStackInSelectedSlot(stack);

    if (internal.getItem().isFood()){
     ItemStack food = entity.eatFood(world, internal);
     PortableDankInventory handler = Utils.getHandler(stack);
     handler.setStack(Utils.getSelectedSlot(stack), food);
     return stack;
    }

    if (internal.getItem() instanceof PotionItem){
      ItemStack potion = internal.finishUsing(world,entity);
      PortableDankInventory handler = Utils.getHandler(stack);
      handler.setStack(Utils.getSelectedSlot(stack), potion);
      return stack;
    }

    return super.finishUsing(stack, world, entity);
  }

  public int getGlintColor(ItemStack stack){
    Mode mode = Utils.getMode(stack);
    switch (mode){
      case NORMAL:default:return 0xffffffff;
      case PICKUP_ALL:return 0xff00ff00;
      case FILTERED_PICKUP:return 0xffffff00;
      case VOID_PICKUP:return 0xffff0000;
    }
  }

  @Nonnull
  @Override
  public ActionResult useOnBlock(ItemUsageContext ctx) {
    ItemStack bag = ctx.getStack();
    C2SMessageToggleUseType.UseType useType = Utils.getUseType(bag);

    if(useType == C2SMessageToggleUseType.UseType.bag) {
      return ActionResult.PASS;
    }

    PortableDankInventory handler = Utils.getHandler(bag);
    int selectedSlot = Utils.getSelectedSlot(bag);

    ItemStack toPlace = handler.getStack(selectedSlot).copy();
    if (toPlace.getCount() == 1 && handler.isLocked(selectedSlot))
      return ActionResult.PASS;

    ItemUsageContext ctx2 = new ItemUseContextExt(ctx.getWorld(),ctx.getPlayer(),ctx.getHand(),toPlace,((ItemUsageContextAccessor)ctx).getHit());
    ActionResult actionResultType = toPlace.getItem().useOnBlock(ctx2);//ctx2.getItem().onItemUse(ctx);
    handler.setStack(selectedSlot, ctx2.getStack());
    return actionResultType;
  }

  @Override
  public boolean damage(DamageSource source) {
    return source == DamageSource.OUT_OF_WORLD;
  }

  public static class ItemUseContextExt extends ItemUsageContext {
    protected ItemUseContextExt(World p_i50034_1_, @Nullable PlayerEntity p_i50034_2_, Hand p_i50034_3_, ItemStack p_i50034_4_, BlockHitResult p_i50034_5_) {
      super(p_i50034_1_, p_i50034_2_, p_i50034_3_, p_i50034_4_, p_i50034_5_);
    }
  }
}
