package moddedmite.rustedironcore.api.chat;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

public record TextColor(int rgb, @Nullable String legacyName) {

    private static final Map<String, Integer> LEGACY_COLORS = Map.ofEntries(
            Map.entry("black", 0x000000),
            Map.entry("dark_blue", 0x0000AA),
            Map.entry("dark_green", 0x00AA00),
            Map.entry("dark_aqua", 0x00AAAA),
            Map.entry("dark_red", 0xAA0000),
            Map.entry("dark_purple", 0xAA00AA),
            Map.entry("gold", 0xFFAA00),
            Map.entry("gray", 0xAAAAAA),
            Map.entry("dark_gray", 0x555555),
            Map.entry("blue", 0x5555FF),
            Map.entry("green", 0x55FF55),
            Map.entry("aqua", 0x55FFFF),
            Map.entry("red", 0xFF5555),
            Map.entry("light_purple", 0xFF55FF),
            Map.entry("yellow", 0xFFFF55),
            Map.entry("white", 0xFFFFFF)
    );

    public TextColor {
        rgb &= 0xFFFFFF;
    }

    public static TextColor rgb(int rgb) {
        return new TextColor(rgb, null);
    }

    @Nullable
    public static TextColor parse(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        Integer legacy = LEGACY_COLORS.get(normalized);
        if (legacy != null) {
            return new TextColor(legacy, normalized);
        }
        if (normalized.length() == 7 && normalized.charAt(0) == '#') {
            try {
                return rgb(Integer.parseInt(normalized.substring(1), 16));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public String serialize() {
        return this.legacyName == null ? String.format(Locale.ROOT, "#%06x", this.rgb) : this.legacyName;
    }
}
