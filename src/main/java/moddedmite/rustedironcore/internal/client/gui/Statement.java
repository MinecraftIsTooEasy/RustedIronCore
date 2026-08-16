package moddedmite.rustedironcore.internal.client.gui;

import moddedmite.rustedironcore.api.util.FabricUtil;
import moddedmite.rustedironcore.api.util.StringUtil;
import moddedmite.rustedironcore.internal.config.RICConfig;
import moddedmite.rustedironcore.localization.internal.RICText;
import moddedmite.rustedironcore.mixin.client.gui.IGuiMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.GuiIngame;
import net.minecraft.Minecraft;
import net.minecraft.ScaledResolution;

import java.util.Arrays;
import java.util.List;

public class Statement {
    public static int firstLoginStatementCounter = 0;

    public static void onRenderTick() {
        if (!shouldRenderStatement()) return;
        renderStatement(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Environment(EnvType.CLIENT)
    public static boolean shouldRenderStatement() {
        if (firstLoginStatementCounter == 0) return false;// the time is over
        if (!StringUtil.getCurrentLanguage().equals("zh_CN")) return false;// Non-Chinese skip
        if (FabricUtil.isDevelopmentEnvironment()) return false;// dev env disable
        if (RICConfig.StatementOnLogin.get()) return true;// config is true then render
        return false;
    }

    public static void renderStatement(Minecraft client, GuiIngame guiIngame) {
        ScaledResolution scaledResolution = new ScaledResolution(client.gameSettings, client.displayWidth, client.displayHeight);
        int x = scaledResolution.getScaledWidth() / 2;
        int y = scaledResolution.getScaledHeight() / 2;
        int leftX = x - 85;
        int topY = y - 35;
        ((IGuiMixin) guiIngame).invokeDrawGradientRect(leftX, topY, x + 85, y + 35, 1074794512, 1074794512);
        List<String> list = Arrays.stream(RICText.STATEMENT.translate().split("[,.;]")).map(String::trim).toList();
        int stringX = leftX + 5;
        int stringY = topY + 5;
        for (String s : list) {
            client.fontRenderer.drawString(s, stringX, stringY, 0xE0E0E0);
            stringY += 10;
        }
        String countdown = RICText.STATEMENT_COUNTDOWN.translate(firstLoginStatementCounter / 20);
        client.fontRenderer.drawString(countdown, stringX, stringY, 0xE0E0E0);
    }
}
