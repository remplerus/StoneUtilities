package com.rempler.stoneutilities.common.blocks.hopper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class StoneHopperItemHandler extends InvWrapper {
    private final StoneHopperTile hopper;

    public StoneHopperItemHandler(StoneHopperTile tile) {
        super(tile);
        this.hopper = tile;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(simulate) {
            return super.insertItem(slot, stack, simulate);
        }

        boolean wasEmpty = getInv().isEmpty();
        int originalStackSize = stack.getCount();
        stack = super.insertItem(slot, stack, simulate);
        if(wasEmpty && originalStackSize > stack.getCount() && !hopper.mayTransfer()) {
            hopper.setTransferCooldown(8);
        }
        return stack;
    }
}
