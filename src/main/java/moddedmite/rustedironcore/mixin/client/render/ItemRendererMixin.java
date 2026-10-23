package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.model.JsonModelDisplayContext;
import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import moddedmite.rustedironcore.api.model.JsonHeldItemTransforms;
import moddedmite.rustedironcore.api.model.JsonModelRenderContext;
import moddedmite.rustedironcore.api.model.JsonModernHeldItemRenderer;
import net.minecraft.Block;
import net.minecraft.EntityLivingBase;
import net.minecraft.Icon;
import net.minecraft.ItemBlock;
import net.minecraft.ItemRenderer;
import net.minecraft.ItemStack;
import org.lwjgl.opengl.GL11;
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

        JsonModelDisplayContext context = JsonModelRenderContext.currentOr(JsonModelDisplayContext.FIRST_PERSON_RIGHT_HAND);
        boolean rendered;
        GL11.glPushMatrix();
        try {
            if (!JsonModelRenderContext.hasModernPrefix()) {
                JsonHeldItemTransforms.apply(par1EntityLivingBase, par2ItemStack, context);
            }
            if (par2ItemStack.isBlock()) {
                Block block = par2ItemStack.getItem() instanceof ItemBlock
                        ? ((ItemBlock) par2ItemStack.getItem()).getBlock()
                        : par2ItemStack.itemID >= 0 && par2ItemStack.itemID < Block.blocksList.length
                        ? Block.blocksList[par2ItemStack.itemID] : null;
                rendered = block != null && JsonBlockModelManager.INSTANCE.renderBlockAsItem(par2ItemStack, block, 1.0F, context, par1EntityLivingBase);
            } else {
                Icon fallbackIcon = par1EntityLivingBase.getItemIcon(par2ItemStack, par3);
                rendered = JsonBlockModelManager.INSTANCE.renderItem(par2ItemStack, fallbackIcon, context, par1EntityLivingBase);
            }
        } finally {
            GL11.glPopMatrix();
        }

        if (!rendered && JsonModelRenderContext.hasModernPrefix()) {
            JsonModernHeldItemRenderer.renderFallback(par1EntityLivingBase, par2ItemStack, par3, context);
            rendered = true;
        }
        if (rendered) {
            ci.cancel();
        }
    }
}
