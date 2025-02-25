package moddedmite.rustedironcore.mixin.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.Chunk;
import net.minecraft.ChunkProviderClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkProviderClient.class)
public class ChunkProviderClientMixin {
    @Inject(method = "loadChunk", at = @At("RETURN"))
    private void onChunkLoad(int par1, int par2, CallbackInfoReturnable<Chunk> cir) {
        Handlers.ChunkLoad.onClientChunkLoad(cir.getReturnValue());
    }

    @Inject(method = "unloadChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/Chunk;onChunkUnload()V", shift = At.Shift.AFTER))
    private void onChunkUnload(int par1, int par2, CallbackInfo ci, @Local Chunk chunk) {
        Handlers.ChunkLoad.onClientChunkUnload(chunk);
    }
}
