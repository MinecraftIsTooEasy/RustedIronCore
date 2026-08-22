package moddedmite.rustedironcore.client.api;

import moddedmite.rustedironcore.client.registry.RicClientRegistryImpl;

public interface RicClientRegistry {
    static RicClientRegistry getInstance() {
        return RicClientRegistryImpl.INSTANCE;
    }
}
