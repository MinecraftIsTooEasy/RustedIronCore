package huix.glacier.api.registry;

import huix.glacier.api.registry.sync.RegistryHelperImpl;
import huix.glacier.api.registry.sync.compat.RegistriesGetter;
import huix.glacier.api.registry.sync.compat.SimpleRegistryCompat;
import huix.glacier.registry.GlacierBlocks;
import huix.glacier.registry.GlacierItems;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.Block;
import net.minecraft.Item;

public class RegistrySyncEarlyInitializer  implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        RegistryHelperImpl.registriesGetter = new RegistriesGetter() {

            @Override
            public <K> SimpleRegistryCompat<K, Block> getBlockRegistry() {
                return (SimpleRegistryCompat<K, Block>) GlacierBlocks.REGISTRY;
            }

            @Override
            public <K> SimpleRegistryCompat<K, Item> getItemRegistry() {
                return (SimpleRegistryCompat<K, Item>) GlacierItems.REGISTRY;
            }
        };

    }
}
