package moddedmite.rustedironcore.references;

import moddedmite.rustedironcore.core.registries.Registries;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.util.RegistryUtil;
import net.minecraft.Block;

public class BlockIds {

    public static ResourceKey<Block> from(Block block) {
        return ResourceKey.create(Registries.BLOCK, RegistryUtil.getId(block));
    }

    private static ResourceKey<Block> create(final String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.withDefaultNamespace(name));
    }
}
