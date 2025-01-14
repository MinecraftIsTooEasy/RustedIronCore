package moddedmite.rustedironcore.api.world;

import java.util.HashMap;
import java.util.Map;

public record Dimension(String name, int id) {
    private static final Map<String, Dimension> STRING_TO_DIMENSION = new HashMap<>(4);
    private static final Map<Integer, Dimension> ID_TO_DIMENSION = new HashMap<>(4);

    public static Dimension register(String name, int id) {
        Dimension dimension = new Dimension(name, id);
        STRING_TO_DIMENSION.put(name, dimension);
        ID_TO_DIMENSION.put(id, dimension);
        return dimension;
    }

    public static Dimension fromString(String name) {
        return STRING_TO_DIMENSION.get(name);
    }

    public static Dimension fromId(int id) {
        return ID_TO_DIMENSION.get(id);
    }

    public String toString() {
        return this.name;
    }

    public static final Dimension OVERWORLD = register("overworld", 0);

    public static final Dimension NETHER = register("the_nether", -1);

    public static final Dimension END = register("the_end", 1);

    public static final Dimension UNDERWORLD = register("the_end", -2);
}