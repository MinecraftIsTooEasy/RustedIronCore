package moddedmite.rustedironcore.mixin.client.gui;

import moddedmite.rustedironcore.RustedIronCore;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.internal.network.packets.S2COpenGuiTips;
import moddedmite.rustedironcore.localization.internal.RICText;
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

    @Inject(method = "renderGameOverlay(FZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;inDevMode()Z"))
    private void renderStatement(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        Handlers.Tick.onRenderTick(par1);
        if (!RustedIronCore.shouldRenderStatement()) return;
        renderStatement(this.mc);
    }

    @Unique
    private void renderStatement(Minecraft client) {
        ScaledResolution scaledResolution = new ScaledResolution(client.gameSettings, client.displayWidth, client.displayHeight);
        int x = scaledResolution.getScaledWidth() / 2;
        int y = scaledResolution.getScaledHeight() / 2;
        int leftX = x - 85;
        int topY = y - 30;
        this.drawGradientRect(leftX, topY, x + 85, y + 30, 1074794512, 1074794512);
        List<String> list = Arrays.stream(RICText.Statement.translate().split("[,.;]")).map(String::trim).toList();
        int stringX = leftX + 5;
        int stringY = topY + 5;
        for (String s : list) {
            client.fontRenderer.drawString(s, stringX, stringY, 0xE0E0E0);
            stringY += 10;
        }
        String countdown = RICText.StatementCountdown.translate(S2COpenGuiTips.firstLoginStatementCounter / 20);
        client.fontRenderer.drawString(countdown, stringX, stringY, 0xE0E0E0);
    }
}
