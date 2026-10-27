package moddedmite.rustedironcore.api.chat;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

/**
 * Resource locations used by the modern text component {@code font} style.
 *
 * <p>The style stores the font as an optional resource location. A missing
 * value means {@link #DEFAULT}; it is kept nullable in {@link TextStyle} so
 * that inheritance and JSON round trips retain the distinction between an
 * omitted field and an explicitly selected default font.</p>
 */
public final class TextFonts {

    public static final String DEFAULT = "minecraft:default";
    public static final String UNIFORM = "minecraft:uniform";
    public static final String ALT = "minecraft:alt";
    public static final String ILLAGER_ALT = "minecraft:illageralt";

    private static final Pattern NAMESPACE = Pattern.compile("[a-z0-9_.-]+");
    private static final Pattern PATH = Pattern.compile("[a-z0-9/._-]*");

    /**
     * Normalizes a font resource location in the same form used by modern
     * text components. Bare paths use the {@code minecraft} namespace.
     */
    @Nullable
    public static String normalize(@Nullable String font) {
        if (font == null) {
            return null;
        }
        int separator = font.indexOf(':');
        String namespace = separator <= 0 ? "minecraft" : font.substring(0, separator);
        String path = separator < 0 ? font : font.substring(separator + 1);
        if (!NAMESPACE.matcher(namespace).matches() || !PATH.matcher(path).matches()) {
            throw new IllegalArgumentException("Invalid font id: " + font);
        }
        return namespace + ":" + path;
    }

    /** Returns the effective font for a style whose font field may be absent. */
    public static String effective(@Nullable String font) {
        String normalized = normalize(font);
        return normalized == null ? DEFAULT : normalized;
    }

    public static boolean isBuiltin(@Nullable String font) {
        String normalized = normalize(font);
        return DEFAULT.equals(normalized) || UNIFORM.equals(normalized)
                || ALT.equals(normalized) || ILLAGER_ALT.equals(normalized);
    }
}
