package moddedmite.rustedironcore.internal.network.packets;

import moddedmite.rustedironcore.internal.client.chat.ChatCommandSuggestionsAccess;
import moddedmite.rustedironcore.internal.command.CommandSuggestionEntry;
import moddedmite.rustedironcore.internal.command.CommandSuggestionResult;
import moddedmite.rustedironcore.internal.command.CommandSyntaxKind;
import moddedmite.rustedironcore.internal.command.CommandSyntaxSpan;
import moddedmite.rustedironcore.internal.network.Packets;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.GuiChat;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class S2CChatSuggestions implements Packet {

    private static final int MAX_SUGGESTIONS = 256;
    private static final int MAX_SYNTAX_SPANS = 128;
    private static final int MAX_USAGE_LINES = 64;
    private final int requestId;
    private final String input;
    private final int cursor;
    private final CommandSuggestionResult result;

    public S2CChatSuggestions(int requestId, String input, int cursor, CommandSuggestionResult result) {
        this.requestId = requestId;
        this.input = input;
        this.cursor = cursor;
        this.result = new CommandSuggestionResult(
                result.suggestions().subList(0, Math.min(result.suggestions().size(), MAX_SUGGESTIONS)),
                result.syntax().subList(0, Math.min(result.syntax().size(), MAX_SYNTAX_SPANS)),
                result.usage().subList(0, Math.min(result.usage().size(), MAX_USAGE_LINES)),
                result.usageStart(),
                result.errorMessage());
    }

    public S2CChatSuggestions(PacketByteBuf buffer) {
        this.requestId = buffer.readInt();
        this.input = buffer.readString();
        this.cursor = buffer.readInt();
        int count = Math.max(0, Math.min(buffer.readVarInt(), MAX_SUGGESTIONS));
        List<CommandSuggestionEntry> suggestions = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            String text = buffer.readString();
            String tooltip = buffer.readBoolean() ? buffer.readString() : null;
            suggestions.add(new CommandSuggestionEntry(text, tooltip, buffer.readInt(), buffer.readInt()));
        }
        int spanCount = Math.max(0, Math.min(buffer.readVarInt(), MAX_SYNTAX_SPANS));
        List<CommandSyntaxSpan> syntax = new ArrayList<>(spanCount);
        for (int index = 0; index < spanCount; index++) {
            int start = buffer.readInt();
            int end = buffer.readInt();
            int kind = buffer.readUnsignedByte();
            int styleIndex = buffer.readUnsignedByte();
            if (kind < CommandSyntaxKind.values().length) {
                syntax.add(new CommandSyntaxSpan(start, end, CommandSyntaxKind.values()[kind], styleIndex));
            }
        }
        int usageCount = Math.max(0, Math.min(buffer.readVarInt(), MAX_USAGE_LINES));
        List<String> usage = new ArrayList<>(usageCount);
        for (int index = 0; index < usageCount; index++) usage.add(buffer.readString());
        int usageStart = buffer.readInt();
        String error = buffer.readBoolean() ? buffer.readString() : null;
        this.result = new CommandSuggestionResult(suggestions, syntax, usage, usageStart, error);
    }

    int requestId() {
        return this.requestId;
    }

    String input() {
        return this.input;
    }

    int cursor() {
        return this.cursor;
    }

    CommandSuggestionResult result() {
        return this.result;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeInt(this.requestId);
        buffer.writeString(this.input);
        buffer.writeInt(this.cursor);
        buffer.writeVarInt(this.result.suggestions().size());
        for (CommandSuggestionEntry suggestion : this.result.suggestions()) {
            buffer.writeString(suggestion.text());
            buffer.writeBoolean(suggestion.tooltip() != null);
            if (suggestion.tooltip() != null) buffer.writeString(suggestion.tooltip());
            buffer.writeInt(suggestion.start());
            buffer.writeInt(suggestion.end());
        }
        buffer.writeVarInt(this.result.syntax().size());
        for (CommandSyntaxSpan span : this.result.syntax()) {
            buffer.writeInt(span.start());
            buffer.writeInt(span.end());
            buffer.writeByte(span.kind().ordinal());
            buffer.writeByte(span.styleIndex());
        }
        buffer.writeVarInt(this.result.usage().size());
        for (String usage : this.result.usage()) buffer.writeString(usage);
        buffer.writeInt(this.result.usageStart());
        buffer.writeBoolean(this.result.errorMessage() != null);
        if (this.result.errorMessage() != null) buffer.writeString(this.result.errorMessage());
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.currentScreen instanceof GuiChat chat) {
            ((ChatCommandSuggestionsAccess) chat).ric$receiveCommandSuggestions(
                    this.requestId, this.input, this.cursor, this.result);
        }
    }

    @Override
    public ResourceLocation getChannel() {
        return Packets.ChatSuggestions;
    }
}
