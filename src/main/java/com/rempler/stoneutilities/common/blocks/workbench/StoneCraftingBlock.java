package com.rempler.stoneutilities.common.blocks.workbench;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StoneCraftingBlock extends CraftingTableBlock {
    public StoneCraftingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos blockPos) {
        return new SimpleMenuProvider((id, inventory, entity) -> new StoneMenu(id, inventory, ContainerLevelAccess.create(level, blockPos), this), new TranslatableComponent("container.stone_crafting_table"));
    }
}