package com.rempler.stoneutilities.common.blocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StoneLadder extends LadderBlock {
    public StoneLadder() {
        super(BlockBehaviour.Properties.copy(Blocks.LADDER)
                .strength(0.4F).sound(SoundType.LADDER).noOcclusion());
    }
}
