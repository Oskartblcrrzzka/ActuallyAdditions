/*
 * This file ("TileEntityXPSolidifier.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.tile;


import de.ellpeck.actuallyadditions.mod.items.InitItems;
import de.ellpeck.actuallyadditions.mod.items.ItemSolidifiedExperience;
import de.ellpeck.actuallyadditions.mod.network.gui.IButtonReactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityXPSolidifier extends TileEntityInventoryBase implements IButtonReactor{

    private final int[] buttonAmounts = new int[]{1, 5, 10, 20, 30, 40, 50, 64, -999};
    public short amount;
    private short lastAmount;

    public TileEntityXPSolidifier(){
        super(1, "xpSolidifier");
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, boolean sync){
        super.writeSyncableNBT(compound, sync);
        compound.setShort("Amount", this.amount);
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, boolean sync){
        super.readSyncableNBT(compound, sync);
        this.amount = compound.getShort("Amount");
    }

    @Override
    public void updateEntity(){
        super.updateEntity();
        if(!this.worldObj.isRemote){
            if(this.amount > 0){
                if(this.slots[0] == null){
                    int toSet = this.amount > 64 ? 64 : this.amount;
                    this.slots[0] = new ItemStack(InitItems.itemSolidifiedExperience, toSet);
                    this.amount -= toSet;
                }
                else if(this.slots[0].stackSize < 64){
                    int needed = 64-this.slots[0].stackSize;
                    int toAdd = this.amount > needed ? needed : this.amount;
                    this.slots[0].stackSize += toAdd;
                    this.amount -= toAdd;
                }
            }

            if(this.lastAmount != this.amount && this.sendUpdateWithInterval()){
                this.lastAmount = this.amount;
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
        return true;
    }

    @Override
    public void onButtonPressed(int buttonID, EntityPlayer player){
        if(buttonID < this.buttonAmounts.length){
            if(this.getPlayerXP(player) > 0){
                int xp = this.buttonAmounts[buttonID] == -999 ? this.getPlayerXP(player)/ItemSolidifiedExperience.SOLID_XP_AMOUNT : this.buttonAmounts[buttonID];
                if(this.amount < Short.MAX_VALUE-xp && this.getPlayerXP(player) >= ItemSolidifiedExperience.SOLID_XP_AMOUNT*xp){
                    this.addPlayerXP(player, -(ItemSolidifiedExperience.SOLID_XP_AMOUNT*xp));
                    if(!this.worldObj.isRemote){
                        this.amount += xp;
                    }
                }
            }
        }
    }

    //TODO Fix XP System to fit points needed in 1.8 (OpenBlocks?)

    /**
     * Gets the Player's XP
     * (Excerpted from OpenBlocks' XP system with permission, thanks guys!)
     *
     * @param player The Player
     * @return The XP
     */
    private int getPlayerXP(EntityPlayer player){
        return (int)(this.getExperienceForLevel(player.experienceLevel)+(player.experience*player.xpBarCap()));
    }

    /**
     * Adds (or removes, if negative) a certain amount of XP from a player
     * (Excerpted from OpenBlocks' XP system with permission, thanks guys!)
     *
     * @param player The Player
     * @param amount The Amount
     */
    private void addPlayerXP(EntityPlayer player, int amount){
        int experience = this.getPlayerXP(player)+amount;
        player.experienceTotal = experience;

        int level = 0;
        while(this.getExperienceForLevel(level) <= experience){
            level++;
        }
        player.experienceLevel = level-1;

        int expForLevel = this.getExperienceForLevel(player.experienceLevel);
        player.experience = (float)(experience-expForLevel)/(float)player.xpBarCap();
    }

    /**
     * Gets the amount of experience a certain level contains
     * (Excerpted from OpenBlocks' XP system with permission, thanks guys!)
     *
     * @param level The Level in question
     * @return The total XP the level has
     */
    private int getExperienceForLevel(int level){
        if(level > 0){
            if(level > 0 && level < 16){
                return level*17;
            }
            else if(level > 15 && level < 31){
                return (int)(1.5*(level*level)-29.5*level+360);
            }
            else{
                return (int)(3.5*(level*level)-151.5*level+2220);
            }
        }
        return 0;
    }
}
