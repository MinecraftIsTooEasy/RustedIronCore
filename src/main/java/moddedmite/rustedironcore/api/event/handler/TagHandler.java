package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.ITagListener;
import moddedmite.rustedironcore.core.Holder;
import moddedmite.rustedironcore.core.HolderSet;
import moddedmite.rustedironcore.core.Registry;
import moddedmite.rustedironcore.core.registries.BuiltInRegistries;
import moddedmite.rustedironcore.tags.TagKey;
import moddedmite.rustedironcore.tags.TagLoader;

import java.util.Optional;
import java.util.stream.Stream;

public class TagHandler extends AbstractHandler<ITagListener> {

    public void loadTags() {
        TagLoader.loadProviders(BuiltInRegistries.BLOCK);
        TagLoader.loadProviders(BuiltInRegistries.ITEM);
    }

    public <T> Stream<T> getTagHolders(Registry<T> registry, TagKey<T> tagKey) {
        Optional<HolderSet.Named<T>> holders = registry.get(tagKey);
        return holders.isPresent() ? holders.get().stream().map(Holder::value) : Stream.of();
    }
}
