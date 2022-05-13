package com.rempler.stoneutilities.common.init;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.blocks.hopper.StoneHopperTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StoneTiles {
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, StoneUtilities.MODID);
    public static final RegistryObject<TileEntityType<StoneHopperTile>> STONE_HOPPER = TILES.register("stone_hopper",
            () -> TileEntityType.Builder.of(StoneHopperTile::new, StoneBlocks.STONE_HOPPER.get()).build(null));

    public static void init(IEventBus iEventBus) {
        StoneUtilities.LOGGER.info("Register Blocks");
        TILES.register(iEventBus);
    }
}
