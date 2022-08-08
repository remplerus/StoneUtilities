package com.rempler.stoneutilities.common.data.common;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.init.StoneBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StoneLootTablesGen extends LootTableProvider {
    @Nonnull
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping()
            .create();
    @Nonnull
    private static final Logger logger = StoneUtilities.LOGGER;
    @Nonnull
    protected final Map<ResourceLocation, LootTable> lootTables = new HashMap<>();
    @Nonnull
    private final DataGenerator generator;

    public StoneLootTablesGen(DataGenerator generator) {
        super(generator);
        this.generator = generator;
    }

    @Override
    public void run(HashCache cache) {
        lootTables.clear();
        @Nonnull final Path outFolder = generator.getOutputFolder();

        newLootTables();

        @Nonnull final ValidationContext validator = new ValidationContext(
                LootContextParamSets.ALL_PARAMS,
                function -> null, lootTables::get);
        lootTables.forEach((name, table) -> LootTables.validate(validator, name, table));
        @Nonnull final Multimap<String, String> problems = validator.getProblems();
        if (!problems.isEmpty()) {
            problems.forEach(
                    (name, table) -> logger.warn("Found validation problem in " + name + ": " + table));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        } else {
            lootTables.forEach((name, table) -> {
                @Nonnull final Path out = getPath(outFolder, name);

                try {
                    DataProvider.save(GSON, cache, LootTables.serialize(table), out);
                } catch (IOException e) {
                    logger.error("Couldn't save loot table " + out);
                    logger.error(Arrays.toString(e.getStackTrace()));
                }
            });
        }
    }

    @Nonnull
    private Path getPath(@Nonnull final Path outFolder, @Nonnull final ResourceLocation name) {
        return outFolder.resolve(
                "data/" + name.getNamespace() + "/loot_tables/" + name.getPath() + ".json");
    }

    public void newLootTables() {
        for (int i = 0; i < StoneBlocks.BLOCKS.getEntries().size(); i++) {
            registerBlockDrop(StoneBlocks.BLOCKS.getEntries().stream().toList().get(i).get());
        }
    }

    private void registerBlockDrop(Block block) {
        register(block.getRegistryName(), LootTable.lootTable().withPool(
                LootPool.lootPool().when(ExplosionCondition.survivesExplosion())
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(block.asItem()))));
    }

    private void register(ResourceLocation blockName, LootTable.Builder table) {
        if (lootTables.put(new ResourceLocation(blockName.getNamespace(), "blocks/" + blockName.getPath()),
                table.setParamSet(LootContextParamSets.BLOCK).build()) != null) {
            throw new IllegalStateException("Duplicate loot table: " + table);
        }
    }
}
