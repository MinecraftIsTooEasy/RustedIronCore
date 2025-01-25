package moddedmite.rustedironcore.api.util;

import moddedmite.rustedironcore.compat.ModCompat;
import moddedmite.rustedironcore.internal.unsafe.PinInImpl;

public class PinyinSupport {
    private static PinInImpl pinInImpl;

    public static boolean isValid() {
        return pinInImpl != null;
    }

    /**
     * The chars can be Chinese characters.
     */
    public static int compareChars(char input1, char input2) {
        return convertToLetters(input1).compareToIgnoreCase(convertToLetters(input2));
    }

    public static boolean contains(String provider, String input) {
        return pinInImpl.contains(provider, input);
    }

    private static String convertToLetters(char c) {
        return pinInImpl.convertToLetters(c);
    }

    static {
        if (ModCompat.HAS_PININ) {
            try {
                pinInImpl = new PinInImpl();
            } catch (RuntimeException e) {
                pinInImpl = null;
            }
        }
    }
}

