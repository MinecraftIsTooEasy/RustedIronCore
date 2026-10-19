package moddedmite.rustedironcore.core;

import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@NotVanilla
public interface Registry<T> extends IdMap<T>, HolderLookup.RegistryLookup<T> {
    static <T> Registry<T> create() {
        return new MappedRegistry<>();
    }

    void register(ResourceKey<T> key, T value);

    @Nullable
    Identifier getKey(T thing);

    Optional<ResourceKey<T>> getResourceKey(T thing);

    @Nullable
    T getValue(@Nullable ResourceKey<T> key);

    @Nullable
    T getValue(@Nullable Identifier key);

    default Optional<T> getOptional(final @Nullable Identifier key) {
        return Optional.ofNullable(this.getValue(key));
    }

    default Optional<T> getOptional(final @Nullable ResourceKey<T> key) {
        return Optional.ofNullable(this.getValue(key));
    }

    Optional<Holder.Reference<T>> getAny();

    default Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    boolean containsKey(Identifier key);

    boolean containsKey(ResourceKey<T> key);

    Holder.Reference<T> createIntrusiveHolder(T value);

    Optional<Holder.Reference<T>> get(int id);

    Optional<Holder.Reference<T>> get(Identifier id);
}
