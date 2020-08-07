package tfar.dankstorage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.util.registry.Registry;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.client.Client;
import tfar.dankstorage.container.DankContainer;
import tfar.dankstorage.container.DockContainer;
import tfar.dankstorage.item.UpgradeInfo;
import tfar.dankstorage.item.UpgradeItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.recipe.Serializer2;
import tfar.dankstorage.tile.DankBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import tfar.dankstorage.utils.DankStats;

import java.util.stream.IntStream;

public class DankStorage implements ModInitializer, ClientModInitializer {

  public static final String MODID = "dankstorage";

  public static Block dock = null;
  public static ScreenHandlerType<DockContainer> dank_1_container = null;
  public static ScreenHandlerType<DockContainer> dank_2_container = null;
  public static ScreenHandlerType<DockContainer> dank_3_container = null;
  public static ScreenHandlerType<DockContainer> dank_4_container = null;
  public static ScreenHandlerType<DockContainer> dank_5_container = null;
  public static ScreenHandlerType<DockContainer> dank_6_container = null;
  public static ScreenHandlerType<DockContainer> dank_7_container = null;
  public static ScreenHandlerType<DankContainer> portable_dank_1_container = null;
  public static ScreenHandlerType<DankContainer> portable_dank_2_container = null;
  public static ScreenHandlerType<DankContainer> portable_dank_3_container = null;
  public static ScreenHandlerType<DankContainer> portable_dank_4_container = null;
  public static ScreenHandlerType<DankContainer> portable_dank_5_container = null;
  public static ScreenHandlerType<DankContainer> portable_dank_6_container = null;
  public static ScreenHandlerType<DankContainer> portable_dank_7_container = null;
  public static BlockEntityType<?> dank_tile = null;
  public static RecipeSerializer<?> upgrade = null;

  public DankStorage() {
  }

