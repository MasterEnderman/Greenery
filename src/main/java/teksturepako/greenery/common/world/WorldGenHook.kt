package teksturepako.greenery.common.world

import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import teksturepako.greenery.Greenery
import teksturepako.greenery.common.config.Config
import teksturepako.greenery.common.util.WorldGenUtil.removeUnwantedBOPGenerators
import teksturepako.greenery.common.world.gen.IPlantGenerator
import java.util.*

internal class WorldGenHook : IWorldGenerator
{
    override fun generate(rand: Random, chunkX: Int, chunkZ: Int, world: World, chunkGen: IChunkGenerator, chunkProv: IChunkProvider)
    {
        val generators: MutableList<IPlantGenerator> = Greenery.loadGenerators(true)
        for (generator in generators) generator.generate(rand, chunkX, chunkZ, world, chunkGen, chunkProv)

        if (Config.global.removeGrass) removeUnwantedBOPGenerators(world)
    }
}