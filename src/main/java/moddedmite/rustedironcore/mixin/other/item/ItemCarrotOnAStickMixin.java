package moddedmite.rustedironcore.mixin.other.item;

import moddedmite.rustedironcore.property.MaterialProperties;
import net.minecraft.ItemCarrotOnAStick;
import net.minecraft.ItemFishingRod;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemCarrotOnAStick.class)
public abstract class ItemCarrotOnAStickMixin {
    @Shadow
    protected Material hook_material;

    @Inject(method = "getFishingRodItem", at = @At("HEAD"), cancellable = true)
    public void getFishingRodItem(CallbackInfoReturnable<ItemFishingRod> cir) {
        MaterialProperties.PeerFishingRod.getOptional(this.hook_material).ifPresent(cir::setReturnValue);
    }
}
