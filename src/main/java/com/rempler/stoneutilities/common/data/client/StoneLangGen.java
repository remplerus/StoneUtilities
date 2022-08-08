package com.rempler.stoneutilities.common.data.client;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.init.StoneBlocks;
import com.rempler.stoneutilities.common.init.StoneContainers;
import com.rempler.stoneutilities.common.init.StoneEntities;
import com.rempler.stoneutilities.common.init.StoneItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import org.apache.commons.lang3.text.WordUtils;

@SuppressWarnings("deprecation")
public class StoneLangGen extends LanguageProvider {
    public StoneLangGen(DataGenerator gen, String locale) {
        super(gen, StoneUtilities.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        for (int i = 0; i < StoneBlocks.BLOCKS.getEntries().size(); i++) {
            add(StoneBlocks.BLOCKS.getEntries().stream().toList().get(i).get(),
                    WordUtils.capitalizeFully(StoneBlocks.BLOCKS.getEntries().stream().toList().get(i).get().getRegistryName().getPath().replace("_", " ")));
        } for (int i = 0; i < StoneItems.ITEMS.getEntries().size(); i++) {
            add(StoneItems.ITEMS.getEntries().stream().toList().get(i).get(),
                    WordUtils.capitalizeFully(StoneItems.ITEMS.getEntries().stream().toList().get(i).get().getRegistryName().getPath().replace("_", " ")));
        } for (int i = 0; i < StoneEntities.ENTITIES.getEntries().size(); i++) {
            add(StoneEntities.ENTITIES.getEntries().stream().toList().get(i).get(),
                    WordUtils.capitalizeFully(StoneEntities.ENTITIES.getEntries().stream().toList().get(i).get().getRegistryName().getPath().replace("_", " ")));
        } for (int i = 0; i < StoneContainers.CONTAINERS.getEntries().size(); i++) {
            add(StoneContainers.CONTAINERS.getEntries().stream().toList().get(i).get().toString(),
                    WordUtils.capitalizeFully(StoneContainers.CONTAINERS.getEntries().stream().toList().get(i).get().getRegistryName().getPath().replace("_", " ")));
        }
    }
}
