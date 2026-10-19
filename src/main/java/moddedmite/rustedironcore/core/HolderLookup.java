package moddedmite.rustedironcore.core;

import moddedmite.rustedironcore.annotations.NotFinished;

@NotFinished
public interface HolderLookup<T> extends HolderGetter<T> {

    interface Provider extends HolderGetter.Provider {
    }

    interface RegistryLookup<T> extends HolderLookup<T> {
    }
}
