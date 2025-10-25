package moddedmite.rustedironcore.api.world;

import net.minecraft.*;

public class WorldTypeExtra extends WorldType {
    private final IChunkProvider chunkProvider;
    private final WorldChunkManager chunkManager;
    public static World world;
    public static long seed;

    public WorldTypeExtra(int i, String string, IChunkProvider chunkProvider, WorldChunkManager chunkManager) {
        super(i, string);
        this.chunkProvider = chunkProvider;
        this.chunkManager = chunkManager;
        world = Minecraft.getMinecraft().theWorld;
        seed = world.getSeed();
    }

    public IChunkProvider getChunkProvider() {
        return chunkProvider;
    }

    public WorldChunkManager getChunkManager() {
        return chunkManager;
    }
}
