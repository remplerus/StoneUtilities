package com.rempler.stoneutilities.common.blocks.workbench;


import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;

public class StoneMenu extends CraftingMenu {
    private final Block workbench;
    private final ContainerLevelAccess worldPos;

    public StoneMenu(int id, Inventory inventory, ContainerLevelAccess posCallable, Block workbench) {
        super(id, inventory, posCallable);
        this.workbench = workbench;
        this.worldPos = posCallable;
    }

    protected static boolean isWithinUsableDistance(ContainerLevelAccess worldPos, Player playerEntity, Block targetBlock) {
        return worldPos.evaluate((world, blockPos) ->
                world.getBlockState(blockPos).getBlock() == targetBlock && playerEntity.distanceToSqr((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    public boolean stillValid(@Nonnull Player playerEntity) {
        return isWithinUsableDistance(this.worldPos, playerEntity, this.workbench);
    }
}
