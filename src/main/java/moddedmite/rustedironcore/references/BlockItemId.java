package moddedmite.rustedironcore.references;

import moddedmite.rustedironcore.core.registries.Registries;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import net.minecraft.Block;
import net.minecraft.Item;

public record BlockItemId(ResourceKey<Block> block, ResourceKey<Item> item) {
	public static BlockItemId create(final Identifier blockId, final Identifier itemId) {
		return new BlockItemId(ResourceKey.create(Registries.BLOCK, blockId), ResourceKey.create(Registries.ITEM, itemId));
	}

	public static BlockItemId create(final String blockName, final String itemName) {
		return create(Identifier.withDefaultNamespace(blockName), Identifier.withDefaultNamespace(itemName));
	}

	public static BlockItemId create(final String name) {
		Identifier id = Identifier.withDefaultNamespace(name);
		return create(id, id);
	}
}
