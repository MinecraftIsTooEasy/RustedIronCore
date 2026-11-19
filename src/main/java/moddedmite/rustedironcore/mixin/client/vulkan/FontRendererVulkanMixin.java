package moddedmite.rustedironcore.mixin.client.vulkan;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.internal.client.chat.RgbTextColor;
import moddedmite.rustedironcore.internal.compat.NativeGlShim;
import net.minecraft.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * FML 4.x native route of {@code FontRendererColorMixin}: FML's native {@code renderStringAtPos} sets the colour of a legacy colour
 * code with {@code Draw.color(FFFF)} instead of {@code GL11.glColor4f}.
 */
@Mixin(FontRenderer.class)
public abstract class FontRendererVulkanMixin {

    @Redirect(method = "renderStringAtPos(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lnet/xiaoyu233/fml/vulkan/render/state/Draw;color(FFFF)V", ordinal = 0, remap = false))
    private void renderRgbColor(float red, float green, float blue, float alpha, @Local(argsOnly = true) boolean shadow) {
        int rgb = ((RgbTextColor) this).ric$activeRgbColor();
        if (rgb < 0) {
            NativeGlShim.color4f(red, green, blue, alpha);
            return;
        }
        float[] color = RgbTextColor.toTextColor(rgb, shadow, alpha);
        NativeGlShim.color4f(color[0], color[1], color[2], color[3]);
    }
}
