/*
 * This file ("TileEntityRangedCollector.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.tile;

import de.ellpeck.actuallyadditions.mod.network.gui.IButtonReactor;
import de.ellpeck.actuallyadditions.mod.util.WorldUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;

public class TileEntityRangedCollector extends TileEntityInventoryBase implements IButtonReactor{

    public static final int WHITELIST_START = 6;
    public static final int RANGE = 6;
    public boolean isWhitelist = true;
    private boolean lastWhitelist;

    public TileEntityRangedCollector(){
        super(18, "rangedCollector");
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, boolean sync){
        super.writeSyncableNBT(compound, sync);
        compound.setBoolean("Whitelist", this.isWhitelist);
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, boolean sync){
        super.readSyncableNBT(compound, sync);
        this.isWhitelist = compound.getBoolean("Whitelist");
    }

    @Override
    public void updateEntity(){
        super.updateEntity();
        if(!this.worldObj.isRemote){
            if(!this.isRedstonePowered){
                ArrayList<EntityItem> items = (ArrayList<EntityItem>)this.worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.pos.getX()-RANGE, this.pos.getY()-RANGE, this.pos.getZ()-RANGE, this.pos.getX()+RANGE, this.pos.getY()+RANGE, this.pos.getZ()+RANGE));
                if(!items.isEmpty()){
                    for(EntityItem item : items){
                        if(!item.isDead && item.getEntityItem() != null){
                            ItemStack toAdd = item.getEntityItem().copy();
                            if(TileEntityLaserRelayItemWhitelist.checkFilter(toAdd, this.isWhitelist, this.slots, WHITELIST_START, WHITELIST_START+12)){
                                ArrayList<ItemStack> checkList = new ArrayList<ItemStack>();
                                checkList.add(toAdd);
                                if(WorldUtil.addToInventory(this, 0, WHITELIST_START, checkList, EnumFacing.UP, false, true)){
                                    WorldUtil.addToInventory(this, 0, WHITELIST_START, checkList, EnumFacing.UP, true, true);
                                    item.setDead();
                                }
                            }
                        }
                    }
                }
            }

            if(this.isWhitelist != this.lastWhitelist && this.sendUpdateWithInterval()){
                this.lastWhitelist = this.isWhitelist;
            }
        }
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack){
        return false;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side){
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side){
        return slot < WHITELIST_START;
    }

    @Override
    public void onButtonPressed(int buttonID, EntityPlayer player){
        this.isWhitelist = !this.isWhitelist;
    }
}
