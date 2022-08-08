package com.rempler.stoneutilities.common.init;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.blocks.hopper.StoneHopperBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StoneBlockEntites {
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, StoneUtilities.MODID);
    public static final RegistryObject<BlockEntityType<StoneHopperBlockEntity>> STONE_HOPPER = TILES.register("stone_hopper",
            () -> BlockEntityType.Builder.of(StoneHopperBlockEntity::new, StoneBlocks.STONE_HOPPER.get()).build(null));

    public static void init(IEventBus iEventBus) {
        StoneUtilities.LOGGER.info("Register Blocks");
        TILES.register(iEventBus);
    }
}
