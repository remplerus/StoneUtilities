package com.rempler.stoneutilities.common.blocks.hopper;

import com.rempler.stoneutilities.common.init.StoneTiles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StoneHopperTile extends LockableLootTileEntity implements IHopper, ITickableTileEntity {
    private NonNullList<ItemStack> inventoryList = NonNullList.withSize(3, ItemStack.EMPTY);
    private int transferCooldown = -1;
    private long tickedGameTime;

    public StoneHopperTile()
    {
        super(StoneTiles.STONE_HOPPER.get());
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT compound) {
        super.load(state, compound);
        this.inventoryList = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if(!this.tryLoadLootTable(compound)) {
            ItemStackHelper.loadAllItems(compound, this.inventoryList);
        }
        this.transferCooldown = compound.getInt("TransferCooldown");
    }

    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        super.save(compound);
        if(!this.trySaveLootTable(compound)) {
            ItemStackHelper.saveAllItems(compound, this.inventoryList);
        }

        compound.putInt("TransferCooldown", this.transferCooldown);
        return compound;
    }

    @Override
    public int getContainerSize() {
        return this.inventoryList.size();
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int index, int count) {
        this.unpackLootTable(null);
        return ItemStackHelper.removeItem(this.getItems(), index, count);
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
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.stone_hopper");
    }

    @Override
    public void tick() {
        if(this.level != null && !this.level.isClientSide) {
            this.transferCooldown--;
            this.tickedGameTime = this.level.getGameTime();
            if(this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                this.updateHopper(() -> pullItems(this));
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
        for(ItemStack itemstack : this.inventoryList) {
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private boolean transferItemsOut() {
        IInventory inventory = this.getInventoryForHopperTransfer();
        if(inventory == null) {
            return false;
        }

        Direction direction = this.getBlockState().getValue(StoneHopperBlock.FACING).getOpposite();
        if(this.isInventoryFull(inventory, direction)) {
            return false;
        }

        for(int index = 0; index < this.getContainerSize(); index++) {
            if(!this.getItem(index).isEmpty()) {
                ItemStack copy = this.getItem(index).copy();
                ItemStack result = putStackInInventoryAllSlots(this, inventory, this.removeItem(index, 1), direction);
                if(result.isEmpty()) {
                    inventory.setChanged();
                    return true;
                }
                this.setItem(index, copy);
            }
        }

        return false;
    }

    private static IntStream getSlotsStream(IInventory inventory, Direction direction) {
        return inventory instanceof ISidedInventory ? IntStream.of(((ISidedInventory) inventory).getSlotsForFace(direction)) : IntStream.range(0, inventory.getContainerSize());
    }

    private boolean isInventoryFull(IInventory inventory, Direction direction) {
        return getSlotsStream(inventory, direction).allMatch((index) -> {
            ItemStack stack = inventory.getItem(index);
            return stack.getCount() >= stack.getMaxStackSize();
        });
    }

    private static boolean isInventoryEmpty(IInventory inventory, Direction direction) {
        return getSlotsStream(inventory, direction).allMatch((index) -> inventory.getItem(index).isEmpty());
    }

    public static boolean pullItems(IHopper hopper) {
        Boolean ret = VanillaInventoryCodeHooks.extractHook(hopper);
        if(ret != null) {
            return ret;
        }

        IInventory inventory = getSourceInventory(hopper);
        if(inventory != null) {
            Direction direction = Direction.DOWN;
            if(isInventoryEmpty(inventory, direction)) {
                return false;
            }
            return getSlotsStream(inventory, direction).anyMatch((index) -> pullItemFromSlot(hopper, inventory, index, direction));
        }

        /* Pulls any item entities that are currently above the hopper */
        for(ItemEntity entity : getItemEntities(hopper)) {
            if(captureItemEntity(hopper, entity)) {
                return true;
            }
        }
        return false;
    }

    private static boolean pullItemFromSlot(IHopper hopper, IInventory inventory, int index, Direction direction) {
        ItemStack stack = inventory.getItem(index);
        if(!stack.isEmpty() && canExtractItemFromSlot(inventory, stack, index, direction)) {
            ItemStack copy = stack.copy();
            ItemStack result = putStackInInventoryAllSlots(inventory, hopper, inventory.removeItem(index, 1), null);
            if(result.isEmpty()) {
                inventory.setChanged();
                return true;
            }
            inventory.setItem(index, copy);
        }
        return false;
    }

    public static boolean captureItemEntity(IInventory inventory, ItemEntity entity) {
        boolean captured = false;
        ItemStack copy = entity.getItem().copy();
        ItemStack result = putStackInInventoryAllSlots(null, inventory, copy, null);
        if(result.isEmpty()) {
            captured = true;
            entity.remove();
        }
        else {
            entity.setItem(result);
        }
        return captured;
    }

    private static ItemStack putStackInInventoryAllSlots(@Nullable IInventory source, IInventory destination, ItemStack stack, @Nullable Direction direction) {
        if(destination instanceof ISidedInventory && direction != null) {
            ISidedInventory sidedInventory = (ISidedInventory) destination;
            int[] slots = sidedInventory.getSlotsForFace(direction);
            for(int i = 0; i < slots.length && !stack.isEmpty(); i++) {
                stack = insertStack(source, sidedInventory, stack, slots[i], direction);
            }
        } else {
            int i = destination.getContainerSize();
            for(int j = 0; j < i && !stack.isEmpty(); ++j) {
                stack = insertStack(source, destination, stack, j, direction);
            }
        }
        return stack;
    }

    private static boolean canInsertItemInSlot(IInventory inventory, ItemStack stack, int index, @Nullable Direction side) {
        if(!inventory.canPlaceItem(index, stack)) {
            return false;
        }
        return !(inventory instanceof ISidedInventory) || ((ISidedInventory) inventory).canPlaceItemThroughFace(index, stack, side);
    }

    private static boolean canExtractItemFromSlot(IInventory inventory, ItemStack stack, int index, Direction side) {
        return !(inventory instanceof ISidedInventory) || ((ISidedInventory) inventory).canTakeItemThroughFace(index, stack, side);
    }

    private static ItemStack insertStack(@Nullable IInventory source, IInventory destination, ItemStack stack, int index, @Nullable Direction direction) {
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
                if(destinationEmpty && destination instanceof HopperTileEntity) {
                    HopperTileEntity hopper = (HopperTileEntity) destination;
                    if(!hopper.isOnCustomCooldown())
                    {
                        int cooldownAmount = 0;
                        if(source instanceof StoneHopperTile) {
                            StoneHopperTile stoneHopperTile = (StoneHopperTile) source;
                            if(hopper.getLastUpdateTime() >= stoneHopperTile.tickedGameTime) {
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
    private IInventory getInventoryForHopperTransfer() {
        Direction direction = this.getBlockState().getValue(StoneHopperBlock.FACING);
        return getInventoryAtPosition(this.getLevel(), this.worldPosition.offset(direction.getNormal()));
    }

    @Nullable
    public static IInventory getSourceInventory(IHopper hopper) {
        if (hopper instanceof StoneHopperTile) {
            return getInventoryAtPosition(Objects.requireNonNull(hopper.getLevel()), hopper.getLevelX(), hopper.getLevelY() + 1.0D, hopper.getLevelZ());
        } else {
            return getInventoryAtPosition(hopper.getLevel(), hopper.getLevelX(), hopper.getLevelY(), hopper.getLevelZ());
        }
    }

    public static List<ItemEntity> getItemEntities(IHopper hopper) {
        return hopper.getSuckShape().toAabbs().stream().flatMap((box) -> Objects.requireNonNull(hopper.getLevel()).getEntitiesOfClass(ItemEntity.class, box.move(hopper.getLevelX() - 0.5, hopper.getLevelY() - 0.5, hopper.getLevelZ() - 0.5), EntityPredicates.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
    }

    @Nullable
    private static IInventory getInventoryAtPosition(World world, BlockPos pos) {
        return getInventoryAtPosition(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Nullable
    private static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z) {
        IInventory targetInventory = null;
        BlockPos targetPos = new BlockPos(x, y, z);
        BlockState targetState = worldIn.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();
        if(targetBlock instanceof ISidedInventoryProvider) {
            targetInventory = ((ISidedInventoryProvider) targetBlock).getContainer(targetState, worldIn, targetPos);
        }
        else if(targetState.hasTileEntity()) {
            TileEntity tileEntity = worldIn.getBlockEntity(targetPos);
            if(tileEntity instanceof IInventory) {
                targetInventory = (IInventory) tileEntity;
                if(targetInventory instanceof ChestTileEntity && targetBlock instanceof ChestBlock) {
                    targetInventory = ChestBlock.getContainer((ChestBlock) targetBlock, targetState, worldIn, targetPos, true);
                }
            }
        }

        if(targetInventory == null) {
            List<Entity> itemEntities = worldIn.getEntities((Entity) null, new AxisAlignedBB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.CONTAINER_ENTITY_SELECTOR);
            if(!itemEntities.isEmpty()) {
                targetInventory = (IInventory) itemEntities.get(worldIn.random.nextInt(itemEntities.size()));
            }
        }

        return targetInventory;
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
        return this.inventoryList;
    }

    protected void setItems(@Nonnull NonNullList<ItemStack> itemsIn)
    {
        this.inventoryList = itemsIn;
    }

    public void entityInside(Entity entity) {
        if(entity instanceof ItemEntity) {
            BlockPos pos = this.getBlockPos();
            if(VoxelShapes.joinIsNotEmpty(VoxelShapes.create(entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ())), this.getSuckShape(), IBooleanFunction.AND)) {
                this.updateHopper(() -> captureItemEntity(this, (ItemEntity) entity));
            }
        }
    }

    @Override
    @Nonnull
    protected Container createMenu(int id, @Nonnull PlayerInventory player) {
        return new StoneHopperContainer(id, player, this);
    }

    @Override
    @Nonnull
    protected IItemHandler createUnSidedHandler()
    {
        return new StoneHopperItemHandler(this);
    }

    @Override
    public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
        return index != 0 && (this.inventoryList.get(0).isEmpty() || stack.getItem() == this.inventoryList.get(0).getItem());
    }

}
