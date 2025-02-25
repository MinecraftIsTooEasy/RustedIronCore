package moddedmite.rustedironcore.mixin.dimension.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin {
    @Mutable
    @Shadow
    @Final
    private boolean has_skylight;

    @Inject(method = {"<init>(Lnet/minecraft/ISaveHandler;Ljava/lang/String;Lnet/minecraft/WorldSettings;Lnet/minecraft/WorldProvider;Lnet/minecraft/Profiler;Lnet/minecraft/ILogAgent;)V"},
            at = @At("RETURN"))
    private void onInit(ISaveHandler par1ISaveHandler, String par2Str, WorldSettings par3WorldSettings, WorldProvider par4WorldProvider, Profiler par5Profiler, ILogAgent par6ILogAgent, CallbackInfo ci) {
        this.handleSkyLight(par4WorldProvider.dimensionId);
    }

    @Inject(method = "<init>(Lnet/minecraft/ISaveHandler;Ljava/lang/String;Lnet/minecraft/WorldProvider;Lnet/minecraft/WorldSettings;Lnet/minecraft/Profiler;Lnet/minecraft/ILogAgent;JJ)V",
            at = @At("RETURN"))
    private void onInit2(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent, long world_creation_time, long total_world_time, CallbackInfo ci) {
        this.handleSkyLight(par3WorldProvider.dimensionId);
    }

    @Unique
    private void handleSkyLight(int dimensionId) {
        Handlers.Dimension.getDimensionContext(dimensionId).ifPresent(x -> {
            this.has_skylight = x.hasSkyLight();
        });
    }

    // TODO don't know how it arise
    @WrapOperation(method = "computeLightValue", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;setErrorMessage(Ljava/lang/String;Z)V"))
    private void mute(String text, boolean echo_to_err, Operation<Void> original) {
    }
}
