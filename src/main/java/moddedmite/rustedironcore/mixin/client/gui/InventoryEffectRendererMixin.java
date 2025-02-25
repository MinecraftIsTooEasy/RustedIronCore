package moddedmite.rustedironcore.mixin.client.gui;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.interfaces.IPotion;
import net.minecraft.Container;
import net.minecraft.GuiContainer;
import net.minecraft.InventoryEffectRenderer;
import net.minecraft.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryEffectRenderer.class)
public abstract class InventoryEffectRendererMixin extends GuiContainer {
    public InventoryEffectRendererMixin(Container par1Container) {
        super(par1Container);
    }

    @Inject(method = "displayDebuffEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/Potion;hasStatusIcon()Z"))
    private void drawIndividualIcon(CallbackInfo ci, @Local Potion var8, @Local(ordinal = 0) int var1, @Local(ordinal = 1) int var2) {
        if (((IPotion) var8).ric$UsesIndividualTexture()) {
            this.mc.getTextureManager().bindTexture(((IPotion) var8).ric$GetTexture());
            this.drawTexturedModalRect2(var1 + 6, var2 + 7, 18, 18);
        }
    }
}
