package com.rempler.stoneutilities.common.workbench;

import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class StoneCraftingBlock extends CraftingTableBlock {
    public StoneCraftingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public INamedContainerProvider getMenuProvider(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedContainerProvider((id, inventory, entity) -> new StoneContainer(id, inventory, IWorldPosCallable.create(world, pos), this), new TranslationTextComponent("container.stone_crafting_table"));
    }
}
