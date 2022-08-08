package com.rempler.stoneutilities.common.blocks.hopper;

import com.rempler.stoneutilities.common.init.StoneBlocks;
import com.rempler.stoneutilities.common.init.StoneEntities;
import com.rempler.stoneutilities.common.init.StoneItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class StoneHopperMinecartEntity extends AbstractMinecartContainer implements Hopper {
    private boolean blocked = true;
    private int transferTicker = -1;
    private final BlockPos lastPosition = BlockPos.ZERO;

    public StoneHopperMinecartEntity(Level level) {
        this(StoneEntities.STONE_HOPPER_MINECART.get(), level);
    }

    protected StoneHopperMinecartEntity(double x, double y, double z, Level level) {
        super(StoneEntities.STONE_HOPPER_MINECART.get(), x, y, z, level);
    }

    public StoneHopperMinecartEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public boolean isBlocked()
    {
        return blocked;
    }

    public void setBlocked(boolean blocked)
    {
        this.blocked = blocked;
    }

    public void setTransferTicker(int transferTicker)
    {
        this.transferTicker = transferTicker;
    }

    public boolean canTransfer()
    {
        return this.transferTicker > 0;
    }

    private boolean captureDroppedItems() {
        if(StoneHopperBlockEntity.pullItems(level, this)) {
            return true;
        }
        List<ItemEntity> list = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.25D, 0.0D, 0.25D), EntitySelector.ENTITY_STILL_ALIVE);
        if(!list.isEmpty()) {
            StoneHopperBlockEntity.captureItemEntity(this, list.get(0));
        }
        return false;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new StoneHopperMenu(id, playerInventory, this);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return StoneBlocks.STONE_HOPPER.get().defaultBlockState();
    }

    @Override
    public int getDefaultDisplayOffset() {
        return 1;
    }

    @Override
    public ItemStack getCartItem() {
        return new ItemStack(StoneItems.STONE_HOPPER_MINECART.get());
    }

    @Override
    public Type getMinecartType() {
        return Type.HOPPER;
    }

    @Nullable
    public Level getWorld() {
        return this.level;
    }

    public double getLevelX() {
        return this.getX();
    }

    public double getLevelY() {
        return this.getY() + 0.5;
    }

    public double getLevelZ() {
        return this.getZ();
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public void activateMinecart(int x, int y, int z, boolean hasPower) {
        if (hasPower == this.isBlocked()) {
            this.setBlocked(!hasPower);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && this.isAlive() && this.isBlocked()) {
            BlockPos pos = this.blockPosition();
            if (pos.equals(this.lastPosition)) {
                this.transferTicker--;
            } else {
                this.setTransferTicker(0);
            }

            if (!this.canTransfer()) {
                this.setTransferTicker(0);
                if (this.captureDroppedItems()) {
                    this.setTransferTicker(4);
                    this.setChanged();
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TransferCooldown", this.transferTicker);
        compound.putBoolean("Enabled", this.blocked);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.transferTicker = compound.getInt("TransferCooldown");
        this.blocked = !compound.contains("Enabled") || compound.getBoolean("Enabled");
    }

    @Override
    public void destroy(DamageSource damageSource) {
        super.destroy(damageSource);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(StoneBlocks.STONE_HOPPER.get());
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
