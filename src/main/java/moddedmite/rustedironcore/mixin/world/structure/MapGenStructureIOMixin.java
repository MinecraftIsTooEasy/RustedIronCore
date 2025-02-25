package moddedmite.rustedironcore.mixin.world.structure;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.StructureNBTRegisterEvent;
import net.minecraft.MapGenStructureIO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapGenStructureIO.class)
public class MapGenStructureIOMixin {
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        Handlers.StructureNBT.publish(new StructureNBTRegisterEvent());
    }
}
