package moddedmite.rustedironcore.core;

import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.serialization.Lifecycle;
import moddedmite.rustedironcore.util.RandomSource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NotVanilla
public class DefaultedMappedRegistry<T> extends MappedRegistry<T> implements DefaultedRegistry<T> {
    private final Identifier defaultKey;
    private Holder.Reference<T> defaultValue;

    public DefaultedMappedRegistry(
            final String defaultKey, final ResourceKey<? extends Registry<T>> key, final Lifecycle lifecycle, final boolean intrusiveHolders
    ) {
        super(key, lifecycle, intrusiveHolders);
        this.defaultKey = Identifier.parse(defaultKey);
    }

    @Override
    public Holder.Reference<T> register(final ResourceKey<T> key, final T value, final RegistrationInfo registrationInfo) {
        Holder.Reference<T> result = super.register(key, value, registrationInfo);
        if (this.defaultKey.equals(key.identifier())) {
            this.defaultValue = result;
        }

        return result;
    }

    @Override
    public int getId(final @Nullable T thing) {
        int id = super.getId(thing);
        return id == -1 ? super.getId(this.defaultValue.value()) : id;
    }

    @Override
    public @NonNull Identifier getKey(final T thing) {
        Identifier k = super.getKey(thing);
        return k == null ? this.defaultKey : k;
    }

    @Override
    public @NonNull T getValue(final @Nullable Identifier key) {
        T t = super.getValue(key);
        return t == null ? this.defaultValue.value() : t;
    }

    @Override
    public Optional<T> getOptional(final @Nullable Identifier key) {
        return Optional.ofNullable(super.getValue(key));
    }

    @Override
    public Optional<Holder.Reference<T>> getAny() {
        return Optional.ofNullable(this.defaultValue);
    }

    @Override
    public @NonNull T byId(final int id) {
        T t = super.byId(id);
        return t == null ? this.defaultValue.value() : t;
    }

    @Override
    public Optional<Holder.Reference<T>> getRandom(final RandomSource random) {
        return super.getRandom(random).or(() -> Optional.of(this.defaultValue));
    }

    @Override
    public Identifier getDefaultKey() {
        return this.defaultKey;
    }
}
