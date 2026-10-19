package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.ITagListener;
import moddedmite.rustedironcore.core.Registry;
import moddedmite.rustedironcore.core.registries.BuiltInRegistries;
import moddedmite.rustedironcore.core.registries.Registries;
import moddedmite.rustedironcore.data.CachedOutput;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.tags.TagEntry;
import moddedmite.rustedironcore.tags.TagKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TagHandler extends AbstractHandler<ITagListener> {

    public void loadTags() {
        CachedOutput output = (path, input, hash) -> {
        };
        BuiltInRegistries.TAGS_PROVIDER.stream()
                .map(x -> x.run(output))
                .forEach(CompletableFuture::join);
    }

    /**
     * @param registryKey Registry
     * @param key         TagKey
     * @param entries     tag or thing
     * @param <T>         whatever
     */
    public <T> void saveTags(ResourceKey<? extends Registry<T>> registryKey, Identifier key, List<TagEntry> entries) {
        if (Registries.BLOCK.equals(registryKey)) {
            for (TagEntry entry : entries) {
                entry.build(id -> BuiltInRegistries.BLOCK.get(id).ifPresent(x -> x.addTag(TagKey.create(Registries.BLOCK, key))));
            }
        }
        if (Registries.ITEM.equals(registryKey)) {
            for (TagEntry entry : entries) {
                entry.build(id -> BuiltInRegistries.ITEM.get(id).ifPresent(x -> x.addTag(TagKey.create(Registries.ITEM, key))));
            }
        }
    }
}
