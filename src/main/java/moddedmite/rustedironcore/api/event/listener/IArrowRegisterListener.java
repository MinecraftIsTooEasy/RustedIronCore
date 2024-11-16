package moddedmite.rustedironcore.api.event.listener;

import moddedmite.rustedironcore.property.FloatProperty;
import net.minecraft.Material;

import java.util.function.Consumer;

public interface IArrowRegisterListener {
    @Deprecated(since = "1.3.6")
    default void onRegister(FloatProperty<Material> registry) {
    }

    default void onRegister(Consumer<Material> registry) {
    }
}
