package moddedmite.rustedironcore.internal.network.packets;

import moddedmite.rustedironcore.internal.network.Packets;
import moddedmite.rustedironcore.internal.command.CommandSuggestionDispatcher;
import moddedmite.rustedironcore.internal.command.CommandSuggestionEntry;
import moddedmite.rustedironcore.internal.command.CommandSuggestionResult;
import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ICommandSender;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class C2SRequestChatSuggestions implements Packet {

    private static final int MAX_SUGGESTIONS = 256;
    private static final int MAX_INPUT_LENGTH = 256;
    private final int requestId;
    private final String input;
    private final int cursor;

    public C2SRequestChatSuggestions(int requestId, String input, int cursor) {
        this.requestId = requestId;
        this.input = input;
        this.cursor = cursor;
    }

    public C2SRequestChatSuggestions(PacketByteBuf buffer) {
        this(buffer.readInt(), buffer.readString(), buffer.readInt());
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

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeInt(this.requestId);
        buffer.writeString(this.input);
        buffer.writeInt(this.cursor);
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        if (this.input.length() > MAX_INPUT_LENGTH || this.cursor < 0 || this.cursor > this.input.length()) return;
        ICommandSender sender = (ICommandSender) entityPlayer;
        CommandSuggestionResult commandResult = CommandSuggestionDispatcher.suggest(this.input, this.cursor, sender);
        if (commandResult != null) {
            if (commandResult.suggestions().isEmpty()) {
                List<CommandSuggestionEntry> legacySuggestions = this.getLegacySuggestions(sender);
                if (!legacySuggestions.isEmpty()) {
                    commandResult = new CommandSuggestionResult(legacySuggestions, commandResult.syntax(),
                            commandResult.usage(), commandResult.usageStart(), commandResult.errorMessage());
                }
            }
            Network.sendToClient((ServerPlayer) entityPlayer,
                    new S2CChatSuggestions(this.requestId, this.input, this.cursor, commandResult));
            return;
        }

        List<CommandSuggestionEntry> suggestions = this.getLegacySuggestions(sender);
        Network.sendToClient((ServerPlayer) entityPlayer,
                new S2CChatSuggestions(this.requestId, this.input, this.cursor,
                        new CommandSuggestionResult(suggestions, List.of(), null)));
    }

    private List<CommandSuggestionEntry> getLegacySuggestions(ICommandSender sender) {
        String prefix = this.input.substring(0, this.cursor);
        List<?> possible = MinecraftServer.getServer().getPossibleCompletions(sender, prefix);
        if (possible == null) possible = List.of();
        List<CommandSuggestionEntry> suggestions = new ArrayList<>(Math.min(possible.size(), MAX_SUGGESTIONS));
        boolean completingCommand = prefix.startsWith("/") && !prefix.substring(1).contains(" ");
        int start = completingCommand ? 0 : CommandSuggestionRange.lastTokenStart(this.input, this.cursor);
        int end = CommandSuggestionRange.tokenEnd(this.input, this.cursor);
        for (Object value : possible) {
            if (suggestions.size() == MAX_SUGGESTIONS) break;
            String suggestion = value.toString();
            if (!suggestion.isEmpty() && !suggestion.equals(this.input)) {
                suggestions.add(new CommandSuggestionEntry(suggestion, null, start, end));
            }
        }
        return suggestions;
    }

    @Override
    public ResourceLocation getChannel() {
        return Packets.RequestChatSuggestions;
    }
}
