package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.StructureRegisterEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.IChunkProvider;
import net.minecraft.MapGenStructure;
import net.minecraft.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Deprecated(since = "1.4.1")
public class StructureHandler extends EventHandler<StructureRegisterEvent> {
    public final Map<Dimension, List<MapGenStructure>> STRUCTURE_MAP = new HashMap<>();

    /**
     * The bytes are called ChunkPrimer in 1.12, contains the data of the chunk.
     */
    public void onStructureGenerate1(Dimension dimension, IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, @Nullable byte[] bytes) {
        Handlers.MapGen.onChunkProvideStructures(dimension, chunkProvider, world, chunkX, chunkZ, bytes);
    }

    public void onStructureGenerate2(Dimension dimension, World world, Random rand, int chunkX, int chunkZ) {
        Handlers.MapGen.onChunkPopulateStructures(dimension, world, rand, chunkX, chunkZ);
    }

}
