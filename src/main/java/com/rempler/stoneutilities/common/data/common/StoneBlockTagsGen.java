package com.rempler.stoneutilities.common.data.common;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.init.StoneBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class StoneBlockTagsGen extends BlockTagsProvider {
    public StoneBlockTagsGen(DataGenerator p_126511_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126511_, StoneUtilities.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.CLIMBABLE).add(StoneBlocks.STONE_LADDER.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(StoneBlocks.STONE_LADDER.get(), StoneBlocks.STONE_HOPPER.get(),
                StoneBlocks.STONE_CRAFTING_TABLE.get(), StoneBlocks.WALL_STONE_GATE.get());
    }
}
