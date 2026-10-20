package moddedmite.rustedironcore.tags;

import moddedmite.rustedironcore.annotations.NotFinished;
import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.api.util.LogUtil;
import moddedmite.rustedironcore.core.*;
import moddedmite.rustedironcore.core.registries.BuiltInRegistries;
import moddedmite.rustedironcore.core.registries.Registries;
import moddedmite.rustedironcore.data.tags.TagsProvider;
import moddedmite.rustedironcore.datafixers.util.Either;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.util.DependencySorter;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@NotFinished
@NotVanilla
public class TagLoader<T> {
    private static final Logger LOGGER = LogUtil.getLogger();
    private final TagLoader.ElementLookup<T> elementLookup;
    private final String directory;

    public TagLoader(final TagLoader.ElementLookup<T> elementLookup, final String directory) {
        this.elementLookup = elementLookup;
        this.directory = directory;
    }

//    public Map<Identifier, List<TagLoader.EntryWithSource>> load(final ResourceManager resourceManager) {
//        Map<Identifier, List<TagLoader.EntryWithSource>> builders = new HashMap<>();
//        FileToIdConverter lister = FileToIdConverter.json(this.directory);
//
//        for (Map.Entry<Identifier, List<Resource>> entry : lister.listMatchingResourceStacks(resourceManager).entrySet()) {
//            Identifier location = entry.getKey();
//            Identifier id = lister.fileToId(location);
//
//            for (Resource resource : entry.getValue()) {
//                try (Reader reader = resource.openAsReader()) {
//                    JsonElement element = StrictJsonParser.parse(reader);
//                    List<TagLoader.EntryWithSource> tagContents = builders.computeIfAbsent(id, key -> new ArrayList<>());
//                    TagFile parsedContents = TagFile.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, element)).getOrThrow();
//                    if (parsedContents.replace()) {
//                        tagContents.clear();
//                    }
//
//                    String sourceId = resource.sourcePackId();
//                    parsedContents.entries().forEach(ex -> tagContents.add(new TagLoader.EntryWithSource(ex, sourceId)));
//                } catch (Exception e) {
//                    LOGGER.error("Couldn't read tag list {} from {} in data pack {}", id, location, resource.sourcePackId(), e);
//                }
//            }
//        }
//
//        return builders;
//    }

    @NotVanilla
    private Either<List<EntryWithSource>, List<T>> tryBuildTag(final TagEntry.Lookup<T> lookup, final List<TagLoader.EntryWithSource> entries) {
        Set<T> values = new LinkedHashSet<>();
        List<TagLoader.EntryWithSource> missingElements = new ArrayList<>();

        for (TagLoader.EntryWithSource entry : entries) {
            if (!entry.entry().build(lookup, values::add)) {
                missingElements.add(entry);
            }
        }

        return missingElements.isEmpty() ? Either.right(List.copyOf(values)) : Either.left(missingElements);
    }

    public Map<Identifier, List<T>> build(final Map<Identifier, List<TagLoader.EntryWithSource>> builders) {
        final Map<Identifier, List<T>> newTags = new HashMap<>();
        TagEntry.Lookup<T> lookup = new TagEntry.Lookup<T>() {
            @Override
            public @Nullable T element(final Identifier key, final boolean required) {
                return TagLoader.this.elementLookup.get(key, required).orElse(null);
            }

            @Override
            public @Nullable Collection<T> tag(final Identifier key) {
                return newTags.get(key);
            }
        };
        DependencySorter<Identifier, TagLoader.SortingEntry> sorter = new DependencySorter<>();
        builders.forEach((id, entry) -> sorter.addEntry(id, new TagLoader.SortingEntry(entry)));
        sorter.orderByDependencies(
                (id, contents) -> this.tryBuildTag(lookup, contents.entries)
                        .ifLeft(
                                missing -> LOGGER.error(
                                        "Couldn't load tag {} as it is missing following references: {}", id, missing.stream().map(Objects::toString).collect(Collectors.joining(", "))
                                )
                        )
                        .ifRight(tag -> newTags.put(id, (List<T>) tag))
        );
        return newTags;
    }
//
//    public static <T> Map<TagKey<T>, List<Holder<T>>> loadTagsFromNetwork(final TagNetworkSerialization.NetworkPayload tags, final Registry<T> registry) {
//        return tags.resolve(registry).tags;
//    }
//
//    public static List<Registry.PendingTags<?>> loadTagsForExistingRegistries(final ResourceManager manager, final RegistryAccess layer) {
//        return layer.registries().map(entry -> loadPendingTags(manager, entry.value())).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
//    }
//
//    public static <T> void loadTagsForRegistry(final ResourceManager manager, final WritableRegistry<T> registry) {
//        loadTagsForRegistry(manager, registry.key(), TagLoader.ElementLookup.fromWritableRegistry(registry));
//    }
//
//    public static <T> Map<TagKey<T>, List<Holder<T>>> loadTagsForRegistry(
//            final ResourceManager manager, final ResourceKey<? extends Registry<T>> registryKey, final TagLoader.ElementLookup<Holder<T>> lookup
//    ) {
//        TagLoader<Holder<T>> loader = new TagLoader<>(lookup, Registries.tagsDirPath(registryKey));
//        return wrapTags(registryKey, loader.build(loader.load(manager)));
//    }

