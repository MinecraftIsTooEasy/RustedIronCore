package moddedmite.rustedironcore.mixin.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.Chunk;
import net.minecraft.ChunkProviderUnderworld;
import net.minecraft.IChunkProvider;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ChunkProviderUnderworld.class)
public abstract class ChunkProviderUnderworldMixin implements IChunkProvider {
    @Shadow
    private World worldObj;

    @Shadow
    private Random hellRNG;

    @Inject(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/ChunkProviderGenerate;placeRandomCobwebs(II[BLjava/util/Random;)V"))
    private void provideChunk$onMapGen(int par1, int par2, CallbackInfoReturnable<Chunk> cir, @Local byte[] bytes) {
        Handlers.MapGen.onChunkProvideMapGen(Dimension.UNDERWORLD, this, this.worldObj, par1, par2, bytes);
    }

    @Inject(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/ChunkProviderGenerate;placeRandomCobwebs(II[BLjava/util/Random;)V", shift = At.Shift.AFTER))
    private void provideChunk$onStructureGenerate(int par1, int par2, CallbackInfoReturnable<Chunk> cir, @Local byte[] bytes) {
        Handlers.MapGen.onChunkProvideStructures(Dimension.UNDERWORLD, this, this.worldObj, par1, par2, bytes);
    }

    @Inject(method = "populate", at = @At(value = "FIELD", target = "Lnet/minecraft/BlockFalling;fallInstantly:Z", ordinal = 0, shift = At.Shift.AFTER))
    private void populate$onStructureGenerate(IChunkProvider par1IChunkProvider, int par2, int par3, CallbackInfo ci) {
        Handlers.MapGen.onChunkPopulateStructures(Dimension.UNDERWORLD, this.worldObj, this.hellRNG, par2, par3);
    }

    @Inject(method = "recreateStructures", at = @At(value = "RETURN"))
    private void recreateStructures$onStructureGenerate(int par1, int par2, CallbackInfo ci) {
        Handlers.MapGen.onRecreateStructures(Dimension.UNDERWORLD, this, this.worldObj, par1, par2);
    }
}
