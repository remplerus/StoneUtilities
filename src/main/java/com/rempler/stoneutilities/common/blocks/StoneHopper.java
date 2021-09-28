package com.rempler.stoneutilities.common.blocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StoneHopper extends HopperBlock {
    public StoneHopper() {
        super(BlockBehaviour.Properties.copy(Blocks.HOPPER).strength(5).sound(SoundType.STONE).noOcclusion());
    }
}
