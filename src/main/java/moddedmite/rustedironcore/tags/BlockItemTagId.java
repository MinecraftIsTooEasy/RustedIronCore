package moddedmite.rustedironcore.tags;

import moddedmite.rustedironcore.core.registries.Registries;
import moddedmite.rustedironcore.resources.Identifier;
import net.minecraft.Block;
import net.minecraft.Item;

public record BlockItemTagId(TagKey<Block> block, TagKey<Item> item) {
    public static BlockItemTagId create(final Identifier blockId, final Identifier itemId) {
        return new BlockItemTagId(TagKey.create(Registries.BLOCK, blockId), TagKey.create(Registries.ITEM, itemId));
    }

    public static BlockItemTagId create(final String blockName, final String itemName) {
        return create(Identifier.withDefaultNamespace(blockName), Identifier.withDefaultNamespace(itemName));
    }

    public static BlockItemTagId create(final String name) {
        Identifier id = Identifier.withDefaultNamespace(name);
        return create(id, id);
    }
}
