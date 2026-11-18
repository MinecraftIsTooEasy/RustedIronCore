package moddedmite.rustedironcore.api.advancement;

import net.minecraft.World;

import java.util.Map;

@FunctionalInterface
public interface BlockStateResolver {

    Map<String, String> resolve(World world, int x, int y, int z);
}
