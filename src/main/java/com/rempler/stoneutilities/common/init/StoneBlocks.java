package com.rempler.stoneutilities.common.init;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.workbench.StoneCraftingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StoneBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister
            .create(ForgeRegistries.BLOCKS, StoneUtilities.MODID);
    public static final RegistryObject<Block> STONE_CRAFTING_TABLE = BLOCKS.register("stone_crafting_table",
            () -> new StoneCraftingBlock(Block.Properties.copy(Blocks.CRAFTING_TABLE)
                    .strength(2F).sound(SoundType.STONE)));

    public static void init(IEventBus iEventBus) {
        BLOCKS.register(iEventBus);
    }
}
