package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.EntityVillager;
import net.minecraft.RenderVillager;
import net.minecraft.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderVillager.class)
public class RenderVillagerMixin {

    @Inject(method = "func_110902_a", at = @At("HEAD"), cancellable = true)
    private void rewrite(EntityVillager par1EntityVillager, CallbackInfoReturnable<ResourceLocation> cir) {
        Handlers.Trading.getForProfession(par1EntityVillager.getProfession())
                .ifPresent(x -> cir.setReturnValue(x.getTexture()));
    }

}
