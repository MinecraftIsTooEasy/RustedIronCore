package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.keybinding.KeybindingV1;
import net.minecraft.KeyBinding;
import net.minecraft.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;

public class KeybindingRegisterEvent {
    private final Consumer<KeyBinding> registry;

    public KeybindingRegisterEvent() {
        this(Handlers.Keybinding::registerLegacyKeybinding);
    }

    public KeybindingRegisterEvent(Consumer<KeyBinding> registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    public void register(KeybindingV1 keybinding) {
        this.registry.accept(keybinding);
    }

    /**
     * @deprecated Use {@link #register(KeybindingV1)} for category and default-key metadata.
     */
    @Deprecated
    public void register(KeyBinding keybinding) {
        this.registry.accept(keybinding);
    }

    public KeybindingV1.Category registerCategory(ResourceLocation id) {
        return KeybindingV1.Category.register(id);
    }
}