    @NotVanilla
    public static <T> void loadProviders(final Registry<T> registry) {
        ResourceKey<? extends Registry<T>> registryKey = registry.key();

        TagLoader<Holder<T>> loader = new TagLoader<>(ElementLookup.fromFrozenRegistry(registry), Registries.tagsDirPath(registryKey));

        Map<Identifier, List<TagLoader.EntryWithSource>> builders = new HashMap<>();

        for (Map.Entry<ResourceKey<TagsProvider<?>>, TagsProvider<?>> entry : BuiltInRegistries.TAGS_PROVIDER.entrySet()) {
            TagsProvider<?> provider = entry.getValue();
            if (!provider.getRegistryKey().equals(registryKey)) {
                continue;
            }
            String source = entry.getKey().identifier().toString();
            provider.provide()
                    .forEach(result -> {
                        Identifier identifier = result.tagKey();
                        List<TagEntry> entries = result.entries();
                        List<EntryWithSource> tagContents = builders.computeIfAbsent(identifier, key -> new ArrayList<>());
                        entries.forEach(x -> tagContents.add(new EntryWithSource(x, source)));
                    });
        }

        Map<TagKey<T>, List<Holder<T>>> map = wrapTags(registryKey, loader.build(builders));

        LoadResult<T> result = new LoadResult<>(registryKey, map);

        Registry.PendingTags<T> pendingTags = registry.prepareTagReload(result);

        pendingTags.apply();
    }

    private static <T> Map<TagKey<T>, List<Holder<T>>> wrapTags(final ResourceKey<? extends Registry<T>> registryKey, final Map<Identifier, List<Holder<T>>> tags) {
        return tags.entrySet().stream().collect(Collectors.toUnmodifiableMap(e -> TagKey.create(registryKey, e.getKey()), Map.Entry::getValue));
    }

//    private static <T> Optional<Registry.PendingTags<T>> loadPendingTags(final ResourceManager manager, final Registry<T> registry) {
//        ResourceKey<? extends Registry<T>> key = registry.key();
//        TagLoader<Holder<T>> loader = new TagLoader<>(
//                (TagLoader.ElementLookup<Holder<T>>)TagLoader.ElementLookup.fromFrozenRegistry(registry), Registries.tagsDirPath(key)
//        );
//        TagLoader.LoadResult<T> tags = new TagLoader.LoadResult<>(key, wrapTags(registry.key(), loader.build(loader.load(manager))));
//        return tags.tags().isEmpty() ? Optional.empty() : Optional.of(registry.prepareTagReload(tags));
//    }

    public static List<HolderLookup.RegistryLookup<?>> buildUpdatedLookups(final RegistryAccess.Frozen registries, final List<Registry.PendingTags<?>> tags) {
        List<HolderLookup.RegistryLookup<?>> result = new ArrayList<>();
        registries.registries().forEach(lookup -> {
            Registry.PendingTags<?> foundTags = findTagsForRegistry(tags, lookup.key());
            result.add(foundTags != null ? foundTags.lookup() : lookup.value());
        });
        return result;
    }

    private static Registry.@Nullable PendingTags<?> findTagsForRegistry(
            final List<Registry.PendingTags<?>> tags, final ResourceKey<? extends Registry<?>> registryKey
    ) {
        for (Registry.PendingTags<?> tag : tags) {
            if (tag.key() == registryKey) {
                return tag;
            }
        }

        return null;
    }

    public interface ElementLookup<T> {
        Optional<? extends T> get(Identifier id, boolean required);

        static <T> TagLoader.ElementLookup<? extends Holder<T>> fromFrozenRegistry(final Registry<T> registry) {
            return (id, required) -> registry.get(id);
        }

        static <T> TagLoader.ElementLookup<Holder<T>> fromWritableRegistry(final WritableRegistry<T> registry) {
            return fromGetters(registry.key(), registry.createRegistrationLookup(), registry);
        }

        static <T> TagLoader.ElementLookup<Holder<T>> fromGetters(
                final ResourceKey<? extends Registry<T>> registryKey, final HolderGetter<T> writable, final HolderGetter<T> immutable
        ) {
            return (id, required) -> (required ? writable : immutable).get(ResourceKey.create(registryKey, id));
        }
    }

    public record EntryWithSource(TagEntry entry, String source) {
        @Override
        public String toString() {
            return this.entry + " (from " + this.source + ")";
        }
    }

    public record LoadResult<T>(ResourceKey<? extends Registry<T>> key, Map<TagKey<T>, List<Holder<T>>> tags) {
    }

    private record SortingEntry(List<TagLoader.EntryWithSource> entries) implements DependencySorter.Entry<Identifier> {
        @Override
        public void visitRequiredDependencies(final Consumer<Identifier> output) {
            this.entries.forEach(e -> e.entry.visitRequiredDependencies(output));
        }

        @Override
        public void visitOptionalDependencies(final Consumer<Identifier> output) {
            this.entries.forEach(e -> e.entry.visitOptionalDependencies(output));
        }
    }
}
