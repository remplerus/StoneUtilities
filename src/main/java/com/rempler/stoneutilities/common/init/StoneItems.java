package com.rempler.stoneutilities.common.init;

import com.rempler.stoneutilities.StoneUtilities;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StoneItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            ForgeRegistries.ITEMS, StoneUtilities.MODID);
    private static final Item.Properties TAB = new Item.Properties().tab(StoneUtilities.TAB);

    public static final RegistryObject<Item> STONE_SHARD = ITEMS.register("stone_shard",
            () -> new Item(TAB));
    public static final RegistryObject<Item> STONE_STICK = ITEMS.register("stone_stick",
            () -> new Item(TAB));
    public static final RegistryObject<Item> STONE_CRAFTING_TABLE = ITEMS.register("stone_crafting_table",
            () -> new BlockItem(StoneBlocks.STONE_CRAFTING_TABLE.get(), TAB));
    public static final RegistryObject<Item> STONE_LADDER = ITEMS.register("stone_ladder",
            () -> new BlockItem(StoneBlocks.STONE_LADDER.get(), TAB));

    public static void init(IEventBus modEventBus) {
        StoneUtilities.LOGGER.info("Register Items");
        ITEMS.register(modEventBus);
    }
}
