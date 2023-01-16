package teksturepako.greenery.common.block.plant.upland.tall

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import teksturepako.greenery.Greenery
import teksturepako.greenery.common.block.plant.GreeneryPlantBase
import teksturepako.greenery.common.util.DropsUtil
import java.util.*

abstract class AbstractTallPlant(name: String, doHarm: Boolean) : GreeneryPlantBase(false, doHarm)
{
    companion object
    {
        val ALLOWED_SOILS = setOf<Material>(Material.GRASS)
        val GRASS_TOP_AABB = arrayOf(
            AxisAlignedBB(0.10, 0.025, 0.10, 0.9, 0.50, 0.9),
            AxisAlignedBB(0.10, 0.025, 0.10, 0.9, 0.625, 0.9),
            AxisAlignedBB(0.10, 0.025, 0.10, 0.9, 0.75, 0.9),
            AxisAlignedBB(0.10, 0.025, 0.10, 0.9, 0.875, 0.9)
        )
        val GRASS_BOTTOM_AABB = arrayOf(
            AxisAlignedBB(0.10, 0.025, 0.10, 0.9, 1.0, 0.9),
            AxisAlignedBB(0.10, 0.025, 0.10, 0.9, 1.0, 0.9),
            AxisAlignedBB(0.10, 0.025, 0.10, 0.9, 1.0, 0.9),
            AxisAlignedBB(0.10, 0.025, 0.10, 0.9, 1.0, 0.9)
        )
    }

    abstract val drops: MutableList<String>

    init
    {
        setRegistryName("plant/upland/tall/$name")
        translationKey = name
        soundType = SoundType.PLANT
        creativeTab = Greenery.creativeTab
    }

    override fun canBlockStay(worldIn: World, pos: BlockPos, state: IBlockState): Boolean
    {
        val down = worldIn.getBlockState(pos.down())
        val down2 = worldIn.getBlockState(pos.down(2))

        return if (worldIn.isAirBlock(pos) || worldIn.getBlockState(pos).block == this)
        {
            ((down.material in ALLOWED_SOILS || down.block == Blocks.DIRT) || (down.block == this && getAge(down) == this.maxAge && down2.material in ALLOWED_SOILS))
        }
        else false
    }

    override fun canGrow(worldIn: World, pos: BlockPos, state: IBlockState, isClient: Boolean): Boolean
    {
        return when
        {
            worldIn.getBlockState(pos.up()).block == this   -> false
            worldIn.getBlockState(pos.down()).block == this -> (getAge(state) < this.maxAge)
            else                                            -> true
        }
    }

    override fun grow(worldIn: World, pos: BlockPos, state: IBlockState)
    {
        var newAge = getAge(state) + getBonemealAgeIncrease(worldIn)
        val maxAge = this.maxAge
        if (newAge > maxAge)
        {
            newAge = maxAge
            if (worldIn.isAirBlock(pos.up()) && canBlockStay(worldIn, pos.up(), state))
            {
                worldIn.setBlockState(pos.up(), withAge(0), 2)
            }
        }
        worldIn.setBlockState(pos, withAge(newAge), 2)
    }

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int)
    {
        DropsUtil.getDrops(this.drops, drops, world, pos, state, this.seed, fortune)
    }

    override fun quantityDroppedWithBonus(fortune: Int, random: Random): Int
    {
        return 1 + random.nextInt(fortune * 2 + 1)
    }

    override fun harvestBlock(worldIn: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack)
    {
        if (!worldIn.isRemote && stack.item === Items.SHEARS)
        {
            StatList.getBlockStats(this)?.let { player.addStat(it) }
            spawnAsEntity(worldIn, pos, ItemStack(this, 1))
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, te, stack)
        }
    }
}