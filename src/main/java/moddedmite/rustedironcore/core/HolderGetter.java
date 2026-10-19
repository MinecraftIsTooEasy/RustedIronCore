package moddedmite.rustedironcore.core;

import moddedmite.rustedironcore.annotations.NotFinished;
import moddedmite.rustedironcore.resources.ResourceKey;

import java.util.Optional;

@NotFinished
public interface HolderGetter<T> extends HolderOwner<T> {
    Optional<Holder.Reference<T>> get(final ResourceKey<T> id);

    interface Provider {
    }
}
