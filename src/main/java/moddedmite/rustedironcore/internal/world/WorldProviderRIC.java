package moddedmite.rustedironcore.internal.world;

import net.minecraft.ChunkCoordinates;
import net.minecraft.WorldProvider;

public class WorldProviderRIC extends WorldProvider {
    public WorldProviderRIC(int dimension_id, String name) {
        super(dimension_id, name);
    }

    @Override
    public ChunkCoordinates getEntrancePortalLocation() {
        return new ChunkCoordinates(100, 50, 0);
    }
}
