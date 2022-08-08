package com.rempler.stoneutilities.common.blocks.hopper;

import com.rempler.stoneutilities.common.init.StoneBlockEntites;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StoneHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
    private NonNullList<ItemStack> containerList = NonNullList.withSize(3, ItemStack.EMPTY);
    private int transferCooldown = -1;
    private long tickedGameTime;

    public StoneHopperBlockEntity(BlockPos pos, BlockState state) {
        super(StoneBlockEntites.STONE_HOPPER.get(), pos, state);
    }

    @Override
    public void load(@Nonnull CompoundTag compound) {
        super.load(compound);
        this.containerList = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if(!this.tryLoadLootTable(compound)) {
            ContainerHelper.loadAllItems(compound, this.containerList);
        }
        this.transferCooldown = compound.getInt("TransferCooldown");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        if(!this.trySaveLootTable(compound)) {
            ContainerHelper.saveAllItems(compound, this.containerList);
        }

        compound.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    public int getContainerSize() {
        return this.containerList.size();
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int index, int count) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem(this.getItems(), index, count);
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        this.unpackLootTable(null);
        this.getItems().set(index, stack);
        if(stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    @Nonnull
    protected Component getDefaultName() {
        return new TranslatableComponent("container.stone_hopper");
    }

    public void tickServer() {
        if(this.level != null && !this.level.isClientSide) {
            this.transferCooldown--;
            this.tickedGameTime = this.level.getGameTime();
            if(this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                this.updateHopper(() -> pullItems(level, this));
            }
        }
    }

    private boolean updateHopper(Supplier<Boolean> supplier) {
        if(this.level != null && !this.level.isClientSide) {
            if (this.isOnTransferCooldown() && Boolean.TRUE.equals(this.getBlockState().getValue(StoneHopperBlock.ENABLED))) {
                boolean pulledItems = false;
                if (!this.isEmpty()) {
                    pulledItems = this.transferItemsOut();
                }
                if (!this.isFull()) {
                    pulledItems |= supplier.get();
                }
                if (pulledItems) {
                    this.setTransferCooldown(8);
                    this.setChanged();
                    return true;
                }
            }
            return false;
        } else return false;
    }

    private boolean isFull() {
        for(ItemStack itemstack : this.containerList) {
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private boolean transferItemsOut() {
        Container container = this.getContainerForHopperTransfer();
        if(container == null) {
            return false;
        }

        Direction direction = this.getBlockState().getValue(StoneHopperBlock.FACING).getOpposite();
        if(this.isContainerFull(container, direction)) {
            return false;
        }

        for(int index = 0; index < this.getContainerSize(); index++) {
            if(!this.getItem(index).isEmpty()) {
                ItemStack copy = this.getItem(index).copy();
                ItemStack result = putStackInContainerAllSlots(this, container, this.removeItem(index, 1), direction);
                if(result.isEmpty()) {
                    container.setChanged();
                    return true;
                }
                this.setItem(index, copy);
            }
        }

        return false;
    }

    private static IntStream getSlotsStream(Container container, Direction direction) {
        return container instanceof WorldlyContainer ? IntStream.of(((WorldlyContainer) container).getSlotsForFace(direction)) : IntStream.range(0, container.getContainerSize());
    }

    private boolean isContainerFull(Container container, Direction direction) {
        return getSlotsStream(container, direction).allMatch((index) -> {
            ItemStack stack = container.getItem(index);
            return stack.getCount() >= stack.getMaxStackSize();
        });
    }

    private static boolean isContainerEmpty(Container container, Direction direction) {
        return getSlotsStream(container, direction).allMatch((index) -> container.getItem(index).isEmpty());
    }

    public static boolean pullItems(Level level, Hopper hopper) {
        Boolean ret = VanillaInventoryCodeHooks.extractHook(level, hopper);
        if(ret != null) {
            return ret;
        }

        Container container = getSourceContainer(level, hopper);
        if(container != null) {
            Direction direction = Direction.DOWN;
            if(isContainerEmpty(container, direction)) {
                return false;
            }
            return getSlotsStream(container, direction).anyMatch((index) -> pullItemFromSlot(hopper, container, index, direction));
        }

        /* Pulls any item entities that are currently above the hopper */
        for(ItemEntity entity : getItemEntities(level, hopper)) {
            if(captureItemEntity(hopper, entity)) {
                return true;
            }
        }
        return false;
    }

    private static boolean pullItemFromSlot(Hopper hopper, Container container, int index, Direction direction) {
        ItemStack stack = container.getItem(index);
        if(!stack.isEmpty() && canExtractItemFromSlot(container, stack, index, direction)) {
            ItemStack copy = stack.copy();
            ItemStack result = putStackInContainerAllSlots(container, hopper, container.removeItem(index, 1), null);
            if(result.isEmpty()) {
                container.setChanged();
                return true;
            }
            container.setItem(index, copy);
        }
        return false;
    }

    public static boolean captureItemEntity(Container container, ItemEntity entity) {
        boolean captured = false;
        ItemStack copy = entity.getItem().copy();
        ItemStack result = putStackInContainerAllSlots(null, container, copy, null);
        if(result.isEmpty()) {
            captured = true;
            entity.remove(Entity.RemovalReason.DISCARDED);
        }
        else {
            entity.setItem(result);
        }
        return captured;
    }

    private static ItemStack putStackInContainerAllSlots(@Nullable Container source, Container destination, ItemStack stack, @Nullable Direction direction) {
        if(destination instanceof WorldlyContainer && direction != null) {
            WorldlyContainer worldlyContainer = (WorldlyContainer) destination;
            int[] slots = worldlyContainer.getSlotsForFace(direction);
            for(int i = 0; i < slots.length && !stack.isEmpty(); i++) {
                stack = insertStack(source, worldlyContainer, stack, slots[i], direction);
            }
        } else {
            int i = destination.getContainerSize();
            for(int j = 0; j < i && !stack.isEmpty(); ++j) {
                stack = insertStack(source, destination, stack, j, direction);
            }
        }
        return stack;
    }

    private static boolean canInsertItemInSlot(Container container, ItemStack stack, int index, @Nullable Direction side) {
        if(!container.canPlaceItem(index, stack)) {
            return false;
        }
        return !(container instanceof WorldlyContainer) || ((WorldlyContainer) container).canPlaceItemThroughFace(index, stack, side);
    }

    private static boolean canExtractItemFromSlot(Container container, ItemStack stack, int index, Direction side) {
        return !(container instanceof WorldlyContainer) || ((WorldlyContainer) container).canTakeItemThroughFace(index, stack, side);
    }

    private static ItemStack insertStack(@Nullable Container source, Container destination, ItemStack stack, int index, @Nullable Direction direction) {
        ItemStack slotStack = destination.getItem(index);
        if(canInsertItemInSlot(destination, stack, index, direction)) {
            boolean shouldInsert = false;
            boolean destinationEmpty = destination.isEmpty();
            if(slotStack.isEmpty()) {
                destination.setItem(index, stack);
                stack = ItemStack.EMPTY;
                shouldInsert = true;
            } else if(canCombine(slotStack, stack)) {
                int remainingCount = stack.getMaxStackSize() - slotStack.getCount();
                int shrinkCount = Math.min(stack.getCount(), remainingCount);
                stack.shrink(shrinkCount);
                slotStack.grow(shrinkCount);
                shouldInsert = shrinkCount > 0;
            }

            if(shouldInsert) {
                if(destinationEmpty && destination instanceof HopperBlockEntity) {
                    HopperBlockEntity hopper = (HopperBlockEntity) destination;
                    if(!hopper.isOnCustomCooldown())
                    {
                        int cooldownAmount = 0;
                        if(source instanceof StoneHopperBlockEntity) {
                            StoneHopperBlockEntity stoneHopperBlockEntity = (StoneHopperBlockEntity) source;
                            if(hopper.getLastUpdateTime() >= stoneHopperBlockEntity.tickedGameTime) {
                                cooldownAmount = 1;
                            }
                        }
                        hopper.setCooldown(8 - cooldownAmount);
                    }
                }
                destination.setChanged();
            }
        }

        return stack;
    }

    @Nullable
    private Container getContainerForHopperTransfer() {
        Direction direction = this.getBlockState().getValue(StoneHopperBlock.FACING);
        return getContainerAtPosition(this.getLevel(), this.worldPosition.offset(direction.getNormal()));
    }

    @Nullable
    public static Container getSourceContainer(Level level, Hopper hopper) {
        if (hopper instanceof StoneHopperBlockEntity) {
            return getContainerAtPosition(Objects.requireNonNull(((StoneHopperBlockEntity) hopper).getLevel()), hopper.getLevelX(), hopper.getLevelY() + 1.0D, hopper.getLevelZ());
        } else {
            return getContainerAtPosition(level, hopper.getLevelX(), hopper.getLevelY(), hopper.getLevelZ());
        }
    }

    public static List<ItemEntity> getItemEntities(Level level, Hopper hopper) {
        return hopper.getSuckShape().toAabbs().stream().flatMap((box) -> Objects.requireNonNull(level).getEntitiesOfClass(ItemEntity.class, box.move(hopper.getLevelX() - 0.5, hopper.getLevelY() - 0.5, hopper.getLevelZ() - 0.5), EntitySelector.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
    }

    @Nullable
    private static Container getContainerAtPosition(Level world, BlockPos pos) {
        return getContainerAtPosition(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Nullable
    private static Container getContainerAtPosition(Level worldIn, double x, double y, double z) {
        Container targetContainer = null;
        BlockPos targetPos = new BlockPos(x, y, z);
        BlockState targetState = worldIn.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();
        if(targetBlock instanceof WorldlyContainerHolder) {
            targetContainer = ((WorldlyContainerHolder) targetBlock).getContainer(targetState, worldIn, targetPos);
        }
        else if(targetState.hasBlockEntity()) {
            BlockEntity blockEntity = worldIn.getBlockEntity(targetPos);
            if(blockEntity instanceof Container) {
                targetContainer = (Container) blockEntity;
                if(targetContainer instanceof ChestBlockEntity && targetBlock instanceof ChestBlock) {
                    targetContainer = ChestBlock.getContainer((ChestBlock) targetBlock, targetState, worldIn, targetPos, true);
                }
            }
        }

        if(targetContainer == null) {
            List<Entity> itemEntities = worldIn.getEntities((Entity) null, new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR);
            if(!itemEntities.isEmpty()) {
                targetContainer = (Container) itemEntities.get(worldIn.random.nextInt(itemEntities.size()));
            }
        }

        return targetContainer;
    }

    private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        if(stack1.getItem() != stack2.getItem()) {
            return false;
        }
        else if(stack1.getDamageValue() != stack2.getDamageValue()) {
            return false;
        }
        else if(stack1.getCount() > stack1.getMaxStackSize()) {
            return false;
        }
        else {
            return ItemStack.tagMatches(stack1, stack2);
        }
    }

    @Override
    public double getLevelX()
    {
        return this.worldPosition.getX() + 0.5;
    }

    @Override
    public double getLevelY()
    {
        return this.worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ()
    {
        return this.worldPosition.getZ() + 0.5;
    }

    public void setTransferCooldown(int ticks)
    {
        this.transferCooldown = ticks;
    }

    private boolean isOnTransferCooldown()
    {
        return this.transferCooldown <= 0;
    }

    public boolean mayTransfer()
    {
        return this.transferCooldown > 8;
    }

    @Nonnull
    protected NonNullList<ItemStack> getItems()
    {
        return this.containerList;
    }

    protected void setItems(@Nonnull NonNullList<ItemStack> itemsIn)
    {
        this.containerList = itemsIn;
    }

    public void entityInside(Entity entity) {
        if(entity instanceof ItemEntity) {
            BlockPos pos = this.getBlockPos();
            if(Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ())), this.getSuckShape(), BooleanOp.AND)) {
                this.updateHopper(() -> captureItemEntity(this, (ItemEntity) entity));
            }
        }
    }

    @Override
    @Nonnull
    protected AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory) {
        return new StoneHopperMenu(id, playerInventory, this);
    }

    @Override
    @Nonnull
    protected IItemHandler createUnSidedHandler()
    {
        return new StoneHopperItemHandler(this);
    }

    @Override
    public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
        return index != 0 && (this.containerList.get(0).isEmpty() || stack.getItem() == this.containerList.get(0).getItem());
    }

}
