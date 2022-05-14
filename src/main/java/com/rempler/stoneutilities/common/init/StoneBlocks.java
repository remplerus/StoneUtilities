package com.rempler.stoneutilities.common.init;

import com.rempler.stoneutilities.StoneUtilities;
import com.rempler.stoneutilities.common.blocks.StoneLadder;
import com.rempler.stoneutilities.common.blocks.hopper.StoneHopperBlock;
import com.rempler.stoneutilities.common.blocks.wallgate.AbstractWallGateBlock;
import com.rempler.stoneutilities.common.blocks.workbench.StoneCraftingBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StoneBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister
            .create(ForgeRegistries.BLOCKS, StoneUtilities.MODID);
    public static final RegistryObject<StoneCraftingBlock> STONE_CRAFTING_TABLE = BLOCKS.register("stone_crafting_table",
            () -> new StoneCraftingBlock(AbstractBlock.Properties.copy(net.minecraft.block.Blocks.CRAFTING_TABLE)
                    .strength(2F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<StoneLadder> STONE_LADDER = BLOCKS.register("stone_ladder", StoneLadder::new);
    public static final RegistryObject<StoneHopperBlock> STONE_HOPPER = BLOCKS.register("stone_hopper",
            () -> new StoneHopperBlock(AbstractBlock.Properties.copy(Blocks.HOPPER)
                    .strength(2F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<TorchBlock> STONE_TORCH = BLOCKS.register("stone_torch",
            () -> new TorchBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().instabreak()
                    .lightLevel((blockState) -> 14).sound(SoundType.WOOD), ParticleTypes.FLAME));
    public static final RegistryObject<WallTorchBlock> WALL_STONE_TORCH = BLOCKS.register("wall_stone_torch",
            () -> new WallTorchBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().instabreak()
                    .lightLevel((blockState) -> 14).sound(SoundType.WOOD).lootFrom(STONE_TORCH), ParticleTypes.FLAME));
    public static final RegistryObject<AbstractWallGateBlock> WALL_STONE_GATE = BLOCKS.register("wall_stone_gate",
            () -> new AbstractWallGateBlock(AbstractBlock.Properties.copy(Blocks.COBBLESTONE_WALL)));

    public static void init(IEventBus iEventBus) {
        StoneUtilities.LOGGER.info("Register Blocks");
        BLOCKS.register(iEventBus);
    }
}
