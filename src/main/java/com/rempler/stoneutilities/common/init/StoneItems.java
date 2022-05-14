package com.rempler.stoneutilities.common.init;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.blocks.hopper.StoneHopperMinecartItem;
import com.rempler.stoneutilities.common.items.armor.StoneArmorMaterial;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.WallOrFloorItem;
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
    public static final RegistryObject<Item> STONE_HOPPER = ITEMS.register("stone_hopper",
            () -> new BlockItem(StoneBlocks.STONE_HOPPER.get(), TAB));
    public static final RegistryObject<Item> STONE_TORCH = ITEMS.register("stone_torch",
            () -> new WallOrFloorItem(StoneBlocks.STONE_TORCH.get(), StoneBlocks.WALL_STONE_TORCH.get(), TAB));
    public static final RegistryObject<Item> STONE_HOPPER_MINECART = ITEMS.register("stone_hopper_minecart",
            () -> new StoneHopperMinecartItem(new Item.Properties().tab(StoneUtilities.TAB).stacksTo(1)));
    public static final RegistryObject<Item> STONE_SHEAR = ITEMS.register("stone_shear",
            () -> new ShearsItem(new Item.Properties().tab(StoneUtilities.TAB).durability(StoneConfig.getShearDurability())));
    public static final RegistryObject<Item> STONE_HELMET = ITEMS.register("stone_helmet",
            () -> new ArmorItem(StoneArmorMaterial.STONE, EquipmentSlotType.HEAD, new Item.Properties().tab(StoneUtilities.TAB).durability(StoneConfig.getHelmetDurability())));
    public static final RegistryObject<Item> STONE_CHESTPLATE = ITEMS.register("stone_chestplate",
            () -> new ArmorItem(StoneArmorMaterial.STONE, EquipmentSlotType.CHEST, new Item.Properties().tab(StoneUtilities.TAB).durability(StoneConfig.getChestplateDurability())));
    public static final RegistryObject<Item> STONE_LEGGINGS = ITEMS.register("stone_leggings",
            () -> new ArmorItem(StoneArmorMaterial.STONE, EquipmentSlotType.LEGS, new Item.Properties().tab(StoneUtilities.TAB).durability(StoneConfig.getLeggingsDurability())));
    public static final RegistryObject<Item> STONE_BOOTS = ITEMS.register("stone_boots",
            () -> new ArmorItem(StoneArmorMaterial.STONE, EquipmentSlotType.FEET, new Item.Properties().tab(StoneUtilities.TAB).durability(StoneConfig.getBootsDurability())));
    public static final RegistryObject<Item> STONE_GEAR = ITEMS.register("stone_gear", () -> new Item(TAB));
    public static final RegistryObject<Item> WALL_STONE_GATE = ITEMS.register("wall_stone_gate",
            () -> new BlockItem(StoneBlocks.WALL_STONE_GATE.get(), TAB));

    public static void init(IEventBus modEventBus) {
        StoneUtilities.LOGGER.info("Register Items");
        ITEMS.register(modEventBus);
    }
}
