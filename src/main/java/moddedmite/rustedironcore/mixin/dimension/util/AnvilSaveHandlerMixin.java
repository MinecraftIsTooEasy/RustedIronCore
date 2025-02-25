package moddedmite.rustedironcore.mixin.dimension.util;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(AnvilSaveHandler.class)
public abstract class AnvilSaveHandlerMixin {
    @Inject(method = "getChunkLoader", at = @At("HEAD"), cancellable = true)
    private void onSavePath(WorldProvider par1WorldProvider, CallbackInfoReturnable<IChunkLoader> cir) {
        int dimensionId = par1WorldProvider.dimensionId;
        Handlers.Dimension.parseDimensionId(dimensionId).ifPresent(x -> {
            File var2 = ((AnvilSaveHandler)(Object) this).getWorldDirectory();
            File var3 = new File(var2, "DIM" + dimensionId);
            var3.mkdirs();
            cir.setReturnValue(new AnvilChunkLoader(var3));
        });
    }
}
