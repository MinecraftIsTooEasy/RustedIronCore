package moddedmite.rustedironcore.mixin.other.world;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.BiomeDecorationRegisterEvent;
import moddedmite.rustedironcore.api.event.events.OreGenerationRegisterEvent;
import net.minecraft.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeGenBase.class)
public class BiomeGenBaseMixin {
    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void onStaticHead(CallbackInfo ci) {
        Handlers.BiomeDecoration.publish(new BiomeDecorationRegisterEvent());
        Handlers.OreGeneration.publish(new OreGenerationRegisterEvent());
    }
}
