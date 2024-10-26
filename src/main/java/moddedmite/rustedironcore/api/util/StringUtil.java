package moddedmite.rustedironcore.api.util;

import net.minecraft.I18n;
import net.minecraft.Minecraft;

public class StringUtil {
    public static String translate(String key) {
        return I18n.getString(key);
    }

    public static String getCurrentLanguage() {
        return Minecraft.getMinecraft().gameSettings.language;
    }
}
