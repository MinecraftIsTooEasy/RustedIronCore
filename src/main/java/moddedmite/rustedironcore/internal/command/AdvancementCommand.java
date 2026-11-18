package moddedmite.rustedironcore.internal.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import moddedmite.rustedironcore.api.advancement.Advancement;
import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.api.event.events.CommandRegisterEvent;
import moddedmite.rustedironcore.internal.advancement.AdvancementJson;
import moddedmite.rustedironcore.internal.advancement.AdvancementFunctionSource;
import net.minecraft.ChatMessageComponent;
import net.minecraft.CommandBase;
import net.minecraft.EnumChatFormatting;
import net.minecraft.ICommandSender;
import net.minecraft.PlayerSelector;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AdvancementCommand extends CommandBase {

    private static final List<String> MODES = List.of("everything", "only", "from", "until", "through");

    public static void register(CommandRegisterEvent event) {
        event.register(new AdvancementCommand());
        LiteralArgumentBuilder<ICommandSender> root = LiteralArgumentBuilder.<ICommandSender>literal("advancement")
                .requires(sender -> sender.canCommandSenderUseCommand(2, "advancement"));
        root.then(LiteralArgumentBuilder.literal("reload"));
        for (String action : List.of("grant", "revoke")) {
            RequiredArgumentBuilder<ICommandSender, String> targets = RequiredArgumentBuilder.<ICommandSender, String>argument("targets", StringArgumentType.word())
                    .suggests((context, builder) -> {
                        for (String name : MinecraftServer.getServer().getAllUsernames()) if (name.startsWith(builder.getRemaining())) builder.suggest(name);
                        for (String selector : List.of("@a", "@p", "@r", "@s")) if (selector.startsWith(builder.getRemaining())) builder.suggest(selector);
                        return builder.buildFuture();
                    });
            for (String mode : MODES) {
                LiteralArgumentBuilder<ICommandSender> branch = LiteralArgumentBuilder.literal(mode);
                if (!mode.equals("everything")) {
                    RequiredArgumentBuilder<ICommandSender, String> id = RequiredArgumentBuilder.<ICommandSender, String>argument("advancement", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                for (Advancement advancement : Advancements.all()) if (advancement.id().toString().startsWith(builder.getRemaining())) builder.suggest(advancement.id().toString());
                                return builder.buildFuture();
                            });
                    if (mode.equals("only")) id.then(RequiredArgumentBuilder.<ICommandSender, String>argument("criterion", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                try {
                                    Advancement advancement = Advancements.get(AdvancementJson.id(StringArgumentType.getString(context, "advancement")));
                                    if (advancement != null) for (String criterion : advancement.criteria().keySet()) if (criterion.startsWith(builder.getRemaining())) builder.suggest(criterion);
                                } catch (IllegalArgumentException ignored) {
                                    return builder.buildFuture();
                                }
                                return builder.buildFuture();
                            }));
                    branch.then(id);
                }
                targets.then(branch);
            }
            root.then(LiteralArgumentBuilder.<ICommandSender>literal(action).then(targets));
        }
        event.registerSuggestionTree(root);
    }

    @Override
    public String getCommandName() {
        return "advancement";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.ric.advancement.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] arguments) {
        try {
            if (arguments.length == 1 && arguments[0].equals("reload")) {
                Advancements.reload();
                sender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.ric.advancement.reloaded"));
                return;
            }
            if (arguments.length < 3 || !List.of("grant", "revoke").contains(arguments[0]) || !MODES.contains(arguments[2])) throw new IllegalArgumentException("usage");
            String mode = arguments[2];
            int max = mode.equals("everything") ? 3 : mode.equals("only") ? 5 : 4;
            if (arguments.length > max || arguments.length < (mode.equals("everything") ? 3 : 4)) throw new IllegalArgumentException("usage");
            ServerPlayer[] players = targets(sender, arguments[1]);
            if (players.length == 0) throw new IllegalArgumentException("No players matched " + arguments[1]);
            Advancement selected = mode.equals("everything") ? null : Advancements.get(AdvancementJson.id(arguments[3]));
            if (!mode.equals("everything") && selected == null) throw new IllegalArgumentException("Unknown advancement: " + arguments[3]);
            String criterion = arguments.length == 5 ? arguments[4] : null;
            if (criterion != null && !selected.criteria().containsKey(criterion)) throw new IllegalArgumentException("Unknown criterion: " + criterion);
            int changed = 0;
            for (ServerPlayer player : players) for (Advancement advancement : select(mode, selected)) {
                Collection<String> criteria = criterion == null ? advancement.criteria().keySet() : List.of(criterion);
                for (String name : criteria) {
                    boolean result = arguments[0].equals("grant") ? Advancements.award(player, advancement.id(), name) : Advancements.revoke(player, advancement.id(), name);
                    if (result) changed++;
                }
            }
            sender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.ric.advancement.changed", changed, players.length));
        } catch (IllegalArgumentException exception) {
            sender.sendChatToPlayer(("usage".equals(exception.getMessage())
                    ? ChatMessageComponent.createFromTranslationKey(getCommandUsage(sender))
                    : ChatMessageComponent.createFromText(exception.getMessage())).setColor(EnumChatFormatting.RED));
        }
    }

    private static Collection<Advancement> select(String mode, Advancement selected) {
        if (mode.equals("everything")) return Advancements.all();
        Set<Advancement> result = new LinkedHashSet<>();
        if (mode.equals("until") || mode.equals("through")) {
            Advancement parent = selected;
            while (parent.parent() != null) {
                parent = Advancements.get(parent.parent());
                result.add(parent);
            }
        }
        result.add(selected);
        if (mode.equals("from") || mode.equals("through")) {
            for (Advancement advancement : Advancements.all()) {
                Advancement parent = advancement;
                while (parent.parent() != null) {
                    if (parent.parent().equals(selected.id())) { result.add(advancement); break; }
                    parent = Advancements.get(parent.parent());
                }
            }
        }
        return result;
    }

    private static ServerPlayer[] targets(ICommandSender sender, String selector) {
        if (selector.equals("@s")) {
            if (sender instanceof ServerPlayer player) return new ServerPlayer[]{player};
            if (sender instanceof AdvancementFunctionSource source) return new ServerPlayer[]{source.player()};
            return new ServerPlayer[0];
        }
        ServerPlayer[] selected = PlayerSelector.matchPlayers(sender, selector);
        if (selected != null) return selected;
        ServerPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(selector);
        return player == null ? new ServerPlayer[0] : new ServerPlayer[]{player};
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] arguments) {
        List<String> options = new ArrayList<>();
        switch (arguments.length) {
            case 1 -> options.addAll(List.of("grant", "revoke", "reload"));
            case 2 -> {
                options.addAll(Arrays.asList(MinecraftServer.getServer().getAllUsernames()));
                options.addAll(List.of("@a", "@p", "@r", "@s"));
            }
            case 3 -> options.addAll(MODES);
            case 4 -> Advancements.all().forEach(advancement -> options.add(advancement.id().toString()));
            case 5 -> {
                try {
                    Advancement advancement = Advancements.get(AdvancementJson.id(arguments[3]));
                    if (advancement != null && arguments[2].equals("only")) options.addAll(advancement.criteria().keySet());
                } catch (IllegalArgumentException ignored) {
                    return List.of();
                }
            }
        }
        String prefix = arguments[arguments.length - 1];
        return options.stream().filter(option -> option.regionMatches(true, 0, prefix, 0, prefix.length())).toList();
    }
}
