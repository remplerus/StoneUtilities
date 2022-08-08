package com.rempler.stoneutilities.common.init;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.blocks.hopper.StoneHopperMinecartEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StoneEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, StoneUtilities.MODID);
    public static final RegistryObject<EntityType<StoneHopperMinecartEntity>> STONE_HOPPER_MINECART = ENTITIES.register("stone_hopper_minecart",
            () -> EntityType.Builder.<StoneHopperMinecartEntity>of(StoneHopperMinecartEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.7F)
                    .setCustomClientFactory(((spawnEntity, world) -> new StoneHopperMinecartEntity(world)))
                    .build("stone_hopper_minecart"));

    public static void init(IEventBus modEventBus) {
        StoneUtilities.LOGGER.info("Register Items");
        ENTITIES.register(modEventBus);
    }
}
