package moddedmite.rustedironcore.internal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ICommand;
import net.minecraft.ICommandSender;
import net.minecraft.Minecraft;
import moddedmite.rustedironcore.internal.network.packets.CommandSuggestionRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CommandSuggestionDispatcher {

    private static final int MAX_SUGGESTIONS = 256;
    private static final Set<String> DEV_ONLY_COMMANDS = Set.of("time", "gamemode", "difficulty", "defaultgamemode", "toggledownfall", "weather", "xp", "effect", "enchant", "gamerule", "clear", "give");
    private static CommandDispatcher<ICommandSender> dispatcher = new CommandDispatcher<>();
    private static final List<LiteralArgumentBuilder<ICommandSender>> pendingBuiltinTrees = new ArrayList<>();
    private static final List<LiteralArgumentBuilder<ICommandSender>> pendingExternalTrees = new ArrayList<>();
    private static final List<LiteralArgumentBuilder<ICommandSender>> pendingStandaloneTrees = new ArrayList<>();
    private static Map<String, ICommand> legacyCommands = Map.of();

    public static Map<String, ICommand> getLegacyCommands() {
        return legacyCommands;
    }

    public static void beginRegistration() {
        dispatcher = new CommandDispatcher<>();
        pendingBuiltinTrees.clear();
        pendingExternalTrees.clear();
        pendingStandaloneTrees.clear();
        legacyCommands = Map.of();
    }

    public static void register(LiteralArgumentBuilder<ICommandSender> command) {
        pendingExternalTrees.add(command);
    }

    public static void registerBuiltin(LiteralArgumentBuilder<ICommandSender> command) {
        pendingBuiltinTrees.add(command);
    }

    public static void registerStandalone(LiteralArgumentBuilder<ICommandSender> command) {
        pendingStandaloneTrees.add(command);
    }

    public static void registerLegacyCommands(Map<?, ?> commands) {
        Map<String, ICommand> registered = new HashMap<>();
        List<LiteralArgumentBuilder<ICommandSender>> builtinTrees = new ArrayList<>(pendingBuiltinTrees);
        pendingBuiltinTrees.clear();
        List<LiteralArgumentBuilder<ICommandSender>> externalTrees = new ArrayList<>(pendingExternalTrees);
        pendingExternalTrees.clear();
        List<LiteralArgumentBuilder<ICommandSender>> standaloneTrees = new ArrayList<>(pendingStandaloneTrees);
        pendingStandaloneTrees.clear();
        Set<String> syntaxRoots = new HashSet<>();
        Map<String, List<LiteralArgumentBuilder<ICommandSender>>> standaloneByName = new HashMap<>();
        for (LiteralArgumentBuilder<ICommandSender> tree : standaloneTrees) {
            standaloneByName.computeIfAbsent(tree.getLiteral(), name -> new ArrayList<>()).add(tree);
        }
        for (Map.Entry<?, ?> entry : commands.entrySet()) {
            if (!(entry.getKey() instanceof String name) || !(entry.getValue() instanceof ICommand command)) continue;
            registered.put(name, command);
            List<LiteralArgumentBuilder<ICommandSender>> standalone = standaloneByName.getOrDefault(name, List.of());
            dispatcher.register(LiteralArgumentBuilder.<ICommandSender>literal(name)
                    .requires(source -> canUse(source, command) || MiteCommandTrees.canUseEnumCommand(source, name)
                            || standalone.stream().anyMatch(tree -> tree.getRequirement().test(source))));
        }
        legacyCommands = Map.copyOf(registered);
        for (LiteralArgumentBuilder<ICommandSender> tree : builtinTrees) {
            if (registered.containsKey(tree.getLiteral()) || MiteCommandTrees.isEnumCommandName(tree.getLiteral())) {
                dispatcher.register(tree);
                syntaxRoots.add(tree.getLiteral());
            }
        }
        for (LiteralArgumentBuilder<ICommandSender> tree : externalTrees) {
            dispatcher.register(tree);
            syntaxRoots.add(tree.getLiteral());
        }
        for (LiteralArgumentBuilder<ICommandSender> tree : standaloneTrees) {
            dispatcher.register(tree);
            syntaxRoots.add(tree.getLiteral());
        }
        for (Map.Entry<String, ICommand> entry : registered.entrySet()) {
            String name = entry.getKey();
            if (syntaxRoots.add(name)) {
                dispatcher.register(LiteralArgumentBuilder.<ICommandSender>literal(name)
                        .then(RequiredArgumentBuilder.<ICommandSender, String>argument(
                                        "legacyArguments", StringArgumentType.greedyString())
                                .suggests(legacyCompletion(entry.getValue().getCommandName()))));
            }
        }
    }

    public static SuggestionProvider<ICommandSender> legacyCompletion(String commandName) {
        return (context, builder) -> {
            String[] input = context.getInput().split(" ", -1);
            ICommand command = legacyCommands.get(input[0]);
            if (command == null || !commandName.equals(command.getCommandName())) {
                return builder.buildFuture();
            }
            String[] arguments = Arrays.copyOfRange(input, 1, input.length);
            List<?> values = command.addTabCompletionOptions(context.getSource(), arguments);
            if (values != null) {
                int wordStart = Math.max(builder.getStart(), context.getInput().lastIndexOf(' ') + 1);
                var wordBuilder = builder.createOffset(wordStart);
                String prefix = wordBuilder.getRemaining().toLowerCase(java.util.Locale.ROOT);
                for (Object value : values) {
                    String candidate = value.toString();
                    if (candidate.toLowerCase(java.util.Locale.ROOT).startsWith(prefix)) wordBuilder.suggest(candidate);
                }
                return wordBuilder.buildFuture();
            }
            return builder.buildFuture();
        };
    }

    public static CommandSuggestionResult suggest(String input, ICommandSender source) {
        return suggest(input, input.length(), source);
    }

    public static CommandSuggestionResult suggest(String input, int cursorPosition, ICommandSender source) {
        if (!input.startsWith("/")) return null;

        int cursor = Math.max(1, Math.min(cursorPosition, input.length()));
        String command = input.substring(1);
        ParseResults<ICommandSender> parse = dispatcher.parse(command, source);
        int commandCursor = cursor - 1;
        ParseResults<ICommandSender> suggestionParse = commandCursor == command.length()
                ? parse : dispatcher.parse(command.substring(0, commandCursor), source);
        var suggestions = dispatcher.getCompletionSuggestions(suggestionParse, commandCursor).join();
        List<CommandSuggestionEntry> values = new ArrayList<>(Math.min(suggestions.getList().size(), MAX_SUGGESTIONS));
        int completionEnd = cursor == input.length() ? cursor : CommandSuggestionRange.tokenEnd(input, cursor);
        for (var suggestion : suggestions.getList()) {
            if (values.size() == MAX_SUGGESTIONS) break;
            var tooltipMessage = suggestion.getTooltip();
            String tooltip = tooltipMessage == null ? null : tooltipMessage.getString();
            int start = suggestion.getRange().getStart() + 1;
            int end = suggestion.getRange().getEnd() + 1;
            if (cursor < input.length()) end = completionEnd;
            values.add(new CommandSuggestionEntry(suggestion.getText(), tooltip,
                    start, Math.max(start, end)));
        }

        List<CommandSyntaxSpan> syntax = new ArrayList<>();
        int argumentStyle = 0;
        for (var parsedArgument : parse.getContext().getLastChild().getArguments().values()) {
            int start = parsedArgument.getRange().getStart() + 1;
            int end = parsedArgument.getRange().getEnd() + 1;
            if (end > start) {
                syntax.add(new CommandSyntaxSpan(start, end, CommandSyntaxKind.ARGUMENT,
                        argumentStyle++ % 5));
            }
        }
        if (parse.getReader().canRead()) {
            int unparsedStart = parse.getReader().getCursor() + 1;
            int unparsedEnd = Math.min(unparsedStart + parse.getReader().getRemainingLength(), input.length());
            if (unparsedStart < unparsedEnd) {
                syntax.add(new CommandSyntaxSpan(unparsedStart, unparsedEnd, CommandSyntaxKind.ERROR));
            }
        }

        var suggestionContext = suggestionParse.getContext().findSuggestionContext(commandCursor);
        List<String> usage = new ArrayList<>();
        if (cursor == input.length()) {
            for (var entry : dispatcher.getSmartUsage(suggestionContext.parent, source).entrySet()) {
                if (!(entry.getKey() instanceof LiteralCommandNode)) usage.add(entry.getValue());
            }
        }

        String errorMessage = null;
        int literalErrors = 0;
        for (var exception : suggestionParse.getExceptions().values()) {
            if (exception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                literalErrors++;
                continue;
            }
            if (errorMessage == null) {
                errorMessage = exception.getRawMessage().getString();
            }
        }
        if (errorMessage == null && literalErrors > 0) errorMessage = "Unknown command";
        if (cursor != input.length()) errorMessage = null;
        int usageStart = errorMessage == null ? suggestionContext.startPos + 1 : 0;
        return new CommandSuggestionResult(values, syntax, usage, usageStart, errorMessage);
    }

    private static boolean canUse(ICommandSender source, ICommand command) {
        return (Minecraft.inDevMode() || !DEV_ONLY_COMMANDS.contains(command.getCommandName()))
                && command.canCommandSenderUseCommand(source);
    }

    static boolean canUseLegacyCommand(ICommandSender source, String commandName) {
        ICommand command = legacyCommands.get(commandName);
        return command != null && canUse(source, command);
    }

}
