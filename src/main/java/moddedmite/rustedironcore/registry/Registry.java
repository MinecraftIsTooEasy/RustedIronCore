package moddedmite.rustedironcore.registry;

import net.minecraft.ResourceLocation;

import javax.annotation.Nullable;

public interface Registry<T> {
    static <T> Registry<T> create() {
        return new MappedRegistry<>();
    }

    void register(ResourceLocation id, T value);

    @Nullable
    ResourceLocation getKey(T thing);

    @Nullable
    T getValue(@Nullable ResourceLocation key);

}
