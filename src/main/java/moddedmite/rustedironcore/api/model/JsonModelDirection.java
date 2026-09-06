package moddedmite.rustedironcore.api.model;

public enum JsonModelDirection {
    DOWN(0),
    UP(1),
    NORTH(2),
    SOUTH(3),
    WEST(4),
    EAST(5);

    private final int side;

    JsonModelDirection(int side) {
        this.side = side;
    }

    public int getSide() {
        return this.side;
    }

    public static JsonModelDirection fromName(String name) {
        return switch (name) {
            case "down" -> DOWN;
            case "up" -> UP;
            case "north" -> NORTH;
            case "south" -> SOUTH;
            case "west" -> WEST;
            case "east" -> EAST;
            default -> null;
        };
    }
}
