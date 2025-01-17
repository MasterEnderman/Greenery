package teksturepako.greenery.common.worldGen

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldType
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import teksturepako.greenery.common.block.plant.GreeneryPlant
import teksturepako.greenery.common.block.plant.upland.tall.TallUplandPlant
import teksturepako.greenery.common.config.Config
import teksturepako.greenery.common.util.WorldGenUtil
import java.util.*

class PlantGenerator(override val plant: GreeneryPlant) : IPlantGenerator
{
    override fun generate(
        rand: Random,
        chunkX: Int,
        chunkZ: Int,
        world: World,
        chunkGenerator: IChunkGenerator,
        chunkProvider: IChunkProvider
    )
    {
        val random = world.rand
        val chunkPos = world.getChunk(chunkX, chunkZ).pos
        val biome = WorldGenUtil.getBiomeInChunk(world, chunkX, chunkZ)
        val dimension = world.provider.dimension
        val genModifier = if (plant is TallUplandPlant) 4 else 1

        // Handle super-flat worlds
        if (!Config.global.genInSuperflat && world.worldType == WorldType.FLAT) return

        // Gets worldGen configuration from the block
        for (input in plant.worldGen)
        {
            // New instance of the worldGen parser class
            val parser = WorldGenParser(currentConfig = input, allConfigs = plant.worldGen)

            // Check if plants can generate
            if (random.nextDouble() >= parser.generationChance || !parser.canGenerate(biome, dimension)) continue

            // Generate patches of the plant
            for (i in 0..parser.patchAttempts(multiplyBy = genModifier))
            {
                val x = random.nextInt(16) + 8
                val z = random.nextInt(16) + 8

                val yRange = world.getHeight(chunkPos.getBlock(0, 0, 0).add(x, 0, z)).y + 32
                val y = random.nextInt(yRange)

                val pos = chunkPos.getBlock(0, 0, 0).add(x, y, z)
                generatePlants(parser.plantAttempts, world, random, pos, 2)
            }
        }
    }

    override fun generatePlants(plantAttempts: Int, world: World, rand: Random, targetPos: BlockPos, flags: Int)
    {
        for (i in 0..plantAttempts)
        {
            val pos = targetPos.add(
                rand.nextInt(8) - rand.nextInt(8),
                rand.nextInt(4) - rand.nextInt(4),
                rand.nextInt(8) - rand.nextInt(8)
            )

            if (!world.isBlockLoaded(pos)) continue

            plant.placePlant(world, pos, rand, flags)
        }
    }
}