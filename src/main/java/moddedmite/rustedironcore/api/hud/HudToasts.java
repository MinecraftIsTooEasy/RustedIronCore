package moddedmite.rustedironcore.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.internal.hud.HudPacketCodec;
import net.minecraft.ResourceLocation;

public final class HudToasts {

    public static final ToastType<SystemToastData> SYSTEM = new ToastType<>(new ResourceLocation("ric", "system"),
            buffer -> new SystemToastData(new ResourceLocation(buffer.readString(), false),
                    HudPacketCodec.readText(buffer), HudPacketCodec.readOptionalText(buffer),
                    buffer.readLong(), buffer.readBoolean()),
            (buffer, data) -> {
                buffer.writeString(data.token().toString());
                buffer.writeString(TextComponentSerializer.toJson(data.title()));
                HudPacketCodec.writeOptionalText(buffer, data.message());
                buffer.writeLong(data.displayTimeMillis());
                buffer.writeBoolean(data.multiline());
            });
}
