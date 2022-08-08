package com.rempler.stoneutilities.common.init;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.blocks.hopper.StoneHopperMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StoneContainers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, StoneUtilities.MODID);
    public static final RegistryObject<MenuType<StoneHopperMenu>> STONE_HOPPER = CONTAINERS.register("stone_hopper", () -> new MenuType<>(StoneHopperMenu::new));

    public static void init(IEventBus modEventBus) {
        StoneUtilities.LOGGER.info("Register Items");
        CONTAINERS.register(modEventBus);
    }
}
