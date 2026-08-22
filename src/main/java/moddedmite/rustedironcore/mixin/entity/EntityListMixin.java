package moddedmite.rustedironcore.mixin.entity;

import moddedmite.rustedironcore.internal.Bootstrap;
import net.minecraft.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityList.class)
public class EntityListMixin {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        Bootstrap.logStage("entity");
    }
}
