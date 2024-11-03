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


import huix.glacier.api.registry.sync.remappers.RegistryRemapper;
import huix.glacier.util.Identifier;
import net.minecraft.CompressedStreamTools;
import net.minecraft.NBTTagCompound;
import net.minecraft.PacketComponentBytes;

public interface RegistryRemapperAccess {
	Identifier PACKET_ID = new Identifier("legacy-fabric-api:registry_remap");

	RegistryRemapper<RegistryRemapper<?>> getRegistryRemapperRegistryRemapper();

	default void remap() {
		this.getRegistryRemapperRegistryRemapper().remap();

		for (RegistryRemapper<?> registryRemapper : this.getRegistryRemapperRegistryRemapper().getRegistry()) {
			registryRemapper.remap();
		}
	}

	default void readAndRemap(NBTTagCompound nbt) {
		this.getRegistryRemapperRegistryRemapper().readNbt(nbt.getCompoundTag(this.getRegistryRemapperRegistryRemapper().nbtName));

		for (RegistryRemapper<?> registryRemapper : this.getRegistryRemapperRegistryRemapper().getRegistry()) {
			registryRemapper.readNbt(nbt.getCompoundTag(registryRemapper.nbtName));
		}

		this.remap();
	}

	default NBTTagCompound toNbtCompound() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setCompoundTag(this.getRegistryRemapperRegistryRemapper().nbtName, this.getRegistryRemapperRegistryRemapper().toNbt());

		for (RegistryRemapper<?> registryRemapper : this.getRegistryRemapperRegistryRemapper().getRegistry()) {
			nbt.setCompoundTag(registryRemapper.nbtName, registryRemapper.toNbt());
		}

		return nbt;
	}

	default byte[] createBuf() {
		return CompressedStreamTools.compress(this.toNbtCompound());
	}

	void registrerRegistryRemapper(RegistryRemapper<?> registryRemapper);
}
