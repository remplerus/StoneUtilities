package com.rempler.stoneutilities.common.blocks.hopper;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StoneHopperMinecartItem extends Item {
    private static final IDispenseItemBehavior MINECART_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {
        private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack execute(IBlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            World world = source.getLevel();
            double posX = source.x() + direction.getStepX() * 1.125D;
            double posY = Math.floor(source.y()) + direction.getStepY();
            double posZ = source.z() + direction.getStepZ() * 1.125D;
            BlockPos adjacentPos = source.getPos().offset(direction.getNormal());
            BlockState adjacentState = world.getBlockState(adjacentPos);
            RailShape adjacentShape = adjacentState.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) adjacentState.getBlock()).getRailDirection(adjacentState, world, adjacentPos, null) : RailShape.NORTH_SOUTH;
            double yOffset;
            if(adjacentState.is(BlockTags.RAILS)) {
                if(adjacentShape.isAscending()) {
                    yOffset = 0.6D;
                }
                else {
                    yOffset = 0.1D;
                }
            }
            else {
                if(!adjacentState.isAir(world, adjacentPos) || !world.getBlockState(adjacentPos.below()).is(BlockTags.RAILS)) {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }

                BlockState state = world.getBlockState(adjacentPos.below());
                RailShape shape = state.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) state.getBlock()).getRailDirection(state, world, adjacentPos.below(), null) : RailShape.NORTH_SOUTH;
                if(direction != Direction.DOWN && shape.isAscending()) {
                    yOffset = -0.4D;
                }
                else {
                    yOffset = -0.9D;
                }
            }

            AbstractMinecartEntity minecart = new StoneHopperMinecart(posX, posY + yOffset, posZ, world);
            if(stack.hasCustomHoverName()) {
                minecart.setCustomName(stack.getDisplayName());
            }

            world.addFreshEntity(minecart);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(IBlockSource source) {
            source.getLevel().levelEvent(1000, source.getPos(), 0);
        }
    };

    public StoneHopperMinecartItem(Item.Properties builder) {
        super(builder);
        DispenserBlock.registerBehavior(this, MINECART_DISPENSER_BEHAVIOR);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        if(!state.is(BlockTags.RAILS)) {
            return ActionResultType.FAIL;
        }

        ItemStack stack = context.getItemInHand();
        if(!world.isClientSide) {
            RailShape shape = state.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) state.getBlock()).getRailDirection(state, world, pos, null) : RailShape.NORTH_SOUTH;
            double yOffset = 0.0D;
            if(shape.isAscending()) {
                yOffset = 0.5D;
            }
            AbstractMinecartEntity minecart = new StoneHopperMinecart(pos.getX() + 0.5, pos.getY() + 0.0625 + yOffset, pos.getZ() + 0.5, world);
            if(stack.hasCustomHoverName()) {
                minecart.setCustomName(stack.getDisplayName());
            }
            world.addFreshEntity(minecart);
        }
        stack.shrink(1);
        return ActionResultType.SUCCESS;
    }
}
