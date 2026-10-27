package moddedmite.rustedironcore.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import net.minecraft.ResourceLocation;

import javax.annotation.Nullable;

/** A network-safe vanilla system notification; identical tokens update the current notification. */
public record SystemToastData(ResourceLocation token, TextComponent title, @Nullable TextComponent message, long displayTimeMillis, boolean multiline) {

    public SystemToastData {
        if (displayTimeMillis < 0) throw new IllegalArgumentException("Toast duration must not be negative");
    }

    public SystemToastData(ResourceLocation token, TextComponent title, @Nullable TextComponent message) {
        this(token, title, message, 5000L, false);
    }
}
