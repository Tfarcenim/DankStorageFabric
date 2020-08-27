package tfar.dankstorage;

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
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DankItem extends Item {
  public final DankStats stats;

  public DankItem(Properties p_i48527_2_, DankStats stats) {
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
  public int getUseDuration(ItemStack bag) {
    if (!Utils.isConstruction(bag))return 0;
    ItemStack stack = Utils.getItemStackInSelectedSlot(bag);
    return stack.getItem().getUseDuration(stack);
  }

  @Override
  public float getDestroySpeed(ItemStack bag, BlockState p_150893_2_) {
    if (!Utils.isConstruction(bag))return 1;
    ItemStack tool = Utils.getItemStackInSelectedSlot(bag);
    return tool.getItem().getDestroySpeed(tool, p_150893_2_);
  }

  //this is used to damage tools and stuff, we use it here to damage the internal item instead
  @Override
  public boolean mineBlock(ItemStack s, Level p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
    if (!Utils.isConstruction(s))return super.mineBlock(s, p_179218_2_, p_179218_3_, p_179218_4_, p_179218_5_);

    ItemStack tool = Utils.getItemStackInSelectedSlot(s);

    return tool.getItem().mineBlock(tool, p_179218_2_, p_179218_3_, p_179218_4_, p_179218_5_);
  }

  @Nonnull
  @Override
  public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    ItemStack bag = player.getItemInHand(hand);
    if (!world.isClientSide){

      if (Utils.getUseType(bag) == C2SMessageToggleUseType.UseType.bag) {
        DankStats type = Utils.getStats(player.getItemInHand(hand));
        player.openMenu(new PortableDankProvider(type));
        return super.use(world,player,hand);
      } else {
        ItemStack toPlace = Utils.getItemStackInSelectedSlot(bag);
        EquipmentSlot hand1 = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        //handle empty
        if (toPlace.isEmpty()){
          return InteractionResultHolder.pass(bag);
        }

        //handle food
        if (toPlace.getItem().isEdible()) {
          if (player.canEat(false)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(bag);
          }
        }
        //handle potion
        else if (toPlace.getItem() instanceof PotionItem){
          player.startUsingItem(hand);
          return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
        }
        //todo support other items?
        else {
          ItemStack newBag = bag.copy();
          player.setItemSlot(hand1, toPlace);
          InteractionResultHolder<ItemStack> actionResult = toPlace.getItem().use(world, player, hand);
          PortableDankInventory handler = Utils.getHandler(newBag);
          handler.setItem(Utils.getSelectedSlot(newBag), actionResult.getObject());
          player.setItemSlot(hand1, newBag);
        }
        }
      }
    return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
  }

  @Override
  public InteractionResult interactLivingEntity(ItemStack bag, Player player, LivingEntity entity, InteractionHand hand) {
    if (!Utils.isConstruction(bag))return InteractionResult.FAIL;
    PortableDankInventory handler = Utils.getHandler(bag);
    ItemStack toPlace = handler.getItem(Utils.getSelectedSlot(bag));
    EquipmentSlot hand1 = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    player.setItemSlot(hand1, toPlace);
    InteractionResult result = toPlace.getItem().interactLivingEntity(toPlace, player, entity, hand);
    handler.setItem(Utils.getSelectedSlot(bag),toPlace);
    player.setItemSlot(hand1, bag);
    return result;
  }

  @Override
  public boolean isFoil(ItemStack stack) {
    return stack.hasTag() && Utils.getMode(stack) != Mode.normal;
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void appendHoverText(ItemStack bag, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
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

      bits.forEach(s1 -> tooltip.add(new TextComponent(s1)));

    }


    if (!Screen.hasShiftDown()){
      tooltip.add(new TranslatableComponent("text.dankstorage.shift",
              new TextComponent("Shift").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
    }

    if (Screen.hasShiftDown()) {
      tooltip.add(new TranslatableComponent("text.dankstorage.changemode",new TextComponent(Client.CONSTRUCTION.saveString()).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
      C2SMessageToggleUseType.UseType mode = Utils.getUseType(bag);
      tooltip.add(
              new TranslatableComponent("text.dankstorage.currentusetype",new TranslatableComponent(
                      "dankstorage.usetype."+mode.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
      tooltip.add(
              new TranslatableComponent("text.dankstorage.stacklimit",new TextComponent(stats.stacklimit+"").withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.GRAY));

      DankInventory handler = Utils.getHandler(bag);

      if (handler.isEmpty()){
        tooltip.add(
                new TranslatableComponent("text.dankstorage.empty").withStyle(ChatFormatting.ITALIC));
        return;
      }
      int count1 = 0;
      for (int i = 0; i < handler.getContainerSize(); i++) {
        if (count1 > 10)break;
        ItemStack item = handler.getItem(i);
        if (item.isEmpty())continue;
        Component count = new TextComponent(Integer.toString(item.getCount())).withStyle(ChatFormatting.AQUA);
        //tooltip.add(new TranslationTextComponent("text.dankstorage.formatcontaineditems", count, item.getDisplayName().(item.getRarity().color)));
        count1++;
      }
    }
  }

  @Nonnull
  @Override
  public UseAnim getUseAnimation(ItemStack stack) {
    if (!Utils.isConstruction(stack))return UseAnim.NONE;
    ItemStack internal = Utils.getItemStackInSelectedSlot(stack);
    return internal.getItem().getUseAnimation(stack);
  }

  //called for stuff like food and potions
  @Nonnull
  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
    if (!Utils.isConstruction(stack))return stack;

    ItemStack internal = Utils.getItemStackInSelectedSlot(stack);

    if (internal.getItem().isEdible()){
     ItemStack food = entity.eat(world, internal);
     PortableDankInventory handler = Utils.getHandler(stack);
     handler.setItem(Utils.getSelectedSlot(stack), food);
     return stack;
    }

    if (internal.getItem() instanceof PotionItem){
      ItemStack potion = internal.finishUsingItem(world,entity);
      PortableDankInventory handler = Utils.getHandler(stack);
      handler.setItem(Utils.getSelectedSlot(stack), potion);
      return stack;
    }

    return super.finishUsingItem(stack, world, entity);
  }

  public int getGlintColor(ItemStack stack){
    Mode mode = Utils.getMode(stack);
    switch (mode){
      case normal:default:return 0xffffffff;
      case pickup_all:return 0xff00ff00;
      case filtered_pickup:return 0xffffff00;
      case void_pickup:return 0xffff0000;
    }
  }

  @Nonnull
  @Override
  public InteractionResult useOn(UseOnContext ctx) {
    ItemStack bag = ctx.getItemInHand();
    C2SMessageToggleUseType.UseType useType = Utils.getUseType(bag);

    if(useType == C2SMessageToggleUseType.UseType.bag) {
      return InteractionResult.PASS;
    }

    PortableDankInventory handler = Utils.getHandler(bag);
    int selectedSlot = Utils.getSelectedSlot(bag);

    ItemStack toPlace = handler.getItem(selectedSlot).copy();
    if (toPlace.getCount() == 1 && handler.isLocked(selectedSlot))
      return InteractionResult.PASS;

    UseOnContext ctx2 = new ItemUseContextExt(ctx.getLevel(),ctx.getPlayer(),ctx.getHand(),toPlace,((ItemUsageContextAccessor)ctx).getHitResult());
    InteractionResult actionResultType = toPlace.getItem().useOn(ctx2);//ctx2.getItem().onItemUse(ctx);
    handler.setItem(selectedSlot, ctx2.getItemInHand());
    return actionResultType;
  }

  @Override
  public boolean canBeHurtBy(DamageSource source) {
    return source == DamageSource.OUT_OF_WORLD;
  }

  public static class ItemUseContextExt extends UseOnContext {
    protected ItemUseContextExt(Level p_i50034_1_, @Nullable Player p_i50034_2_, InteractionHand p_i50034_3_, ItemStack p_i50034_4_, BlockHitResult p_i50034_5_) {
      super(p_i50034_1_, p_i50034_2_, p_i50034_3_, p_i50034_4_, p_i50034_5_);
    }
  }
}
