package moddedmite.rustedironcore.mixin.util;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.StagedHandler;
import moddedmite.rustedironcore.api.interfaces.IPotion;
import net.minecraft.Potion;
import net.minecraft.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Potion.class)
public abstract class PotionMixin implements IPotion {
    @Shadow
    @Final
    public static Potion field_76443_y;

    @Shadow
    @Final
    public int id;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        ((StagedHandler) Handlers.PotionRegistry).publish();
    }

    @Override
    public boolean ric$UsesIndividualTexture() {
        return this.id == field_76443_y.id;
    }

    @Override
    public ResourceLocation ric$GetTexture() {
        return new ResourceLocation("textures/gui/mob_effects/saturation.png");
    }
}
