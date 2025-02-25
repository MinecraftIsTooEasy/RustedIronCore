package moddedmite.rustedironcore.api.world;

import net.minecraft.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This is only an identifier, refer {@link DimensionContext} for world generations.
 */
public record Dimension(String name, int id) {
    private static final Map<String, Dimension> STRING_TO_DIMENSION = new HashMap<>(4);
    private static final Map<Integer, Dimension> ID_TO_DIMENSION = new HashMap<>(4);

    public static Dimension register(String name, int id) {
        return register(new Dimension(name, id));
    }

    public static Dimension register(Dimension dimension) {
        String name = dimension.name();
        int id = dimension.id();
        STRING_TO_DIMENSION.put(name, dimension);
        ID_TO_DIMENSION.put(id, dimension);
        return dimension;
    }

    @Nullable
    public static Dimension fromString(String name) {
        return STRING_TO_DIMENSION.get(name);
    }

    @Nullable
    public static Dimension fromId(int id) {
        return ID_TO_DIMENSION.get(id);
    }

    public boolean isOf(World world) {
        return world.getDimensionId() == this.id;
    }

    public static Stream<Dimension> streamDimensions() {
        return STRING_TO_DIMENSION.values().stream();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static final Dimension OVERWORLD = register("overworld", 0);

    public static final Dimension NETHER = register("the_nether", -1);

    public static final Dimension END = register("the_end", 1);

    public static final Dimension UNDERWORLD = register("the_end", -2);
}
