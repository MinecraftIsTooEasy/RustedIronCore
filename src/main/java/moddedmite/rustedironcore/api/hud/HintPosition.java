package moddedmite.rustedironcore.api.hud;

import java.util.Objects;

/** An anchor plus GUI-pixel offsets: positive X moves right, positive Y moves down. */
public record HintPosition(HintAnchor anchor, int offsetX, int offsetY) {

    public static final HintPosition HOTBAR = new HintPosition(HintAnchor.HOTBAR, 0, 0);
    public static final HintPosition TOP_LEFT = new HintPosition(HintAnchor.TOP_LEFT, 8, 8);
    public static final HintPosition TOP_CENTER = new HintPosition(HintAnchor.TOP_CENTER, 0, 8);
    public static final HintPosition TOP_RIGHT = new HintPosition(HintAnchor.TOP_RIGHT, -8, 8);
    public static final HintPosition CENTER_LEFT = new HintPosition(HintAnchor.CENTER_LEFT, 8, 0);
    public static final HintPosition CENTER = new HintPosition(HintAnchor.CENTER, 0, 0);
    public static final HintPosition CENTER_RIGHT = new HintPosition(HintAnchor.CENTER_RIGHT, -8, 0);
    public static final HintPosition BOTTOM_LEFT = new HintPosition(HintAnchor.BOTTOM_LEFT, 8, -8);
    public static final HintPosition BOTTOM_CENTER = new HintPosition(HintAnchor.BOTTOM_CENTER, 0, -8);
    public static final HintPosition BOTTOM_RIGHT = new HintPosition(HintAnchor.BOTTOM_RIGHT, -8, -8);

    public HintPosition {
        Objects.requireNonNull(anchor, "anchor");
    }

    /** Returns a new position offset from this preset or custom position. */
    public HintPosition offset(int x, int y) {
        return new HintPosition(anchor, offsetX + x, offsetY + y);
    }

    public int x(int screenWidth, int textWidth) {
        return anchor.x(screenWidth, textWidth) + offsetX;
    }

    public int y(int screenHeight, int textHeight) {
        return anchor.y(screenHeight, textHeight) + offsetY;
    }
}
