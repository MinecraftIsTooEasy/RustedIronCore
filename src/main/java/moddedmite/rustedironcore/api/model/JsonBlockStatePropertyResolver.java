package moddedmite.rustedironcore.api.model;

import net.minecraft.Block;
import net.minecraft.IBlockAccess;

import java.util.Map;

@FunctionalInterface
public interface JsonBlockStatePropertyResolver {
    void addProperties(Block block, IBlockAccess blockAccess, int x, int y, int z, int metadata, Map<String, String> properties);
}
