package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.model.JsonBlockModel;
import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import moddedmite.rustedironcore.api.model.JsonModelRenderContext;
import net.minecraft.Block;
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
        if (par2ItemStack == null) {
            return;
        }

        JsonBlockModel.RenderContext context = JsonModelRenderContext.currentOr(JsonBlockModel.RenderContext.FIRST_PERSON_RIGHT_HAND);
        if (par2ItemStack.isBlock()) {
            Block block = Block.blocksList[par2ItemStack.itemID];
            if (block != null && JsonBlockModelManager.INSTANCE.renderBlockAsItem(block, par2ItemStack.getItemSubtype(), 1.0F, context)) {
                ci.cancel();
            }
            return;
        }

        Icon fallbackIcon = par1EntityLivingBase.getItemIcon(par2ItemStack, par3);
        if (JsonBlockModelManager.INSTANCE.renderItem(par2ItemStack, fallbackIcon, context)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderItemInFirstPerson", at = @At("HEAD"))
    private void rustedIronCore$pushFirstPersonItemContext(float partialTicks, CallbackInfo ci) {
        JsonModelRenderContext.push(JsonBlockModel.RenderContext.FIRST_PERSON_RIGHT_HAND);
    }

    @Inject(method = "renderItemInFirstPerson", at = @At("RETURN"))
    private void rustedIronCore$popFirstPersonItemContext(float partialTicks, CallbackInfo ci) {
        JsonModelRenderContext.pop();
    }
}
