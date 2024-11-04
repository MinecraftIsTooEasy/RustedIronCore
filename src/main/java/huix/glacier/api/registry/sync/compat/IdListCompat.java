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

package huix.glacier.api.registry.sync.compat;


import huix.glacier.registry.util.ObjectIdIterable;

import java.util.IdentityHashMap;
import java.util.List;

public interface IdListCompat<V> extends ObjectIdIterable<V> {
	<K> IdentityHashMap<V, Integer> getIdMap(SimpleRegistryCompat<K, V> simpleRegistry);

	List<V> getList();

	Object fromInt(int index);

	void setValue(V value, int index);

	int getInt(V value);

	IdListCompat<V> createIdList();
}