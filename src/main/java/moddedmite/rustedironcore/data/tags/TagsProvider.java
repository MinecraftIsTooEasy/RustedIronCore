package moddedmite.rustedironcore.data.tags;

import com.google.common.collect.Maps;
import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.core.HolderLookup;
import moddedmite.rustedironcore.core.Registry;
import moddedmite.rustedironcore.data.CachedOutput;
import moddedmite.rustedironcore.data.DataProvider;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.tags.TagBuilder;
import moddedmite.rustedironcore.tags.TagKey;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@NotVanilla
public abstract class TagsProvider<T> implements DataProvider {
    private final CompletableFuture<TagsProvider.TagLookup<T>> parentProvider;
    protected final ResourceKey<? extends Registry<T>> registryKey;
    private final Map<Identifier, TagBuilder> builders = Maps.newLinkedHashMap();

    protected TagsProvider(
            final ResourceKey<? extends Registry<T>> registryKey
    ) {
        this(registryKey, CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()));
    }

    protected TagsProvider(
            final ResourceKey<? extends Registry<T>> registryKey,
            final CompletableFuture<TagsProvider.TagLookup<T>> parentProvider
    ) {
        this.registryKey = registryKey;
        this.parentProvider = parentProvider;
    }

    protected abstract void addTags(HolderLookup.Provider registries);

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        this.builders.clear();
        this.addTags(new HolderLookup.Provider() {
        });
        this.builders.forEach((key, builder) -> Handlers.Tag.saveTags(this.registryKey, key, builder.build()));
        return CompletableFuture.completedFuture(null);
    }

    protected TagBuilder getOrCreateRawBuilder(final TagKey<T> tag) {
        return this.builders.computeIfAbsent(tag.location(), k -> TagBuilder.create());
    }

    protected TagAppender<T> tag(final TagKey<T> tag) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        return TagAppender.forBuilder(builder);
    }

    protected TagAppender<T> tag(final TagKey<T> tag, final boolean replace) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        builder.setReplace(replace);
        return TagAppender.forBuilder(builder);
    }

    @FunctionalInterface
    public interface TagLookup<T> extends Function<TagKey<T>, Optional<TagBuilder>> {
        static <T> TagsProvider.TagLookup<T> empty() {
            return id -> Optional.empty();
        }

        default boolean contains(final TagKey<T> key) {
            return this.apply(key).isPresent();
        }
    }
}
