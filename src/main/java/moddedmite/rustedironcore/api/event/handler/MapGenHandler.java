package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.MapGenRegisterEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.IChunkProvider;
import net.minecraft.MapGenBase;
import net.minecraft.MapGenStructure;
import net.minecraft.World;

import java.util.*;

public class MapGenHandler extends EventHandler<MapGenRegisterEvent> {
    private final Map<Dimension, List<MapGenBase>> MAP_GEN_MAP = new HashMap<>();
    private final Map<Dimension, List<MapGenStructure>> STRUCTURE_MAP = new HashMap<>();

    public void register(Dimension dimension, MapGenBase mapGen) {
        MAP_GEN_MAP.computeIfAbsent(dimension, k -> new ArrayList<>())
                .add(mapGen);
    }

    public void registerStructure(Dimension dimension, MapGenStructure structure) {
        STRUCTURE_MAP.computeIfAbsent(dimension, k -> new ArrayList<>())
                .add(structure);
    }

    public void onChunkProvideMapGen(Dimension dimension,
                                     IChunkProvider chunkProvider,
                                     World world,
                                     int chunkX,
                                     int chunkZ,
                                     byte[] blocks
    ) {
        List<MapGenBase> list = MAP_GEN_MAP.get(dimension);
        if (list == null) return;
        list.forEach(x -> x.generate(chunkProvider, world, chunkX, chunkZ, blocks));
    }

    public void onChunkProvideStructures(Dimension dimension,
                                         IChunkProvider chunkProvider,
                                         World world,
                                         int chunkX,
                                         int chunkZ,
                                         byte[] blocks
    ) {
        List<MapGenStructure> list = STRUCTURE_MAP.get(dimension);
        if (list == null) return;
        list.forEach(x -> x.generate(chunkProvider, world, chunkX, chunkZ, blocks));
    }

    public void onRecreateStructures(Dimension dimension,
                                     IChunkProvider chunkProvider,
                                     World world,
                                     int chunkX,
                                     int chunkZ
    ) {
        List<MapGenBase> list = this.MAP_GEN_MAP.get(dimension);
        if (list == null) return;
        list.stream().filter(x -> x instanceof MapGenStructure)
                .forEach(x -> x.generate(chunkProvider, world, chunkX, chunkZ, null));
    }

    public void onChunkPopulateStructures(Dimension dimension,
                                          World world,
                                          Random rand,
                                          int chunkX,
                                          int chunkZ
    ) {
        List<MapGenStructure> list = this.STRUCTURE_MAP.get(dimension);
        if (list == null) return;
        list.forEach(x -> x.generateStructuresInChunk(world, rand, chunkX, chunkZ));
    }
}