  @Override
  public void onInitialize() {
    Item.Settings properties = new Item.Settings().group(ItemGroup.DECORATIONS);
    Registry.register(Registry.BLOCK,new Identifier(MODID,"dock"),dock = new DockBlock(AbstractBlock.Settings.of(Material.METAL).strength(1, 30)));
    Registry.register(Registry.ITEM,new Identifier(MODID,"dock"),new BlockItem(dock,properties));
    Registry.register(Registry.BLOCK_ENTITY_TYPE,new Identifier(MODID,"dank_tile"),dank_tile = BlockEntityType.Builder.create(DankBlockEntity::new, dock).build(null));

    IntStream.range(1,8).forEach(i -> Registry.register(Registry.ITEM,new Identifier(MODID,"dank_"+i),new DankItem(properties.maxCount(1), DankStats.values()[i])));
    IntStream.range(1,7).forEach(i -> Registry.register(Registry.ITEM,new Identifier(MODID,i+"_to_"+(i+1)),new UpgradeItem(properties,new UpgradeInfo(i,i+1))));
    Registry.register(Registry.RECIPE_SERIALIZER,new Identifier(MODID,"upgrade"),upgrade = new Serializer2());

    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"dank_1"),dank_1_container = new ScreenHandlerType<>(DockContainer::t1));
    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"portable_dank_1"),portable_dank_1_container = new ScreenHandlerType<>(DankContainer::t1));

    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"dank_2"),dank_2_container = new ScreenHandlerType<>(DockContainer::t2));
    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"portable_dank_2"),portable_dank_2_container = new ScreenHandlerType<>(DankContainer::t2));

    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"dank_3"),dank_3_container = new ScreenHandlerType<>(DockContainer::t3));
    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"portable_dank_3"),portable_dank_3_container = new ScreenHandlerType<>(DankContainer::t3));

    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"dank_4"),dank_4_container = new ScreenHandlerType<>(DockContainer::t4));
    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"portable_dank_4"),portable_dank_4_container = new ScreenHandlerType<>(DankContainer::t4));

    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"dank_5"),dank_5_container = new ScreenHandlerType<>(DockContainer::t5));
    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"portable_dank_5"),portable_dank_5_container = new ScreenHandlerType<>(DankContainer::t5));

    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"dank_6"),dank_6_container = new ScreenHandlerType<>(DockContainer::t6));
    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"portable_dank_6"),portable_dank_6_container = new ScreenHandlerType<>(DankContainer::t6));

    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"dank_7"),dank_7_container = new ScreenHandlerType<>(DockContainer::t7));
    Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,"portable_dank_7"),portable_dank_7_container = new ScreenHandlerType<>(DankContainer::t7));

    DankPacketHandler.registerMessages();
  }

  @Override
  public void onInitializeClient() {
    Client.client();
  }


    /*

  public static final ClientConfig CLIENT;
  public static final ForgeConfigSpec CLIENT_SPEC;

  public static final ServerConfig SERVER;
  public static final ForgeConfigSpec SERVER_SPEC;

  static {
    final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
    final Pair<ServerConfig, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
    SERVER_SPEC = specPair2.getRight();
    SERVER = specPair2.getLeft();
  }


  public static class ClientConfig {
    public static ForgeConfigSpec.BooleanValue preview;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
      builder.push("client");
      preview = builder
              .comment("Whether to display the preview of the item in the dank, disable if you have optifine")
              .define("preview", true);
      builder.pop();
    }
  }

  public static class ServerConfig {
    public static ForgeConfigSpec.IntValue stacklimit1;
    public static ForgeConfigSpec.IntValue stacklimit2;
    public static ForgeConfigSpec.IntValue stacklimit3;
    public static ForgeConfigSpec.IntValue stacklimit4;
    public static ForgeConfigSpec.IntValue stacklimit5;
    public static ForgeConfigSpec.IntValue stacklimit6;
    public static ForgeConfigSpec.IntValue stacklimit7;
    public static ForgeConfigSpec.BooleanValue useShareTag;
    public static ForgeConfigSpec.ConfigValue<List<String>> convertible_tags;

    public static final List<String> defaults = Lists.newArrayList(
            "forge:ingots/iron",
            "forge:ingots/gold",
            "forge:ores/coal",
            "forge:ores/diamond",
            "forge:ores/emerald",
            "forge:ores/gold",
            "forge:ores/iron",
            "forge:ores/lapis",
            "forge:ores/redstone",

            "forge:gems/amethyst",
            "forge:gems/peridot",
            "forge:gems/ruby",

            "forge:ingots/copper",
            "forge:ingots/lead",
            "forge:ingots/nickel",
            "forge:ingots/silver",
            "forge:ingots/tin",

            "forge:ores/copper",
            "forge:ores/lead",
            "forge:ores/ruby",
            "forge:ores/silver",
            "forge:ores/tin");

    public ServerConfig(ForgeConfigSpec.Builder builder) {
      builder.push("server");
      stacklimit1 = builder.
              comment("Stack limit of first dank storage")
              .defineInRange("stacklimit1", 256, 1, Integer.MAX_VALUE);
      stacklimit2 = builder.
              comment("Stack limit of second dank storage")
              .defineInRange("stacklimit2", 1024, 1, Integer.MAX_VALUE);
      stacklimit3 = builder.
              comment("Stack limit of third dank storage")
              .defineInRange("stacklimit3", 4096, 1, Integer.MAX_VALUE);
      stacklimit4 = builder.
              comment("Stack limit of fourth dank storage")
              .defineInRange("stacklimit4", 16384, 1, Integer.MAX_VALUE);
      stacklimit5 = builder.
              comment("Stack limit of fifth dank storage")
              .defineInRange("stacklimit5", 65536, 1, Integer.MAX_VALUE);
      stacklimit6 = builder.
              comment("Stack limit of sixth dank storage")
              .defineInRange("stacklimit6", 262144, 1, Integer.MAX_VALUE);
      stacklimit7 = builder.
              comment("Stack limit of seventh dank storage")
              .defineInRange("stacklimit7", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

      convertible_tags = builder.
              comment("Tags that are eligible for conversion, input as a list of resourcelocation, eg 'forge:ingots/iron'")
              .define("convertible tags", defaults);
      builder.pop();
    }
  }*/
}
