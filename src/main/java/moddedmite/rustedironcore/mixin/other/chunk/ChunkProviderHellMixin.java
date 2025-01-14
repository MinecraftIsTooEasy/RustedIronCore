package moddedmite.rustedironcore.mixin.other.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.Chunk;
import net.minecraft.ChunkProviderHell;
import net.minecraft.IChunkProvider;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ChunkProviderHell.class)
public abstract class ChunkProviderHellMixin implements IChunkProvider {
    @Shadow
    private World worldObj;

    @Shadow
    private Random hellRNG;

    @Inject(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/MapGenNetherBridge;generate(Lnet/minecraft/IChunkProvider;Lnet/minecraft/World;II[B)V", shift = At.Shift.AFTER))
    private void provideChunk$onStructureGenerate(int par1, int par2, CallbackInfoReturnable<Chunk> cir, @Local byte[] bytes) {
        Handlers.Structure.onStructureGenerate1(Dimension.NETHER, this, this.worldObj, par1, par2, bytes);
    }

    @Inject(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraft/MapGenNetherBridge;generateStructuresInChunk(Lnet/minecraft/World;Ljava/util/Random;II)Z", shift = At.Shift.AFTER))
    private void populate$onStructureGenerate(IChunkProvider par1IChunkProvider, int par2, int par3, CallbackInfo ci) {
        Handlers.Structure.onStructureGenerate2(Dimension.NETHER, this.worldObj, this.hellRNG, par2, par3);
    }

    @Inject(method = "recreateStructures", at = @At(value = "INVOKE", target = "Lnet/minecraft/MapGenNetherBridge;generate(Lnet/minecraft/IChunkProvider;Lnet/minecraft/World;II[B)V", shift = At.Shift.AFTER))
    private void recreateStructures$onStructureGenerate(int par1, int par2, CallbackInfo ci) {
        Handlers.Structure.onStructureGenerate1(Dimension.NETHER, this, this.worldObj, par1, par2, null);
    }
}
