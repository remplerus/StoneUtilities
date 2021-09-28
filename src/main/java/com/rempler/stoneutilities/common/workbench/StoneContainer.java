package com.rempler.stoneutilities.common.workbench;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;

import javax.annotation.Nonnull;

public class StoneContainer extends WorkbenchContainer {
    private final Block workbench;
    private final IWorldPosCallable worldPos;

    public StoneContainer(int id, PlayerInventory inventory, IWorldPosCallable posCallable, Block workbench) {
        super(id, inventory, posCallable);
        this.workbench = workbench;
        this.worldPos = posCallable;
    }

    protected static boolean isWithinUsableDistance(IWorldPosCallable worldPos, PlayerEntity playerEntity, Block targetBlock) {
        return worldPos.evaluate((world, blockPos) ->
        {
            return world.getBlockState(blockPos).getBlock() == targetBlock && playerEntity.distanceToSqr((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D) <= 64.0D;
        }, true);
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity playerEntity) {
        return isWithinUsableDistance(this.worldPos, playerEntity, this.workbench);
    }
}
