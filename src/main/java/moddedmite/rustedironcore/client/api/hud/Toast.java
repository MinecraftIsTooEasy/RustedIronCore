package moddedmite.rustedironcore.client.api.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Toast {

    Object NO_TOKEN = new Object();

    ToastVisibility render(HudGraphics graphics, ToastManager manager, long visibleMillis);

    default Object getToken() {
        return NO_TOKEN;
    }

    default int width() {
        return 160;
    }

    default int height() {
        return 32;
    }

    default int slotCount() {
        return (height() + 31) / 32;
    }

    /** Return true when an incoming notification was merged into this instance. */
    default boolean update(Toast incoming) {
        return false;
    }
}
