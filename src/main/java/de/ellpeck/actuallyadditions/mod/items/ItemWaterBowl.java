/*
 * This file ("ItemWaterBowl.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.items;

import de.ellpeck.actuallyadditions.mod.items.base.ItemBase;
import de.ellpeck.actuallyadditions.mod.util.PosUtil;
import de.ellpeck.actuallyadditions.mod.util.WorldUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemWaterBowl extends ItemBase{

    public ItemWaterBowl(String name){
        super(name);
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand){
        RayTraceResult trace = WorldUtil.getNearestBlockWithDefaultReachDistance(world, player);
        ActionResult<ItemStack> result = ForgeEventFactory.onBucketUse(player, world, stack, trace);
        if(result != null){
            return result;
        }

        if(trace == null){
            return new ActionResult(EnumActionResult.PASS, stack);
        }
        else if(trace.typeOfHit != RayTraceResult.Type.BLOCK){
            return new ActionResult(EnumActionResult.PASS, stack);
        }
        else{
            BlockPos pos = trace.getBlockPos();

            if(!world.isBlockModifiable(player, pos)){
                return new ActionResult(EnumActionResult.FAIL, stack);
            }
            else{
                BlockPos pos1 = PosUtil.getBlock(pos, world).isReplaceable(world, pos) && trace.sideHit == EnumFacing.UP ? pos : pos.offset(trace.sideHit);

                if(!player.canPlayerEdit(pos1, trace.sideHit, stack)){
                    return new ActionResult(EnumActionResult.FAIL, stack);
                }
                else if(this.tryPlaceContainedLiquid(player, world, pos1)){
                    return !player.capabilities.isCreativeMode ? new ActionResult(EnumActionResult.SUCCESS, new ItemStack(Items.BOWL)) : new ActionResult(EnumActionResult.SUCCESS, stack);
                }
                else{
                    return new ActionResult(EnumActionResult.FAIL, stack);
                }
            }
        }
    }

    public boolean tryPlaceContainedLiquid(EntityPlayer player, World world, BlockPos pos){
        IBlockState state = world.getBlockState(pos);
        Material material = state.getMaterial();
        boolean nonSolid = !material.isSolid();
        boolean replaceable = state.getBlock().isReplaceable(world, pos);

        if(!world.isAirBlock(pos) && !nonSolid && !replaceable){
            return false;
        }
        else{
            if(world.provider.doesWaterVaporize()){
                world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F+(world.rand.nextFloat()-world.rand.nextFloat())*0.8F);

                for(int k = 0; k < 8; k++){
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)pos.getX()+Math.random(), (double)pos.getY()+Math.random(), (double)pos.getZ()+Math.random(), 0.0D, 0.0D, 0.0D);
                }
            }
            else{
                if(!world.isRemote && (nonSolid || replaceable) && !material.isLiquid()){
                    world.destroyBlock(pos, true);
                }

                world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState(), 3);
            }

            return true;
        }
    }
}
