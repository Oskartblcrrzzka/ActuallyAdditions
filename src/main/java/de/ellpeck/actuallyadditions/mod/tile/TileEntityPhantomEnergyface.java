/*
 * This file ("TileEntityPhantomEnergyface.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.tile;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import de.ellpeck.actuallyadditions.mod.blocks.BlockPhantom;
import de.ellpeck.actuallyadditions.mod.util.WorldUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityPhantomEnergyface extends TileEntityPhantomface implements IEnergyReceiver, IEnergyProvider{

    public TileEntityPhantomEnergyface(){
        super("energyface");
        this.type = BlockPhantom.Type.ENERGYFACE;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate){
        return this.isBoundThingInRange() && this.getReceiver() != null ? this.getReceiver().receiveEnergy(from, maxReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate){
        return this.isBoundThingInRange() && this.getProvider() != null ? this.getProvider().extractEnergy(from, maxExtract, simulate) : 0;
    }

    @Override
    public int getEnergyStored(EnumFacing from){
        if(this.isBoundThingInRange()){
            if(this.getProvider() != null){
                return this.getProvider().getEnergyStored(from);
            }
            if(this.getReceiver() != null){
                return this.getReceiver().getEnergyStored(from);
            }
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from){
        if(this.isBoundThingInRange()){
            if(this.getProvider() != null){
                return this.getProvider().getMaxEnergyStored(from);
            }
            if(this.getReceiver() != null){
                return this.getReceiver().getMaxEnergyStored(from);
            }
        }
        return 0;
    }

    public IEnergyProvider getProvider(){
        if(this.boundPosition != null){
            TileEntity tile = this.worldObj.getTileEntity(this.boundPosition);
            if(tile instanceof IEnergyProvider){
                return (IEnergyProvider)tile;
            }
        }
        return null;
    }

    public IEnergyReceiver getReceiver(){
        if(this.boundPosition != null){
            TileEntity tile = this.worldObj.getTileEntity(this.boundPosition);
            if(tile instanceof IEnergyReceiver){
                return (IEnergyReceiver)tile;
            }
        }
        return null;
    }

    @Override
    public void updateEntity(){
        super.updateEntity();

        if(!this.worldObj.isRemote){
            if(this.isBoundThingInRange() && this.getProvider() != null){
                WorldUtil.pushEnergyToAllSides(this);
            }
        }
    }

    @Override
    public boolean isBoundThingInRange(){
        return super.isBoundThingInRange() && (this.worldObj.getTileEntity(this.boundPosition) instanceof IEnergyReceiver || this.worldObj.getTileEntity(this.boundPosition) instanceof IEnergyProvider);
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from){
        if(this.isBoundThingInRange()){
            if(this.getProvider() != null){
                return this.getProvider().canConnectEnergy(from);
            }
            if(this.getReceiver() != null){
                return this.getReceiver().canConnectEnergy(from);
            }
        }
        return false;
    }
}
