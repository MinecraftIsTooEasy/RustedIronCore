package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.model.JsonModelDisplayContext;
import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import moddedmite.rustedironcore.api.model.JsonItemEntityRenderer;
import net.minecraft.Block;
import net.minecraft.EntityItem;
import net.minecraft.FontRenderer;
import net.minecraft.ItemStack;
import net.minecraft.RenderItem;
import net.minecraft.TextureManager;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin {

    @Shadow
    public float zLevel;

    @Inject(method = "renderItemIntoGUI", at = @At("HEAD"), cancellable = true)
    private void renderJsonItemIntoGui(FontRenderer par1FontRenderer, TextureManager par2TextureManager, ItemStack par3ItemStack, int par4, int par5, CallbackInfo ci) {
        if (par3ItemStack == null) {
            return;
        }

        if (JsonBlockModelManager.INSTANCE.renderItemIntoGui(par3ItemStack, par4, par5, this.zLevel)) {
            ci.cancel();
        }
    }

    @Inject(method = "doRenderItem", at = @At("HEAD"), cancellable = true)
    private void renderJsonDroppedItem(EntityItem par1EntityItem, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci) {
        if (!RenderItem.renderInFrame) {
            if (JsonItemEntityRenderer.render(par1EntityItem, par2, par4, par6, par9)) {
                ci.cancel();
            }
            return;
        }

        ItemStack stack = par1EntityItem.getEntityItem();
        if (stack == null) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) par2, (float) par4 + 0.15F, (float) par6);
        GL11.glRotatef(((par1EntityItem.age + par9) / 20.0F + par1EntityItem.hoverStart) * 57.295776F, 0.0F, 1.0F, 0.0F);

        if (stack.isBlock()) {
            Block block = Block.blocksList[stack.itemID];
            if (block == null) {
                GL11.glPopMatrix();
                return;
            }
            boolean rendered = JsonBlockModelManager.INSTANCE.renderBlockAsItem(stack, block, 1.0F, JsonModelDisplayContext.GROUND, null);
            GL11.glPopMatrix();
            if (rendered) {
                ci.cancel();
            }
            return;
        }

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        boolean rendered = JsonBlockModelManager.INSTANCE.renderItem(stack, stack.getIconIndex(), JsonModelDisplayContext.GROUND);
        GL11.glPopMatrix();

        if (rendered) {
            ci.cancel();
        }
    }
}
