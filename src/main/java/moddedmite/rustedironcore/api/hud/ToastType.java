package moddedmite.rustedironcore.api.hud;

import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Function;

/** Common-side toast payload codec. Register its renderer through client ToastManager.registerType. */
public final class ToastType<T> {

    private final ResourceLocation id;
    private final Function<PacketByteBuf, T> reader;
    private final BiConsumer<PacketByteBuf, T> writer;

    public ToastType(ResourceLocation id, Function<PacketByteBuf, T> reader, BiConsumer<PacketByteBuf, T> writer) {
        this.id = id;
        this.reader = reader;
        this.writer = writer;
    }

    public ResourceLocation getId() {
        return id;
    }

    public T read(PacketByteBuf buffer) {
        return reader.apply(buffer);
    }

    public void write(PacketByteBuf buffer, T value) {
        writer.accept(buffer, value);
    }
}
