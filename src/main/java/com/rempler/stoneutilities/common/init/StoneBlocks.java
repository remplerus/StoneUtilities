package com.rempler.stoneutilities.common.init;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.workbench.StoneCraftingBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StoneBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister
            .create(ForgeRegistries.BLOCKS, StoneUtilities.MODID);
    public static final RegistryObject<Block> STONE_CRAFTING_TABLE = BLOCKS.register("stone_crafting_table",
            () -> new StoneCraftingBlock(AbstractBlock.Properties.copy(net.minecraft.block.Blocks.CRAFTING_TABLE)
                    .strength(2F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE)));

    public static void init(IEventBus iEventBus) {
        BLOCKS.register(iEventBus);
    }
}
