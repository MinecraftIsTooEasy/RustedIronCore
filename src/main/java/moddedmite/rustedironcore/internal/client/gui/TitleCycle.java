package moddedmite.rustedironcore.internal.client.gui;

import moddedmite.rustedironcore.api.util.FabricUtil;
import moddedmite.rustedironcore.api.util.GuiUtil;
import moddedmite.rustedironcore.api.util.StringUtil;
import moddedmite.rustedironcore.localization.internal.RICText;

public class TitleCycle {
    public static int changeTitlePeriod = 100;
    private static int changeTitleCounter = 0;
    private static boolean isNowRICTitle = false;
    private static String titleCache;

    public static void onClientTick() {
        if (!shouldChangeTitle()) return;
        changeTitleCounter++;
        if (changeTitleCounter >= changeTitlePeriod) {
            if (isNowRICTitle) {
                if (titleCache == null) return;
                GuiUtil.setWindowTitle(titleCache);
                isNowRICTitle = false;
            } else {
                titleCache = GuiUtil.getWindowTitle();
                String translate = StringUtil.translate(RICText.GUI_TITLE.getKey());
                GuiUtil.setWindowTitle(translate);
                isNowRICTitle = true;
            }
            changeTitleCounter = 0;
        }
    }

    private static boolean shouldChangeTitle() {
        if (!StringUtil.getCurrentLanguage().equals("zh_CN")) return false;
        if (FabricUtil.isDevelopmentEnvironment()) return false;
        return true;
    }
}
