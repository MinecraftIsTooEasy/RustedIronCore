package moddedmite.rustedironcore.mixin.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.Chunk;
import net.minecraft.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkProviderServer.class)
public class ChunkProviderServerMixin {
    @Inject(method = "loadChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/Chunk;onChunkLoad()V"))
    private void onChunkLoad(int par1, int par2, CallbackInfoReturnable<Chunk> cir, @Local Chunk chunk) {
        Handlers.ChunkLoad.onServerChunkLoad(chunk);
    }

    @Inject(method = "unloadQueuedChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/Chunk;onChunkUnload()V", shift = At.Shift.AFTER))
    private void onChunkUnload(CallbackInfoReturnable<Boolean> cir, @Local Chunk chunk) {
        Handlers.ChunkLoad.onServerChunkUnload(chunk);
    }
}
