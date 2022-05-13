package com.rempler.stoneutilities.common.blocks.hopper;

import com.rempler.stoneutilities.common.init.StoneContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class StoneHopperContainer extends Container {
    private final IInventory hopperInventory;

    public StoneHopperContainer(int windowId, PlayerInventory playerInventory)
    {
        this(windowId, playerInventory, new Inventory(3));
    }

    public StoneHopperContainer(int windowId, PlayerInventory playerInventory, IInventory hopperInventory) {
        super(StoneContainers.STONE_HOPPER.get(), windowId);
        this.hopperInventory = hopperInventory;
        checkContainerSize(hopperInventory, 3);
        hopperInventory.startOpen(playerInventory.player);

        for(int i = 0; i < 3; i++) {
            this.addSlot(new Slot(hopperInventory, i, 62 + i * 18, 20));
        }

        for(int k = 0; k < 3; k++) {
            for(int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 8 + j * 18, k * 18 + 51));
            }
        }

        for(int l = 0; l < 9; l++) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 109));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return this.hopperInventory.stillValid(playerIn);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if(slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < this.hopperInventory.getContainerSize()) {
                if (!this.moveItemStackTo(slotStack, this.hopperInventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, this.hopperInventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return result;
    }

    @Override
    public void removed(PlayerEntity playerIn)
    {
        super.removed(playerIn);
        this.hopperInventory.stopOpen(playerIn);
    }
}
