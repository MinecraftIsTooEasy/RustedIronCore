package moddedmite.rustedironcore.mixin.client.gui;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.Gui;
import net.minecraft.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class GuiIngameMixin extends Gui {

    @Inject(method = "renderGameOverlay(FZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;inDevMode()Z"))
    private void renderStatement(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        Handlers.Tick.onRenderTick(par1);
    }
}
