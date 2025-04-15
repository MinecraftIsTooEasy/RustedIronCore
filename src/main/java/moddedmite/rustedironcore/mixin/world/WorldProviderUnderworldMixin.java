package moddedmite.rustedironcore.mixin.world;

import moddedmite.rustedironcore.internal.delegate.world.ChunkProviderUnderworldDelegate;
import moddedmite.rustedironcore.internal.delegate.world.WorldChunkManagerUnderworldDelegate;
import net.minecraft.IChunkProvider;
import net.minecraft.WorldProvider;
import net.minecraft.WorldProviderUnderworld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProviderUnderworld.class)
public abstract class WorldProviderUnderworldMixin extends WorldProvider {
    public WorldProviderUnderworldMixin(int dimension_id, String name) {
        super(dimension_id, name);
    }

    @Inject(method = "registerWorldChunkManager", at = @At("RETURN"))
    private void onWorldChunkManagerRegister(CallbackInfo ci) {
        this.worldChunkMgr = new WorldChunkManagerUnderworldDelegate(this.worldObj);
    }

    @Inject(method = "createChunkGenerator", at = @At("HEAD"), cancellable = true)
    private void onChunkGeneratorCreate(CallbackInfoReturnable<IChunkProvider> cir){
        cir.setReturnValue(new ChunkProviderUnderworldDelegate(this.worldObj, this.worldObj.getSeed()));
    }
}
