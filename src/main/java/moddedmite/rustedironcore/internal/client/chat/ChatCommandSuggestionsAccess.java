package moddedmite.rustedironcore.internal.client.chat;

import moddedmite.rustedironcore.internal.command.CommandSuggestionResult;

public interface ChatCommandSuggestionsAccess {

    void ric$receiveCommandSuggestions(int requestId, String input, int cursor, CommandSuggestionResult result);

    int ric$getInputTextColor(int inputPosition, int defaultColor);

    String ric$getInputSuggestionSuffix();
}
