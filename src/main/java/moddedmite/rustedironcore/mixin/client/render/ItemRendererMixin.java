package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.model.JsonBlockModel;
import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import net.minecraft.EntityLivingBase;
import net.minecraft.Icon;
import net.minecraft.ItemRenderer;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void renderJsonItemModel(EntityLivingBase par1EntityLivingBase, ItemStack par2ItemStack, int par3, CallbackInfo ci) {
        if (par2ItemStack == null || par2ItemStack.isBlock()) {
            return;
        }

        Icon fallbackIcon = par1EntityLivingBase.getItemIcon(par2ItemStack, par3);
        if (JsonBlockModelManager.INSTANCE.renderItem(par2ItemStack, fallbackIcon, JsonBlockModel.RenderContext.FIRST_PERSON_RIGHT_HAND)) {
            ci.cancel();
        }
    }
}
