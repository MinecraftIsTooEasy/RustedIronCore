package moddedmite.rustedironcore.core;

import moddedmite.rustedironcore.annotations.NotFinished;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.tags.TagKey;
import moddedmite.rustedironcore.util.RandomSource;

import java.util.Optional;

@NotFinished
public interface HolderGetter<T> extends HolderOwner<T> {
    Optional<Holder.Reference<T>> get(final ResourceKey<T> id);

    default Holder.Reference<T> getOrThrow(final ResourceKey<T> id) {
        return this.get(id).orElseThrow(() -> new IllegalStateException("Missing element " + id));
    }

    Optional<HolderSet.Named<T>> get(final TagKey<T> id);

    default HolderSet.Named<T> getOrThrow(final TagKey<T> id) {
        return this.get(id).orElseThrow(() -> new IllegalStateException("Missing tag " + id));
    }

    default Optional<Holder<T>> getRandomElementOf(final TagKey<T> tag, final RandomSource random) {
        return this.get(tag).flatMap(holderSet -> holderSet.getRandomElement(random));
    }

    interface Provider {
        <T> Optional<? extends HolderGetter<T>> lookup(final ResourceKey<? extends Registry<? extends T>> key);

        default <T> HolderGetter<T> lookupOrThrow(final ResourceKey<? extends Registry<? extends T>> key) {
            return (HolderGetter<T>)this.lookup(key).orElseThrow(() -> new IllegalStateException("Registry " + key.identifier() + " not found"));
        }

        default <T> Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
            return this.lookup(id.registryKey()).flatMap(l -> l.get(id));
        }

        default <T> Holder.Reference<T> getOrThrow(final ResourceKey<T> id) {
            return this.lookup(id.registryKey()).flatMap(l -> l.get(id)).orElseThrow(() -> new IllegalStateException("Missing element " + id));
        }

        default <T> Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
            return this.lookup(id.registry()).flatMap(l -> l.get(id));
        }

        default <T> HolderSet.Named<T> getOrThrow(final TagKey<T> id) {
            return this.lookup(id.registry()).flatMap(l -> l.get(id)).orElseThrow(() -> new IllegalStateException("Missing tag " + id));
        }
    }
}
