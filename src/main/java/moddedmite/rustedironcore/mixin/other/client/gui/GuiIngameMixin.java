package moddedmite.rustedironcore.mixin.other.client.gui;

import moddedmite.rustedironcore.api.util.StringUtil;
import moddedmite.rustedironcore.internal.config.RICConfig;
import moddedmite.rustedironcore.network.packets.S2COpenGuiTips;
import net.minecraft.Gui;
import net.minecraft.GuiIngame;
import net.minecraft.Minecraft;
import net.minecraft.ScaledResolution;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
    @Inject(method = "renderGameOverlay(FZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;inDevMode()Z"))
    private void renderStatement(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        if (!this.shouldRenderStatement()) return;
        ScaledResolution scaledResolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int x = scaledResolution.getScaledWidth() / 2;
        int y = scaledResolution.getScaledHeight() / 2;
        int leftX = x - 85;
        int topY = y - 30;
        this.drawGradientRect(leftX, topY, x + 85, y + 30, 1074794512, 1074794512);
        List<String> list = Arrays.stream(StringUtil.translate("ric.statement").split("[,.;]")).map(String::trim).toList();
        int stringX = leftX + 5;
        int stringY = topY + 5;
        for (String s : list) {
            this.mc.fontRenderer.drawString(s, stringX, stringY, 0xE0E0E0);
            stringY += 10;
        }
        String countdown = StringUtil.translateF("ric.statement.countdown", S2COpenGuiTips.firstLoginStatementCounter / 20);
        this.mc.fontRenderer.drawString(countdown, stringX, stringY, 0xE0E0E0);
    }

    @Unique
    private boolean shouldRenderStatement() {
        if (S2COpenGuiTips.firstLoginStatementCounter == 0) return false;// the time is over
        if (!RICConfig.StatementOnLogin.get() && StringUtil.getCurrentLanguage().equals("en_US"))
            return false;// config is false and English
        return true;
    }
}
