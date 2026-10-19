package moddedmite.rustedironcore.references;

import moddedmite.rustedironcore.core.registries.Registries;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.util.RegistryUtil;
import net.minecraft.Item;

public class ItemIds {

    public static ResourceKey<Item> from(Item item) {
        return ResourceKey.create(Registries.ITEM, RegistryUtil.getId(item));
    }

    private static ResourceKey<Item> create(final String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.withDefaultNamespace(name));
    }
}
