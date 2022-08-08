package com.rempler.stoneutilities.common.init;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.blocks.StoneLadder;
import com.rempler.stoneutilities.common.blocks.hopper.StoneHopperBlock;
import com.rempler.stoneutilities.common.blocks.wallgate.AbstractWallGateBlock;
import com.rempler.stoneutilities.common.blocks.workbench.StoneCraftingBlock;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StoneBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister
            .create(ForgeRegistries.BLOCKS, StoneUtilities.MODID);
    public static final RegistryObject<Block> STONE_CRAFTING_TABLE = BLOCKS.register("stone_crafting_table",
            () -> new StoneCraftingBlock(Block.Properties.copy(Blocks.CRAFTING_TABLE)
                    .strength(2F).sound(SoundType.STONE)));
    public static final RegistryObject<Block> STONE_LADDER = BLOCKS.register("stone_ladder", StoneLadder::new);
    public static final RegistryObject<StoneHopperBlock> STONE_HOPPER = BLOCKS.register("stone_hopper",
            () -> new StoneHopperBlock(Block.Properties.copy(Blocks.HOPPER)
                    .strength(2F).sound(SoundType.STONE)));
    public static final RegistryObject<TorchBlock> STONE_TORCH = BLOCKS.register("stone_torch",
            () -> new TorchBlock(Block.Properties.of(Material.DECORATION).noCollission().instabreak()
                    .lightLevel((blockState) -> 14).sound(SoundType.WOOD), ParticleTypes.FLAME));
    public static final RegistryObject<WallTorchBlock> WALL_STONE_TORCH = BLOCKS.register("wall_stone_torch",
            () -> new WallTorchBlock(Block.Properties.of(Material.DECORATION).noCollission().instabreak()
                    .lightLevel((blockState) -> 14).sound(SoundType.WOOD).lootFrom(STONE_TORCH), ParticleTypes.FLAME));
    public static final RegistryObject<AbstractWallGateBlock> WALL_STONE_GATE = BLOCKS.register("wall_stone_gate",
            () -> new AbstractWallGateBlock(Block.Properties.copy(Blocks.COBBLESTONE_WALL)));

    public static void init(IEventBus iEventBus) {
        StoneUtilities.LOGGER.info("Register Blocks");
        BLOCKS.register(iEventBus);
    }
}
