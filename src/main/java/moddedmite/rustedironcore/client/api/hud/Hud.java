package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.internal.client.hud.ClientHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Access to the client overlays, including title state, custom toasts and client-only boss bars. */
@Environment(EnvType.CLIENT)
public final class Hud {

    public static float getTextBackgroundOpacity() {
        return ClientHud.getGraphics().getTextBackgroundOpacity();
    }

    public static void setTextBackgroundOpacity(float opacity) {
        ClientHud.getGraphics().setTextBackgroundOpacity(opacity);
    }

    public static TitleOverlay getTitles() {
        return ClientHud.getTitles();
    }

    public static BossOverlay getBosses() {
        return ClientHud.getBosses();
    }

    public static TabListOverlay getTabList() {
        return ClientHud.getTabList();
    }

    public static ToastManager getToasts() {
        return ClientHud.getToasts();
    }
}
