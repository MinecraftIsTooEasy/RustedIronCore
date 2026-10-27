package moddedmite.rustedironcore.api.hud;

/** Screen and text-block anchors in scaled GUI coordinates. */
public enum HintAnchor {

    HOTBAR(1, 2),
    TOP_LEFT(0, 0),
    TOP_CENTER(1, 0),
    TOP_RIGHT(2, 0),
    CENTER_LEFT(0, 1),
    CENTER(1, 1),
    CENTER_RIGHT(2, 1),
    BOTTOM_LEFT(0, 2),
    BOTTOM_CENTER(1, 2),
    BOTTOM_RIGHT(2, 2);

    private final int horizontal;
    private final int vertical;

    HintAnchor(int horizontal, int vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    int x(int screenWidth, int textWidth) {
        return screenWidth * horizontal / 2 - textWidth * horizontal / 2;
    }

    int y(int screenHeight, int textHeight) {
        // GuiIngame draws the native Action Bar at height - 68, with its text at y = -4.
        return this == HOTBAR ? screenHeight - 72 : screenHeight * vertical / 2 - textHeight * vertical / 2;
    }
}
