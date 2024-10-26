package moddedmite.rustedironcore.api.util;

import org.lwjgl.opengl.Display;

public class GuiUtil {
    public static void setWindowTitle(String title) {
        Display.setTitle(title);
    }

    public static String getWindowTitle() {
        return Display.getTitle();
    }
}
