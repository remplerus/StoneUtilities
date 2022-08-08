package com.rempler.stoneutilities.common.data;

import com.rempler.stoneutilities.common.data.client.StoneLangGen;
import com.rempler.stoneutilities.common.data.common.StoneBlockTagsGen;
import com.rempler.stoneutilities.common.data.common.StoneItemTagsGen;
import com.rempler.stoneutilities.common.data.common.StoneLootTablesGen;
import com.rempler.stoneutilities.common.data.common.StoneRecipesGen;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class StoneDataGen {
    @SubscribeEvent
    public static void gatherDataEvent(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        if (event.includeServer()) {
            generator.addProvider(new StoneBlockTagsGen(generator, fileHelper));
            generator.addProvider(new StoneItemTagsGen(generator, fileHelper));
            generator.addProvider(new StoneRecipesGen(generator));
            generator.addProvider(new StoneLootTablesGen(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(new StoneLangGen(generator, "en_us"));
        }
    }
}
