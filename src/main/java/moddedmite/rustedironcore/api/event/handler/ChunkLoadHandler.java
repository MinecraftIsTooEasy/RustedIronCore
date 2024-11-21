package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IChunkLoadListener;
import net.minecraft.Chunk;

public class ChunkLoadHandler extends AbstractHandler<IChunkLoadListener> {
    public void onClientChunkLoad(Chunk chunk) {
        this.listeners.forEach(x -> x.onClientChunkLoad(chunk));
    }

    public void onClientChunkUnload(Chunk chunk) {
        this.listeners.forEach(x -> x.onClientChunkUnload(chunk));
    }

    public void onServerChunkLoad(Chunk chunk) {
        this.listeners.forEach(x -> x.onServerChunkLoad(chunk));
    }

    public void onServerChunkUnload(Chunk chunk) {
        this.listeners.forEach(x -> x.onServerChunkUnload(chunk));
    }
}
