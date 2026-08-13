package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.model.JsonBlockModel;
import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import moddedmite.rustedironcore.api.model.JsonModelRenderContext;
import net.minecraft.Block;
import net.minecraft.IBlockAccess;
import net.minecraft.RenderBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBlocks.class)
public abstract class RenderBlocksMixin {

    @Shadow
    public IBlockAccess blockAccess;

    @Inject(method = "renderBlockByRenderType", at = @At("HEAD"), cancellable = true)
    private void renderJsonBlock(Block par1Block, int par2, int par3, int par4, CallbackInfoReturnable<Boolean> cir) {
        int metadata = this.blockAccess.getBlockMetadata(par2, par3, par4);
        if (JsonBlockModelManager.INSTANCE.renderBlock((RenderBlocks) (Object) this, par1Block, par2, par3, par4, metadata)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "renderBlockAsItem", at = @At("HEAD"), cancellable = true)
    private void renderJsonBlockAsItem(Block par1Block, int par2, float par3, CallbackInfo ci) {
        if (JsonBlockModelManager.INSTANCE.renderBlockAsItem(par1Block, par2, par3, JsonModelRenderContext.currentOr(JsonBlockModel.RenderContext.BLOCK_ITEM))) {
            ci.cancel();
        }
    }

}
