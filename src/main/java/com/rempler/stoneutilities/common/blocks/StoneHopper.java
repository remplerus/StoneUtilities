package com.rempler.stoneutilities.common.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.SoundType;
import net.minecraftforge.common.ToolType;

public class StoneHopper extends HopperBlock {
    public StoneHopper() {
        super(AbstractBlock.Properties.copy(Blocks.HOPPER).strength(5).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).noOcclusion());
    }
}
