package com.rempler.stoneutilities.common.blocks.hopper;

import com.rempler.stoneutilities.common.init.StoneContainers;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class StoneHopperMenu extends AbstractContainerMenu {
    private final Container hopperInventory;

    public StoneHopperMenu(int windowId, Inventory playerInventory)
    {
        this(windowId, playerInventory, new SimpleContainer(3));
    }

    public StoneHopperMenu(int windowId, Inventory playerInventory, Container hopperInventory) {
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
    public boolean stillValid(Player player) {
        return this.hopperInventory.stillValid(player);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if(slot.hasItem()) {
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
    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.hopperInventory.stopOpen(playerIn);
    }
}
