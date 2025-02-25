package moddedmite.rustedironcore.api.event.handler;

import com.google.common.collect.HashBiMap;
import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.DimensionRegisterEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import moddedmite.rustedironcore.api.world.DimensionContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.WorldServer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DimensionHandler extends EventHandler<DimensionRegisterEvent> {
    private final Map<Dimension, DimensionContext> contextMap = new HashMap<>();
    /**
     * Indexes in the array {@link MinecraftServer#worldServers}, always positive integers.
     * <li>overworld: 0</li>
     * <li>nether: 1</li>
     * <li>end: 2</li>
     * <li>underworld: 3</li>
     */
    private int index = 4;
    private final HashBiMap<Dimension, Integer> indexMap = HashBiMap.create();
    private final Map<Integer, Dimension> indexMapInverse = this.indexMap.inverse();

    public DimensionHandler() {
        this.indexMap.put(Dimension.OVERWORLD, 0);
        this.indexMap.put(Dimension.NETHER, 1);
        this.indexMap.put(Dimension.END, 2);
        this.indexMap.put(Dimension.UNDERWORLD, 3);
    }

    @ApiStatus.Internal
    public void registerDimensionInternal(Dimension dimension, DimensionContext context) {
        this.contextMap.put(dimension, context);
        this.indexMap.put(dimension, this.index);
        this.index++;
    }

    public Optional<Dimension> parseDimensionId(int dimensionId) {
        return Optional.ofNullable(Dimension.fromId(dimensionId));
    }

    public Optional<DimensionContext> getDimensionContext(int dimensionId) {
        return parseDimensionId(dimensionId).flatMap(this::getDimensionContext);
    }

    public Optional<DimensionContext> getDimensionContext(Dimension dimension) {
        return Optional.ofNullable(this.contextMap.get(dimension));
    }

    public int getTotalDimensionsCount() {
        return this.contextMap.size() + 4;
    }

    public int getCustomDimensionsCount() {
        return this.contextMap.size();
    }

    public int getIndex(Dimension dimension) {
        Integer i = this.indexMap.get(dimension);
        if (i == null) throw new IllegalArgumentException("unregistered dimension: " + dimension);
        return i;
    }

    public Optional<Dimension> parseIndex(int index) {
        return Optional.ofNullable(this.indexMapInverse.get(index));
    }

    @Environment(EnvType.SERVER)
    public WorldServer getWorldServer(MinecraftServer server, Dimension dimension) {
        Integer index = this.indexMap.get(dimension);
        if (index == null) throw new IllegalArgumentException("unregistered dimension: " + dimension);
        return server.worldServers[index];
    }

}
