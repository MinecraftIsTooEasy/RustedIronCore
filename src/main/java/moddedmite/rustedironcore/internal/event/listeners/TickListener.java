package moddedmite.rustedironcore.internal.event.listeners;

import moddedmite.rustedironcore.api.event.listener.ITickListener;
import moddedmite.rustedironcore.api.util.GuiUtil;
import moddedmite.rustedironcore.api.util.StringUtil;
import net.minecraft.Minecraft;

public class TickListener implements ITickListener {
    private static int changeTitleCounter = 0;
    public static int changeTitlePeriod = 100;
    private static boolean isNowRICTitle = false;
    private static String titleCache;

    @Override
    public void onClientTick(Minecraft client) {
        if (StringUtil.getCurrentLanguage().equals("en_US")) return;
        changeTitleCounter++;
        if (changeTitleCounter >= changeTitlePeriod) {
            if (isNowRICTitle) {
                if (titleCache == null) return;
                GuiUtil.setWindowTitle(titleCache);
                isNowRICTitle = false;
            } else {
                titleCache = GuiUtil.getWindowTitle();
                String translate = StringUtil.translate("ric.gui.title");
                if (translate.equals("ric.gui.title")) return;
                GuiUtil.setWindowTitle(translate);
                isNowRICTitle = true;
            }
            changeTitleCounter = 0;
        }
//                Handlers.TimedTask.onTick();
    }
}
