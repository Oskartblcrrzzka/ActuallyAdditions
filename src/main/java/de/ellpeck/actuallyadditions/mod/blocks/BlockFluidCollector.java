/*
 * This file ("BlockFluidCollector.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.blocks;


import de.ellpeck.actuallyadditions.mod.ActuallyAdditions;
import de.ellpeck.actuallyadditions.mod.blocks.base.BlockContainerBase;
import de.ellpeck.actuallyadditions.mod.inventory.GuiHandler;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityFluidCollector;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityFluidPlacer;
import de.ellpeck.actuallyadditions.mod.util.PosUtil;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFluidCollector extends BlockContainerBase{

    private static final PropertyInteger META = PropertyInteger.create("meta", 0, 5);

    private final boolean isPlacer;

    public BlockFluidCollector(boolean isPlacer, String name){
        super(Material.ROCK, name);
        this.isPlacer = isPlacer;
        this.setHarvestLevel("pickaxe", 0);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
    }


    @Override
    public TileEntity createNewTileEntity(World world, int par2){
        return this.isPlacer ? new TileEntityFluidPlacer() : new TileEntityFluidCollector();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing par6, float par7, float par8, float par9){
        if(this.tryToggleRedstone(world, pos, player)){
            return true;
        }
        if(!world.isRemote){
            TileEntityFluidCollector collector = (TileEntityFluidCollector)world.getTileEntity(pos);
            if(collector != null){
                if(this.checkFailUseItemOnTank(player, stack, collector.tank)){
                    player.openGui(ActuallyAdditions.instance, GuiHandler.GuiTypes.FLUID_COLLECTOR.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack){
        return EnumRarity.RARE;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack){
        int rotation = BlockPistonBase.getFacingFromEntity(pos, player).ordinal();
        PosUtil.setMetadata(pos, world, rotation, 2);

        super.onBlockPlacedBy(world, pos, state, player, stack);
    }

    @Override
    protected PropertyInteger getMetaProperty(){
        return META;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state){
        this.dropInventory(world, pos);
        super.breakBlock(world, pos, state);
    }
}
