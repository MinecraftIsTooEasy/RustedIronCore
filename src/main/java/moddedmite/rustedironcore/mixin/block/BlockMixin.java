package moddedmite.rustedironcore.mixin.block;

import huix.glacier.api.registry.sync.RegistryHelperImpl;
import huix.glacier.api.registry.sync.remappers.BlockRegistryRemapper;
import moddedmite.rustedironcore.api.block.DynamicLightBlock;
import moddedmite.rustedironcore.core.Holder;
import moddedmite.rustedironcore.core.registries.BuiltInRegistries;
import moddedmite.rustedironcore.internal.Bootstrap;
import moddedmite.rustedironcore.mixins.interfaces.BlockInstanceImpl;
import net.minecraft.Block;
import net.minecraft.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin implements BlockInstanceImpl {
    @Unique
    private final Holder.Reference<Block> builtInRegistryHolder = BuiltInRegistries.BLOCK.createIntrusiveHolder((Block) (Object) this);

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        RegistryHelperImpl.registerRegistryRemapper(BlockRegistryRemapper::new);
        Bootstrap.logStage("block");
    }

    @Redirect(method = "getBlockBrightness", at = @At(value = "INVOKE", target = "Lnet/minecraft/IBlockAccess;getBrightness(IIII)F"))
    private float useDynamicBrightness(IBlockAccess access, int x, int y, int z, int staticEmission) {
        int emission = DynamicLightBlock.resolve(access, x, y, z, staticEmission);
        return access.getBrightness(x, y, z, emission);
    }

    @Redirect(method = "getMixedBrightnessForBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/IBlockAccess;getLightBrightnessForSkyBlocks(IIII)I"))
    private int useDynamicMixedBrightness(IBlockAccess access, int x, int y, int z, int staticEmission) {
        int emission = DynamicLightBlock.resolve(access, x, y, z, staticEmission);
        return access.getLightBrightnessForSkyBlocks(x, y, z, emission);
    }

    @Override
    public Holder<Block> ric$typeHolder() {
        return this.builtInRegistryHolder;
    }
}
