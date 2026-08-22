package moddedmite.rustedironcore.api;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface RicPlugin {
    void onRegistry(RicRegistry registry);
}
