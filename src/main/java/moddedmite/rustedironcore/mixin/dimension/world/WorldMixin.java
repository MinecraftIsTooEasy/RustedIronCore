package moddedmite.rustedironcore.mixin.dimension.world;

import moddedmite.rustedironcore.api.block.DynamicLightBlock;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.Chunk;
import net.minecraft.EnumSkyBlock;
import net.minecraft.ILogAgent;
import net.minecraft.ISaveHandler;
import net.minecraft.Profiler;
import net.minecraft.World;
import net.minecraft.WorldProvider;
import net.minecraft.WorldSettings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class  WorldMixin {

    @Mutable
    @Shadow
    @Final
    private boolean has_skylight;

    @Inject(method = {"<init>(Lnet/minecraft/ISaveHandler;Ljava/lang/String;Lnet/minecraft/WorldSettings;Lnet/minecraft/WorldProvider;Lnet/minecraft/Profiler;Lnet/minecraft/ILogAgent;)V"}, at = @At("RETURN"))
    private void rusted_iron_core$onInit(ISaveHandler par1ISaveHandler, String par2Str, WorldSettings par3WorldSettings, WorldProvider par4WorldProvider, Profiler par5Profiler, ILogAgent par6ILogAgent, CallbackInfo ci) {
        this.rusted_iron_core$handleSkyLight(par4WorldProvider.dimensionId);
    }

    @Inject(method = "<init>(Lnet/minecraft/ISaveHandler;Ljava/lang/String;Lnet/minecraft/WorldProvider;Lnet/minecraft/WorldSettings;Lnet/minecraft/Profiler;Lnet/minecraft/ILogAgent;JJ)V", at = @At("RETURN"))
    private void rusted_iron_core$onInit2(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent, long world_creation_time, long total_world_time, CallbackInfo ci) {
        this.rusted_iron_core$handleSkyLight(par3WorldProvider.dimensionId);
    }

    @Unique
    private void rusted_iron_core$handleSkyLight(int dimensionId) {
        Handlers.Dimension.getDimensionContext(dimensionId).ifPresent(context -> this.has_skylight = context.hasSkyLight());
    }

    @ModifyVariable(method = "computeLightValue", at = @At(value = "FIELD", target = "Lnet/minecraft/Block;lightOpacity:[I", ordinal = 0, opcode = Opcodes.GETSTATIC), name = "var6")
    private int rusted_iron_core$resolveLightValue(int var6, int par1, int par2, int par3, EnumSkyBlock par4EnumSkyBlock) {
        if (par4EnumSkyBlock == EnumSkyBlock.Sky) {
            return var6;
        }
        return DynamicLightBlock.resolve((World) (Object) this, par1, par2, par3, var6);
    }

    @ModifyVariable(method = "computeLightValueMITE", at = @At(value = "FIELD", target = "Lnet/minecraft/Block;lightOpacity:[I", ordinal = 0, opcode = Opcodes.GETSTATIC), name = "var6")
    private int rusted_iron_core$resolveOptimizedLightValue(int var6, int par1, int par2, int par3, EnumSkyBlock par4EnumSkyBlock, Chunk chunk) {
        if (par4EnumSkyBlock == EnumSkyBlock.Sky) {
            return var6;
        }
        return DynamicLightBlock.resolve((World) (Object) this, par1, par2, par3, var6);
    }

    @ModifyVariable(method = "computeBlocklightValueMITE", at = @At(value = "FIELD", target = "Lnet/minecraft/Block;lightOpacity:[I", ordinal = 0, opcode = Opcodes.GETSTATIC), name = "var6")
    private int rusted_iron_core$resolveOptimizedBlockLightValue(int var6, int par1, int par2, int par3, Chunk chunk) {
        return DynamicLightBlock.resolve((World) (Object) this, par1, par2, par3, var6);
    }
}
