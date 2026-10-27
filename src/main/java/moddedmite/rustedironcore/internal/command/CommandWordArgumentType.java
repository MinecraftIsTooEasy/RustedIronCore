package moddedmite.rustedironcore.internal.command;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

final class CommandWordArgumentType implements ArgumentType<String> {

    private static final DynamicCommandExceptionType INVALID_VALUE = new DynamicCommandExceptionType(
            value -> new LiteralMessage("Invalid value: " + value));

    private final Supplier<? extends Collection<String>> values;
    private final Supplier<? extends Collection<String>> suggestions;

    private CommandWordArgumentType(Supplier<? extends Collection<String>> values,
                                    Supplier<? extends Collection<String>> suggestions) {
        this.values = values;
        this.suggestions = suggestions;
    }

    static CommandWordArgumentType choices(String... values) {
        return choices(List.of(values), List.of(values));
    }

    static CommandWordArgumentType choices(Collection<String> values, Collection<String> suggestions) {
        List<String> acceptedValues = List.copyOf(values);
        List<String> suggestedValues = List.copyOf(suggestions);
        return new CommandWordArgumentType(() -> acceptedValues, () -> suggestedValues);
    }

    static CommandWordArgumentType dynamic(Supplier<? extends Collection<String>> values) {
        return new CommandWordArgumentType(values, values);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        String value = reader.readUnquotedString();
        boolean accepted = this.values.get().stream().anyMatch(option -> option.equalsIgnoreCase(value));
        if (accepted) return value;
        reader.setCursor(start);
        throw INVALID_VALUE.createWithContext(reader, value);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (String value : this.suggestions.get()) {
            if (value.toLowerCase(Locale.ROOT).startsWith(remaining)) builder.suggest(value);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return this.values.get().stream().limit(3).toList();
    }
}
