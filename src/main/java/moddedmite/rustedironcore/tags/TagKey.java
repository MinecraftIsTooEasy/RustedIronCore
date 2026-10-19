package moddedmite.rustedironcore.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import moddedmite.rustedironcore.core.Registry;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;

@SuppressWarnings("UnstableApiUsage")
public record TagKey<T>(ResourceKey<? extends Registry<T>> registry, Identifier location) {
    private static final Interner<TagKey<?>> VALUES = Interners.newWeakInterner();

    @SuppressWarnings("unchecked")
    public static <T> TagKey<T> create(final ResourceKey<? extends Registry<T>> registry, final Identifier location) {
        return (TagKey<T>) VALUES.intern(new TagKey<>(registry, location));
    }
}
