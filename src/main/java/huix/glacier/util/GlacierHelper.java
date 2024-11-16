package huix.glacier.util;

public class GlacierHelper {
    private static int nextCreativeID = 12;

    public static int getNextCreativeID() {
        return nextCreativeID++;
    }
}
