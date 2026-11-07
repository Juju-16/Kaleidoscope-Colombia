package com.example.examplemod;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MODID);

    public static final RegistryObject<Block> LIMON_CROP =
            BLOCKS.register("limon_crop", () -> new LimonCropBlock(
                    BlockBehaviour.Properties.copy(Blocks.WHEAT)
                            .noCollission()
                            .randomTicks()
                            .instabreak()
            ));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}