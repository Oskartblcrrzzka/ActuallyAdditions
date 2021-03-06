/*
 * This file ("ContainerInputter.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.inventory;

import de.ellpeck.actuallyadditions.mod.inventory.gui.GuiInputter;
import de.ellpeck.actuallyadditions.mod.inventory.slot.SlotFilter;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityBase;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityInputter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class ContainerInputter extends Container{

    private final TileEntityInputter tileInputter;

    private final boolean isAdvanced;

    public ContainerInputter(InventoryPlayer inventory, TileEntityBase tile, boolean isAdvanced){
        this.tileInputter = (TileEntityInputter)tile;
        this.isAdvanced = isAdvanced;

        this.addSlotToContainer(new Slot(this.tileInputter, 0, 80, 21+(isAdvanced ? 12 : 0)));

        if(isAdvanced){
            for(int i = 0; i < 2; i++){
                for(int x = 0; x < 3; x++){
                    for(int y = 0; y < 4; y++){
                        this.addSlotToContainer(new SlotFilter(this.tileInputter, 1+y+x*4+i*12, 20+i*84+x*18, 6+y*18));
                    }
                }
            }
        }

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; j++){
                this.addSlotToContainer(new Slot(inventory, j+i*9+9, 8+j*18, 101+i*18+(isAdvanced ? GuiInputter.OFFSET_ADVANCED : 0)));
            }
        }
        for(int i = 0; i < 9; i++){
            this.addSlotToContainer(new Slot(inventory, i, 8+i*18, 159+(isAdvanced ? GuiInputter.OFFSET_ADVANCED : 0)));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot){
        final int inventoryStart = this.isAdvanced ? 25 : 1;
        final int inventoryEnd = inventoryStart+26;
        final int hotbarStart = inventoryEnd+1;
        final int hotbarEnd = hotbarStart+8;

        Slot theSlot = this.inventorySlots.get(slot);

        if(theSlot != null && theSlot.getHasStack()){
            ItemStack newStack = theSlot.getStack();
            ItemStack currentStack = newStack.copy();

            //Other Slots in Inventory excluded
            if(slot >= inventoryStart){
                //Shift from Inventory
                if(!this.mergeItemStack(newStack, 0, 1, false)){
                    //
                    if(slot >= inventoryStart && slot <= inventoryEnd){
                        if(!this.mergeItemStack(newStack, hotbarStart, hotbarEnd+1, false)){
                            return null;
                        }
                    }
                    else if(slot >= inventoryEnd+1 && slot < hotbarEnd+1 && !this.mergeItemStack(newStack, inventoryStart, inventoryEnd+1, false)){
                        return null;
                    }
                }
            }
            else if(!this.mergeItemStack(newStack, inventoryStart, hotbarEnd+1, false)){
                return null;
            }

            if(newStack.stackSize <= 0){
                theSlot.putStack(null);
            }
            else{
                theSlot.onSlotChanged();
            }

            if(newStack.stackSize == currentStack.stackSize){
                return null;
            }
            theSlot.onPickupFromSlot(player, newStack);

            return currentStack;
        }
        return null;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        if(slotId >= 0 && slotId < this.inventorySlots.size() && this.getSlot(slotId) instanceof SlotFilter){
            //Calls the Filter's SlotClick function
            return ((SlotFilter)this.getSlot(slotId)).slotClick(player);
        }
        else{
            return super.slotClick(slotId, dragType, clickTypeIn, player);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return this.tileInputter.isUseableByPlayer(player);
    }
}