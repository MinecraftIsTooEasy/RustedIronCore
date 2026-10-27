package moddedmite.rustedironcore.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import moddedmite.rustedironcore.internal.chat.LegacyTextFormatting;
import moddedmite.rustedironcore.internal.client.chat.LegacyTextWidth;
import net.minecraft.EnumChatFormatting;
import net.minecraft.FontRenderer;
import net.minecraft.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public abstract class FontRendererMixin {

    @Unique
    private int ric$activeRgbColor = -1;

    @Inject(method = "renderStringAtPos(Ljava/lang/String;Z)V", at = @At("HEAD"))
    private void resetRgbColor(String text, boolean shadow, CallbackInfo ci) {
        this.ric$activeRgbColor = -1;
    }

    @WrapOperation(method = "renderStringAtPos(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/EnumChatFormatting;getByChar(C)Lnet/minecraft/EnumChatFormatting;"))
    private EnumChatFormatting decodeRgbFormatting(char code, Operation<EnumChatFormatting> original, @Local(argsOnly = true) String text, @Local(ordinal = 0) LocalIntRef index) {
        int current = index.get();
        if (Character.toLowerCase(code) == 'x' && LegacyTextFormatting.isRgbCodeAt(text, current)) {
            this.ric$activeRgbColor = LegacyTextFormatting.rgbAt(text, current);
            index.set(current + 12);
            return EnumChatFormatting.WHITE;
        }

        EnumChatFormatting formatting = original.call(code);
        if (formatting == EnumChatFormatting.RESET || formatting != null && formatting.isColor()) {
            this.ric$activeRgbColor = -1;
        }
        return formatting;
    }

    @Redirect(method = "renderStringAtPos(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 0))
    private void renderRgbColor(float red, float green, float blue, float alpha, @Local(argsOnly = true) boolean shadow) {
        if (this.ric$activeRgbColor < 0) {
            GL11.glColor4f(red, green, blue, alpha);
            return;
        }

        int rgb = this.ric$activeRgbColor;
        int r = rgb >> 16 & 255;
        int g = rgb >> 8 & 255;
        int b = rgb & 255;
        if (Minecraft.theMinecraft.gameSettings.anaglyph) {
            int luminance = (r * 30 + g * 59 + b * 11) / 100;
            g = (r * 30 + g * 70) / 100;
            b = (r * 30 + b * 70) / 100;
            r = luminance;
        }
        if (shadow) {
            r /= 4;
            g /= 4;
            b /= 4;
        }
        GL11.glColor4f(r / 255.0F, g / 255.0F, b / 255.0F, alpha);
    }

    @Inject(method = "sizeStringToWidth(Ljava/lang/String;I)I", at = @At("HEAD"), cancellable = true)
    private void measureRgbFormattedText(String text, int maxWidth, CallbackInfoReturnable<Integer> cir) {
        if (text.indexOf(LegacyTextFormatting.CODE) < 0) {
            return;
        }
        boolean containsRgb = false;
        for (int index = text.indexOf(LegacyTextFormatting.CODE); index >= 0;
             index = text.indexOf(LegacyTextFormatting.CODE, index + 1)) {
            if (LegacyTextFormatting.isRgbCodeAt(text, index)) {
                containsRgb = true;
                break;
            }
        }
        if (!containsRgb) {
            return;
        }

        cir.setReturnValue(LegacyTextWidth.sizeStringToWidth((FontRenderer) (Object) this, text, maxWidth));
    }

    @Inject(method = "getFormatFromString(Ljava/lang/String;)Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    private static void preserveRgbForWrappedText(String text, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(LegacyTextFormatting.preserveLastRgbColor(text, cir.getReturnValue()));
    }

}
