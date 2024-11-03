/*
 * Copyright (c) 2020 - 2024 Legacy Fabric
 * Copyright (c) 2016 - 2022 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package huix.glacier.api.registry.sync;

import huix.glacier.api.registry.RegistryIds;
import huix.glacier.event.Event;
import huix.glacier.event.EventFactory;
import huix.glacier.util.Identifier;
import net.minecraft.Block;
import net.minecraft.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows registration of Blocks, Items, Block Entity Classes, Status Effects and Enchantments.
 */
public final class RegistryHelper {
	public static final Map<Identifier, Event<RegistryInitialized>> IDENTIFIER_EVENT_MAP = new HashMap<>();

	public static Event<RegistryInitialized> onRegistryInitialized(Identifier identifier) {
		Event<RegistryInitialized> event;

		if (IDENTIFIER_EVENT_MAP.containsKey(identifier)) {
			event = IDENTIFIER_EVENT_MAP.get(identifier);
		} else {
			event = EventFactory.createArrayBacked(RegistryInitialized.class,
				(callbacks) -> () -> {
					for (RegistryInitialized callback : callbacks) {
						callback.initialized();
					}
				}
			);
			IDENTIFIER_EVENT_MAP.put(identifier, event);
		}

		return event;
	}

	/**
	 * Registers a block with the given ID.
	 *
	 * <p>The Block's translation key is automatically set.</p>
	 *
	 * @param block The block to register
	 * @param id    The ID of the block
	 * @return The block registered
	 */
	public static Block registerBlock(Block block, Identifier id) {
		return RegistryHelperImpl.registerBlock(block, id);
	}

	public static Block getBlock(Identifier id) {
		return RegistryHelperImpl.getValue(id, RegistryIds.BLOCKS);
	}

	/**
	 * Registers an item with the given ID.
	 *
	 * <p>The Item's translation key is automatically set.</p>
	 *
	 * @param item The item to register
	 * @param id   The ID of the item
	 * @return The item registered
	 */
	public static Item registerItem(Item item, Identifier id) {
		return RegistryHelperImpl.registerItem(item, id);
	}

	public static Item getItem(Identifier id) {
		return RegistryHelperImpl.getValue(id, RegistryIds.ITEMS);
	}

	@FunctionalInterface
	public interface EntryCreator<T> {
		T create(int rawId);
	}

	@FunctionalInterface
	public interface RegistryInitialized {
		void initialized();
	}
}
