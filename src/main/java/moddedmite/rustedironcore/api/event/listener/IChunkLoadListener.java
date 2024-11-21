package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.Chunk;

public interface IChunkLoadListener {
    default void onClientChunkLoad(Chunk chunk) {
    }

    default void onClientChunkUnload(Chunk chunk) {
    }

    default void onServerChunkLoad(Chunk chunk) {
    }

    default void onServerChunkUnload(Chunk chunk) {
    }
}
