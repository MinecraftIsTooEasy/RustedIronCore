package moddedmite.rustedironcore.mixin.client;

import moddedmite.rustedironcore.internal.client.chat.RgbTextColor;
import net.minecraft.FontRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;

/** Legacy render route: applies the active RGB colour where MITE sets a legacy colour code with {@code glColor4f}. */
@Mixin(FontRenderer.class)
public abstract class FontRendererColorMixin {

    @Redirect(method = "renderStringAtPos(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 0))
    private void renderRgbColor(float red, float green, float blue, float alpha, @Local(argsOnly = true) boolean shadow) {
        int rgb = ((RgbTextColor) this).ric$activeRgbColor();
        if (rgb < 0) {
            GL11.glColor4f(red, green, blue, alpha);
            return;
        }
        float[] color = RgbTextColor.toTextColor(rgb, shadow, alpha);
        GL11.glColor4f(color[0], color[1], color[2], color[3]);
    }
}
