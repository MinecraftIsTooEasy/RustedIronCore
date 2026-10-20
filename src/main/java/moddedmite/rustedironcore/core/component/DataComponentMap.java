package moddedmite.rustedironcore.core.component;

import moddedmite.rustedironcore.annotations.NotFinished;
import org.jspecify.annotations.Nullable;

@NotFinished
public interface DataComponentMap extends /*Iterable<TypedDataComponent<?>>,*/ DataComponentGetter {
    DataComponentMap EMPTY = new DataComponentMap() {

        @Override
        public @Nullable <T> T get(DataComponentType<? extends T> type) {
            return null;
        }
    };
}
