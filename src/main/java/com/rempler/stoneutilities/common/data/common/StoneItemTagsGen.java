package com.rempler.stoneutilities.common.data.common;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.init.StoneItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class StoneItemTagsGen extends ItemTagsProvider {
    public StoneItemTagsGen(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, new StoneBlockTagsGen(generator, existingFileHelper), StoneUtilities.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(StoneUtilities.STONE_RODS).add(StoneItems.STONE_STICK.get());
    }
}
