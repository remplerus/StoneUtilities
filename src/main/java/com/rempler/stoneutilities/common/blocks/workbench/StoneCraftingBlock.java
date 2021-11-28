package com.rempler.stoneutilities.common.blocks.workbench;

import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class StoneCraftingBlock extends CraftingTableBlock {
    public StoneCraftingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public INamedContainerProvider getMenuProvider(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        return new SimpleNamedContainerProvider((id, inventory, entity) -> new StoneContainer(id, inventory, IWorldPosCallable.create(world, pos), this), new TranslationTextComponent("container.stone_crafting_table"));
    }
}
