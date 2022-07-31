package com.rempler.stoneutilities.common.blocks.hopper;

import com.rempler.stoneutilities.common.init.StoneBlocks;
import com.rempler.stoneutilities.common.init.StoneEntities;
import com.rempler.stoneutilities.common.init.StoneItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class StoneHopperMinecartEntity extends ContainerMinecartEntity implements IHopper {
    private boolean blocked = true;
    private int transferTicker = -1;
    private final BlockPos lastPosition = BlockPos.ZERO;

    public StoneHopperMinecartEntity(World world) {
        this(StoneEntities.STONE_HOPPER_MINECART.get(), world);
    }

    protected StoneHopperMinecartEntity(double x, double y, double z, World world) {
        super(StoneEntities.STONE_HOPPER_MINECART.get(), x, y, z, world);
    }

    public StoneHopperMinecartEntity(EntityType<?> type, World world) {
        super(type, world);
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
        if(StoneHopperTile.pullItems(this)) {
            return true;
        }
        List<ItemEntity> list = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.25D, 0.0D, 0.25D), EntityPredicates.ENTITY_STILL_ALIVE);
        if(!list.isEmpty()) {
            StoneHopperTile.captureItemEntity(this, list.get(0));
        }
        return false;
    }

    @Override
    protected Container createMenu(int id, PlayerInventory playerInventory) {
        return new StoneHopperContainer(id, playerInventory, this);
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
    @Override
    public World getLevel() {
        return this.level;
    }

    @Override
    public double getLevelX() {
        return this.getX();
    }

    @Override
    public double getLevelY() {
        return this.getY() + 0.5;
    }

    @Override
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
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TransferCooldown", this.transferTicker);
        compound.putBoolean("Enabled", this.blocked);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
