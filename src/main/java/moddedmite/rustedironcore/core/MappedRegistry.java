package moddedmite.rustedironcore.core;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.api.util.LogUtil;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NotVanilla
public class MappedRegistry<T> implements Registry<T> {
    private static final Logger LOGGER = LogUtil.getLogger();
    private final ObjectList<Holder.Reference<T>> byId = new ObjectArrayList<>(256);
    private final Reference2IntMap<T> toId = new Reference2IntOpenHashMap<>() {
        {
            this.defaultReturnValue(-1);
        }
    };
    private final Map<Identifier, Holder.Reference<T>> byLocation = new HashMap<>();
    private final Map<ResourceKey<T>, Holder.Reference<T>> byKey = new HashMap<>();
    private final Map<T, Holder.Reference<T>> byValue = new IdentityHashMap<>();
    private boolean frozen;
    private @Nullable Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

    public MappedRegistry() {
        this(false);
    }

    public MappedRegistry(final boolean intrusiveHolders) {
        if (intrusiveHolders) {
            this.unregisteredIntrusiveHolders = new IdentityHashMap<>();
        }
    }

    private void validateWrite() {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen");
        }
    }

    private void validateWrite(final ResourceKey<T> key) {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen (trying to add key " + key + ")");
        }
    }

    @NotVanilla
    @Override
    public void register(final ResourceKey<T> key, final T value) {
        this.validateWrite(key);
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        if (this.byKey.containsKey(key)) {
            LOGGER.error("duplicate key {} for values {} and {}", key, this.byKey.get(key), value);
            throw new IllegalStateException();
        }

        if (this.byValue.containsKey(value)) {
            LOGGER.error("value exists for {}", value);
            throw new IllegalStateException();
        }

        Holder.Reference<T> holder;
        if (this.unregisteredIntrusiveHolders != null) {
            holder = this.unregisteredIntrusiveHolders.remove(value);
            if (holder == null) {
                throw new AssertionError("Missing intrusive holder for " + key + ":" + value);
            }

            holder.bindKey(key);
        } else {
            holder = this.byKey.computeIfAbsent(key, k -> Holder.Reference.createStandAlone(this, (ResourceKey<T>) k));
        }

        this.byKey.put(key, holder);
        this.byLocation.put(key.identifier(), holder);
        this.byValue.put(value, holder);
        int newId = this.byId.size();
        this.byId.add(holder);
        this.toId.put(value, newId);
//        this.registrationInfos.put(key, registrationInfo);
//        this.registryLifecycle = this.registryLifecycle.add(registrationInfo.lifecycle());
//        return holder;
        this.byValue.forEach((v, h) -> h.bindValue(v));
    }

    @Override
    public @Nullable Identifier getKey(final T thing) {
        Holder.Reference<T> holder = this.byValue.get(thing);
        return holder != null ? holder.key().identifier() : null;
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(final T thing) {
        return Optional.ofNullable(this.byValue.get(thing)).map(Holder.Reference::key);
    }

    @Override
    public int getId(final @Nullable T thing) {
        return this.toId.getInt(thing);
    }

    @Override
    public @Nullable T getValue(final @Nullable ResourceKey<T> key) {
        return getValueFromNullable(this.byKey.get(key));
    }

    @Override
    public @Nullable T byId(final int id) {
        return id >= 0 && id < this.byId.size() ? this.byId.get(id).value() : null;
    }
    @Override
    public Optional<Holder.Reference<T>> get(final int id) {
        return id >= 0 && id < this.byId.size() ? Optional.ofNullable(this.byId.get(id)) : Optional.empty();
    }

    @Override
    public Optional<Holder.Reference<T>> get(final Identifier id) {
        return Optional.ofNullable(this.byLocation.get(id));
    }

    @Override
    public Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
        return Optional.ofNullable(this.byKey.get(id));
    }

    @NotVanilla
    @Override
    public Optional<Holder.Reference<T>> getAny() {
        return this.byId.isEmpty() ? Optional.empty() : Optional.of(this.byId.get(0));
    }

    @Override
    public int size() {
        return this.byKey.size();
    }

    @Override
    public @NonNull Iterator<T> iterator() {
        return Iterators.transform(this.byId.iterator(), Holder::value);
    }

    @Override
    public @Nullable T getValue(final @Nullable Identifier key) {
        Holder.Reference<T> result = this.byLocation.get(key);
        return getValueFromNullable(result);
    }

    private static <T> @Nullable T getValueFromNullable(final Holder.@Nullable Reference<T> result) {
        return result != null ? result.value() : null;
    }

    @Override
    public boolean containsKey(final Identifier key) {
        return this.byLocation.containsKey(key);
    }

    @Override
    public boolean containsKey(final ResourceKey<T> key) {
        return this.byKey.containsKey(key);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public Holder.Reference<T> createIntrusiveHolder(final T value) {
        if (this.unregisteredIntrusiveHolders == null) {
            throw new IllegalStateException("This registry can't create intrusive holders");
        }

        this.validateWrite();
        return this.unregisteredIntrusiveHolders.computeIfAbsent(value, v -> Holder.Reference.createIntrusive(this, v));
    }
}
