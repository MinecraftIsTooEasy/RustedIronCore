package moddedmite.rustedironcore.mixin.other.client;

import moddedmite.rustedironcore.network.packets.S2COpenGuiTips;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(GuiIngame.class)
public class GuiIngameMixin extends Gui {
    @Shadow
    @Final
    private Minecraft mc;

    //    @Shadow
//    @Final
//    private Minecraft mc;
//
//    @Inject(method = {"renderGameOverlay(FZII)V"},
//            at = {@At(value = "INVOKE",
//                    target = "Lnet/minecraft/Minecraft;inDevMode()Z",
//                    shift = At.Shift.BEFORE)})
//    private void inject(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
//        if (mc.gameSettings.gui_mode == 0 && mc.gameSettings.keyBindPlayerList.pressed) {
//            GuiTipsWindow.updateAchievementWindow(this, "你好", "你好");
//        }
//    }
    @Inject(method = "renderGameOverlay(FZII)V", at = {@At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;inDevMode()Z", shift = At.Shift.BEFORE)})
    private void inject(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        if (S2COpenGuiTips.firstEnterTipCounter == 0) return;
        ScaledResolution scaledResolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int x = scaledResolution.getScaledWidth() / 2;
        int y = scaledResolution.getScaledHeight() / 2;
        int leftX = x - 85;
        int topY = y - 25;
        this.drawGradientRect(leftX, topY, x + 85, y + 25, 1074794512, 1074794512);
        List<String> list = Arrays.stream(I18n.getString("ric.statement").split("[,.;]")).map(String::trim).toList();
        int stringX = leftX + 10;
        int stringY = topY + 5;
        for (String s : list) {
            this.mc.fontRenderer.drawString(s, stringX, stringY, 0xE0E0E0);
            stringY += 10;
        }
    }

    @Inject(method = "updateTick", at = @At("RETURN"))
    private void updateCounter(CallbackInfo ci) {
        S2COpenGuiTips.firstEnterTipCounter--;
    }
}
