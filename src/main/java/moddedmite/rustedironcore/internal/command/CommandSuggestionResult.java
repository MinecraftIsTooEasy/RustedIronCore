package moddedmite.rustedironcore.internal.command;

import java.util.List;

public record CommandSuggestionResult(List<CommandSuggestionEntry> suggestions, List<CommandSyntaxSpan> syntax, List<String> usage, int usageStart, String errorMessage) {

    public CommandSuggestionResult(List<CommandSuggestionEntry> suggestions, List<CommandSyntaxSpan> syntax, String errorMessage) {
        this(suggestions, syntax, List.of(), 0, errorMessage);
    }

    public CommandSuggestionResult {
        suggestions = List.copyOf(suggestions);
        syntax = List.copyOf(syntax);
        usage = List.copyOf(usage);
    }
}
