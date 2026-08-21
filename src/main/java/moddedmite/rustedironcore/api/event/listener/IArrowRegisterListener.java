package moddedmite.rustedironcore.api.event.listener;

import moddedmite.rustedironcore.api.item.ArrowRegistry;
import moddedmite.rustedironcore.property.FloatProperty;
import net.minecraft.Material;

import java.util.function.Consumer;

/**
 * Register custom arrows through {@link ArrowRegistry}.
 */
public interface IArrowRegisterListener {

    /**
     * @deprecated Use {@link #onRegister(ArrowRegistry)}.
     */
    @Deprecated(since = "1.3.6")
    default void onRegister(FloatProperty<Material> registry) {
    }

    /**
     * @deprecated Use {@link #onRegister(ArrowRegistry)}.
     */
    @Deprecated(since = "1.5.7")
    default void onRegister(Consumer<Material> registry) {
    }

    /**
     * Register arrow materials and their texture resource domains.
     */
    default void onRegister(ArrowRegistry registry) {
        this.onRegister(registry::register);
    }
}
