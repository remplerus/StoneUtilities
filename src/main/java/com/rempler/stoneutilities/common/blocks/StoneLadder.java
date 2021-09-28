package com.rempler.stoneutilities.common.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.SoundType;
import net.minecraftforge.common.ToolType;

public class StoneLadder extends LadderBlock {
    public StoneLadder() {
        super(AbstractBlock.Properties.copy(Blocks.LADDER)
                .strength(0.4F).sound(SoundType.LADDER).noOcclusion().harvestTool(ToolType.PICKAXE));
    }
}
