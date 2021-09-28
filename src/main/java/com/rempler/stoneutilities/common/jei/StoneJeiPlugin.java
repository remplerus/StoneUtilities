package com.rempler.stoneutilities.common.jei;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.init.StoneBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@JeiPlugin
public class StoneJeiPlugin implements IModPlugin {

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(StoneBlocks.STONE_CRAFTING_TABLE.get()), VanillaRecipeCategoryUid.CRAFTING);
    }

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(StoneUtilities.MODID, "stone_crafting_table");
    }
}
