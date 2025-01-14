package moddedmite.rustedironcore.mixin.other.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.Chunk;
import net.minecraft.ChunkProviderEnd;
import net.minecraft.IChunkProvider;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ChunkProviderEnd.class)
public abstract class ChunkProviderEndMixin implements IChunkProvider {
    @Shadow
    private World endWorld;

    @Shadow
    private Random endRNG;

    @Inject(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/ChunkProviderEnd;replaceBlocksForBiome(II[B[Lnet/minecraft/BiomeGenBase;)V", shift = At.Shift.AFTER))
    private void provideChunk$onStructureGenerate(int par1, int par2, CallbackInfoReturnable<Chunk> cir, @Local byte[] bytes) {
        Handlers.Structure.onStructureGenerate1(Dimension.END, this, this.endWorld, par1, par2, bytes);
    }

    @Inject(method = "populate", at = @At(value = "FIELD", target = "Lnet/minecraft/BlockFalling;fallInstantly:Z", ordinal = 0, shift = At.Shift.AFTER))
    private void populate$onStructureGenerate(IChunkProvider par1IChunkProvider, int par2, int par3, CallbackInfo ci) {
        Handlers.Structure.onStructureGenerate2(Dimension.END, this.endWorld, this.endRNG, par2, par3);
    }

    @Inject(method = "recreateStructures", at = @At(value = "RETURN"))
    private void recreateStructures$onStructureGenerate(int par1, int par2, CallbackInfo ci) {
        Handlers.Structure.onStructureGenerate1(Dimension.END, this, this.endWorld, par1, par2, null);
    }
}
