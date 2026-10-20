package moddedmite.rustedironcore.core;

import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.tags.TagKey;

import java.util.List;
import java.util.Map;

public interface WritableRegistry<T> extends Registry<T> {
	Holder.Reference<T> register(ResourceKey<T> key, T value, RegistrationInfo registrationInfo);

	void bindTags(Map<TagKey<T>, List<Holder<T>>> pendingTags);

	boolean isEmpty();

	HolderGetter<T> createRegistrationLookup();
}
