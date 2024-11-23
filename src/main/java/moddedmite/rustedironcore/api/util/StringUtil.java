package moddedmite.rustedironcore.api.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.I18n;
import net.minecraft.Minecraft;

public class StringUtil {
    @Environment(EnvType.CLIENT)
    public static String translate(String key) {
        return I18n.getString(key);
    }

    @Environment(EnvType.CLIENT)
    public static String translateF(String key, Object... params) {
        return I18n.getStringParams(key, params);
    }

    @Environment(EnvType.CLIENT)
    public static String getCurrentLanguage() {
        return Minecraft.getMinecraft().gameSettings.language;
    }
}
