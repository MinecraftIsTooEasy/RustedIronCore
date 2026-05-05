package moddedmite.rustedironcore.api.event.listener;

import moddedmite.rustedironcore.api.model.JsonModelRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IJsonModelListener {

    default void onJsonModelRegister(JsonModelRegistry registry) {
    }
}
