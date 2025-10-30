package moddedmite.rustedironcore.mixin.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.MapGenRegisterEvent;
import moddedmite.rustedironcore.api.event.events.StructureRegisterEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.Chunk;
import net.minecraft.ChunkProviderGenerate;
import net.minecraft.IChunkProvider;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ChunkProviderGenerate.class)
public abstract class ChunkProviderGenerateMixin implements IChunkProvider {
    @Shadow
    private World worldObj;

    @Shadow
    private Random rand;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(World par1World, long par2, boolean par4, CallbackInfo ci) {
        Handlers.Structure.publish(new StructureRegisterEvent());
        Handlers.MapGen.publish(new MapGenRegisterEvent());
    }

    @Inject(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/ChunkProviderGenerate;placeRandomCobwebs(II[BLjava/util/Random;)V"))
    private void provideChunk$onMapGen(int par1, int par2, CallbackInfoReturnable<Chunk> cir, @Local byte[] bytes) {
        Handlers.MapGen.onChunkProvideMapGen(Dimension.OVERWORLD, this, this.worldObj, par1, par2, bytes);
    }

    @Inject(method = "provideChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/MapGenScatteredFeature;generate(Lnet/minecraft/IChunkProvider;Lnet/minecraft/World;II[B)V",
                    shift = At.Shift.AFTER
            )
    )
    private void provideChunk$onStructureGen(int par1, int par2, CallbackInfoReturnable<Chunk> cir, @Local byte[] bytes) {
        Handlers.MapGen.onChunkProvideStructures(Dimension.OVERWORLD, this, this.worldObj, par1, par2, bytes);
    }

    @Inject(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraft/MapGenScatteredFeature;generateStructuresInChunk(Lnet/minecraft/World;Ljava/util/Random;II)Z", shift = At.Shift.AFTER))
    private void populate$onStructureGenerate(IChunkProvider par1IChunkProvider, int par2, int par3, CallbackInfo ci) {
        Handlers.MapGen.onChunkPopulateStructures(Dimension.OVERWORLD, this.worldObj, this.rand, par2, par3);
    }

    @Inject(method = "recreateStructures", at = @At(value = "INVOKE", target = "Lnet/minecraft/MapGenScatteredFeature;generate(Lnet/minecraft/IChunkProvider;Lnet/minecraft/World;II[B)V", shift = At.Shift.AFTER))
    private void recreateStructures$onStructureGenerate(int par1, int par2, CallbackInfo ci) {
        Handlers.MapGen.onRecreateStructures(Dimension.OVERWORLD, this, this.worldObj, par1, par2);
    }
}
