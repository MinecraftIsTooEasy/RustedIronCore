package moddedmite.rustedironcore.mixin.block;

import huix.glacier.api.registry.sync.RegistryHelperImpl;
import huix.glacier.api.registry.sync.remappers.BlockRegistryRemapper;
import moddedmite.rustedironcore.internal.Bootstrap;
import net.minecraft.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        RegistryHelperImpl.registerRegistryRemapper(BlockRegistryRemapper::new);
        Bootstrap.logStage("block");
    }
}
