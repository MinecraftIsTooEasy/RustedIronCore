package moddedmite.rustedironcore.registry;

import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class MappedRegistry<T> implements Registry<T> {
    private final Map<ResourceLocation, T> byId = new HashMap<>();
    private final Map<T, ResourceLocation> byValue = new IdentityHashMap<>();

    @Override
    public void register(ResourceLocation id, T value) {
        byId.put(id, value);
        byValue.put(value, id);
    }

    @Override
    public @Nullable ResourceLocation getKey(T thing) {
        return byValue.get(thing);
    }

    @Override
    public @Nullable T getValue(@Nullable ResourceLocation key) {
        return byId.get(key);
    }
}
