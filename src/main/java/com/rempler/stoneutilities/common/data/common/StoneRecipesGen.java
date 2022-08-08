package com.rempler.stoneutilities.common.data.common;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.init.StoneItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class StoneRecipesGen extends RecipeProvider {
    public StoneRecipesGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(Items.HOPPER)
                .pattern("i i")
                .pattern("ihi")
                .pattern(" i ")
                .define('i', Tags.Items.INGOTS_IRON)
                .define('h', StoneItems.STONE_HOPPER.get())
                .save(consumer);
        ShapedRecipeBuilder.shaped(StoneItems.STONE_HOPPER.get())
                .pattern("s s")
                .pattern("scs")
                .pattern(" s ")
                .define('s', Tags.Items.COBBLESTONE)
                .define('c', Tags.Items.CHESTS)
                .save(consumer, modLoc(StoneItems.STONE_HOPPER.get()));
        ShapedRecipeBuilder.shaped(StoneItems.STONE_LADDER.get())
                .pattern("s s")
                .pattern("sss")
                .pattern("s s")
                .define('s', StoneItems.STONE_STICK.get())
                .save(consumer, modLoc(StoneItems.STONE_LADDER.get()));
        ShapedRecipeBuilder.shaped(Items.COBBLESTONE)
                .pattern("ss")
                .pattern("ss")
                .define('s', StoneItems.STONE_SHARD.get())
                .save(consumer, modLoc(Items.COBBLESTONE));
        ShapedRecipeBuilder.shaped(Items.LEVER)
                .pattern("s")
                .pattern("c")
                .define('s', StoneItems.STONE_STICK.get())
                .define('c', Tags.Items.COBBLESTONE)
                .save(consumer, modLoc(Items.LEVER));
    }

    private ResourceLocation modLoc(Item item) {
        return new ResourceLocation(StoneUtilities.MODID, item.getRegistryName().getPath());
    }
}
