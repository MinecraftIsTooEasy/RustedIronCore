package moddedmite.rustedironcore.mixin.client.gui;

import moddedmite.rustedironcore.api.event.Handlers;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.internal.client.hud.ClientHud;
import moddedmite.rustedironcore.internal.client.hud.HudColors;
import net.minecraft.BossStatus;
import net.minecraft.FontRenderer;
import net.minecraft.Gui;
import net.minecraft.GuiIngame;
import net.minecraft.Minecraft;
import net.minecraft.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class GuiIngameMixin extends Gui {

    @Unique
    private boolean ric$tabListTranslated;

    @Inject(method = "renderGameOverlay(FZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;inDevMode()Z"))
    private void renderStatement(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        Handlers.Tick.onRenderTick(par1);
    }

    @Inject(method = "updateTick", at = @At("RETURN"))
    private void tickHud(CallbackInfo ci) {
        ClientHud.tick();
    }

    @WrapOperation(method = "renderGameOverlay(FZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/FontRenderer;drawString(Ljava/lang/String;III)I"), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=overlayMessage"), to = @At(value = "CONSTANT", args = "stringValue=chat")))
    private int renderActionBarText(FontRenderer font, String text, int x, int y, int color, Operation<Integer> original) {
        // Keep MITE's equivalent lifetime, position and fade; replace its text suffix with 1.21.1 backdrop/shadow.
        return ClientHud.getGraphics().drawStringWithBackdrop(text, x, y, color);
    }

    @WrapOperation(method = "renderGameOverlay(FZII)V", at = @At(value = "INVOKE", target = "Ljava/awt/Color;HSBtoRGB(FFF)I"), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=overlayMessage"), to = @At(value = "CONSTANT", args = "stringValue=chat")))
    private int calculateActionBarColor(float hue, float saturation, float value, Operation<Integer> original) {
        return HudColors.hsvToRgb(hue, saturation, value);
    }

    @WrapOperation(method = "renderGameOverlay(FZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiIngame;renderBossHealth()V"))
    private void renderModernBossBars(GuiIngame gui, Operation<Void> original) {
        boolean legacyVisible = BossStatus.bossName != null && BossStatus.statusBarLength > 0;
        original.call(gui);
        ClientHud.getBosses().render(ClientHud.getGraphics(), legacyVisible);
    }

    @WrapOperation(method = "renderGameOverlay(FZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/Profiler;startSection(Ljava/lang/String;)V"), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=playerList")))
    private void renderTabDecorations(Profiler profiler, String name, Operation<Void> original) {
        original.call(profiler, name);
        if ("playerList".equals(name)) {
            ric$tabListTranslated = ClientHud.getTabList().beginPlayerList(ClientHud.getGraphics());
        }
    }

    @WrapOperation(method = "renderGameOverlay(FZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiIngame;drawRect(IIIII)V", ordinal = 0), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=playerList")))
    private void expandTabBackground(int left, int top, int right, int bottom, int color, Operation<Void> original) {
        int width = ClientHud.getTabList().getBackgroundWidth();
        if (ric$tabListTranslated && width > right - left - 2) {
            int center = ClientHud.getGraphics().guiWidth() / 2;
            left = center - width / 2 - 1;
            right = center + width / 2 + 1;
        }
        original.call(left, top, right, bottom, color);
    }

    @Inject(method = "renderGameOverlay(FZII)V", at = @At("RETURN"))
    private void renderHudOverlays(float partialTick, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci) {
        if (ric$tabListTranslated) {
            ClientHud.getGraphics().popPose();
            ric$tabListTranslated = false;
        }
        Minecraft client = Minecraft.getMinecraft();
        if (client.gameSettings.gui_mode != 2 && !client.thePlayer.isGhost()) {
            ClientHud.renderHints(partialTick);
            ClientHud.getTitles().render(ClientHud.getGraphics(), partialTick);
        }
    }
}
