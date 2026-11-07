package com.example.examplemod;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraft.world.item.ItemNameBlockItem;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "kaleidoscope_colombia";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final RegistryObject<SoundEvent> EXPRIMIR_LIMON =
            SOUND_EVENTS.register("exprimir_limon", () ->
                    SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(MODID, "exprimir_limon")
                    )
            );

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> BUNUELO =
            ITEMS.register("bunuelo", () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .food(new FoodProperties.Builder()
                            .nutrition(2)
                            .saturationMod(0.6f)
                            .build()
                    )
            ));
    public static final RegistryObject<Item> LIMON =
            ITEMS.register("limon", () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .food(new FoodProperties.Builder()
                            .nutrition(2)
                            .saturationMod(0.1f)
                            .build()
                    )
            ));
    public static final RegistryObject<Item> LIMON_CORTADO =
        ITEMS.register("limon_cortado", () -> new Item(new Item.Properties()
                .stacksTo(16)
                .food(new FoodProperties.Builder()
                        .nutrition(1)
                        .saturationMod(0.1f)
                        .build()
                )
        ));
    public static final RegistryObject<Item> SEMILLAS_DE_LIMON =
            ITEMS.register("semillas_de_limon", () -> new ItemNameBlockItem(
                    ModBlocks.LIMON_CROP.get(),
                    new Item.Properties().stacksTo(64)
            ));
    public static final RegistryObject<Item> EXPRIMIDOR_DE_LIMONES =
            ITEMS.register("exprimidor_de_limones", () -> new ExprimidorItem(
                    new Item.Properties().stacksTo(1)
            ));
    public static final RegistryObject<Item> JUGO_DE_LIMON =
            ITEMS.register("jugo_de_limon", () -> new Item(
                    new Item.Properties().stacksTo(16)
            ));
    public static final RegistryObject<Item> QUESO =
            ITEMS.register("queso", () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .food(new FoodProperties.Builder()
                            .nutrition(1)
                            .saturationMod(0.6f)
                            .build()
                    )
            ));
    public static final RegistryObject<Item> BUNUELO_CRUDO =
            ITEMS.register("bunuelo_crudo", () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .food(new FoodProperties.Builder()
                            .nutrition(1)
                            .saturationMod(0.1f)
                            .build()
                    )
            ));

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> COLOMBIA_TAB =
            CREATIVE_MODE_TABS.register("colombia_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.kaleidoscope_colombia.colombia_tab"))
                    .withTabsBefore(new ResourceLocation("kaleidoscope_cookery", "cookery_food"))
                    .icon(() -> BUNUELO.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(BUNUELO.get());
                        output.accept(LIMON.get());
                        output.accept(LIMON_CORTADO.get());
                        output.accept(SEMILLAS_DE_LIMON.get());
                        output.accept(EXPRIMIDOR_DE_LIMONES.get());
                        output.accept(JUGO_DE_LIMON.get());
                        output.accept(QUESO.get());
                        output.accept(BUNUELO_CRUDO.get());
                    }).build());
    public ExampleMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        //
        SOUND_EVENTS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {


    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
