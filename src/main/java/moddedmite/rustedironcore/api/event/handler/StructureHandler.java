package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.StructureRegisterEvent;
import net.minecraft.IChunkProvider;
import net.minecraft.MapGenStructure;
import net.minecraft.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StructureHandler extends EventHandler<StructureRegisterEvent> {
    public List<MapGenStructure> structures = new ArrayList<>();

    // the bytes are called ChunkPrimer in 1.12, contains the data of the chunk.
    public void onStructureGenerate1(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, @Nullable byte[] bytes) {
        this.structures.forEach(x -> x.generate(chunkProvider, world, chunkX, chunkZ, bytes));
    }

    public void onStructureGenerate2(World world, Random rand, int chunkX, int chunkZ) {
        this.structures.forEach(x -> x.generateStructuresInChunk(world, rand, chunkX, chunkZ));
    }

}
