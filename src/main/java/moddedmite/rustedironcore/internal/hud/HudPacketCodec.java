package moddedmite.rustedironcore.internal.hud;

import moddedmite.rustedironcore.api.chat.ChatComponents;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.api.hud.HintAnchor;
import moddedmite.rustedironcore.api.hud.HintPosition;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ServerPlayer;

import java.util.UUID;

public final class HudPacketCodec {

    public static void writeText(PacketByteBuf buffer, TextComponent text, EntityPlayer recipient) {
        TextComponent resolved = recipient instanceof ServerPlayer
                ? ChatComponents.resolveFor(text, recipient, recipient) : text;
        buffer.writeString(TextComponentSerializer.toJson(resolved));
    }

    public static TextComponent readText(PacketByteBuf buffer) {
        return TextComponentSerializer.fromJson(buffer.readString());
    }

    public static void writeHintPosition(PacketByteBuf buffer, HintPosition position) {
        buffer.writeByte(position.anchor().ordinal());
        buffer.writeInt(position.offsetX());
        buffer.writeInt(position.offsetY());
    }

    public static HintPosition readHintPosition(PacketByteBuf buffer) {
        return new HintPosition(HintAnchor.values()[buffer.readUnsignedByte()], buffer.readInt(), buffer.readInt());
    }

    public static void writeOptionalText(PacketByteBuf buffer, TextComponent text) {
        buffer.writeBoolean(text != null);
        if (text != null) buffer.writeString(TextComponentSerializer.toJson(text));
    }

    public static TextComponent readOptionalText(PacketByteBuf buffer) {
        return buffer.readBoolean() ? readText(buffer) : null;
    }

    public static void writeUuid(PacketByteBuf buffer, UUID id) {
        buffer.writeLong(id.getMostSignificantBits());
        buffer.writeLong(id.getLeastSignificantBits());
    }

    public static UUID readUuid(PacketByteBuf buffer) {
        return new UUID(buffer.readLong(), buffer.readLong());
    }
}
