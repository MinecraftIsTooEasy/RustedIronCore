package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
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

public class StructureHandler extends EventHandler<StructureRegisterEvent> {
    public final Map<Dimension, List<MapGenStructure>> STRUCTURE_MAP = new HashMap<>();

    /**
     * The bytes are called ChunkPrimer in 1.12, contains the data of the chunk.
     */
    public void onStructureGenerate1(Dimension dimension, IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, @Nullable byte[] bytes) {
        List<MapGenStructure> list = this.STRUCTURE_MAP.get(dimension);
        if (list == null) return;
        list.forEach(x -> x.generate(chunkProvider, world, chunkX, chunkZ, bytes));
    }

    public void onStructureGenerate2(Dimension dimension, World world, Random rand, int chunkX, int chunkZ) {
        List<MapGenStructure> list = this.STRUCTURE_MAP.get(dimension);
        if (list == null) return;
        list.forEach(x -> x.generateStructuresInChunk(world, rand, chunkX, chunkZ));
    }

}
