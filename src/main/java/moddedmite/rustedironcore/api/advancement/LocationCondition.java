package moddedmite.rustedironcore.api.advancement;

import net.minecraft.World;

/** Checks a registered structure or other location supplied by an extension. */
@FunctionalInterface
public interface LocationCondition {

    boolean matches(World world, int x, int y, int z);
}
