package moddedmite.rustedironcore.internal.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.EnumCommand;
import net.minecraft.EntityPlayer;
import net.minecraft.ICommand;
import net.minecraft.ICommandSender;
import net.minecraft.Minecraft;
import net.minecraft.Skill;
import net.minecraft.server.MinecraftServer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class MiteCommandTrees {

    private static final int SUCCESS = 1;
    private static final Set<EnumCommand> PLAYER_COMMANDS = Set.of(
            EnumCommand.commands, EnumCommand.xp, EnumCommand.syncpos, EnumCommand.ground, EnumCommand.stats);

    public static void register(Consumer<LiteralArgumentBuilder<ICommandSender>> registry,
                                Consumer<LiteralArgumentBuilder<ICommandSender>> standaloneRegistry) {
        for (EnumCommand command : EnumCommand.values()) {
            LiteralArgumentBuilder<ICommandSender> tree = literal(command.toString()).executes(context -> SUCCESS);
            if (PLAYER_COMMANDS.contains(command)) {
                tree.requires(source -> source instanceof EntityPlayer);
            }
            registry.accept(tree);
        }
        registerTime(registry);
        registerGameModes(registry);
        registerGameRules(registry);
        registerInventoryCommands(registry);
        registerPlayerCommands(registry);
        registerSocialCommands(registry);
        registerServerCommands(registry);
        registerScoreboard(registry);
        registerMiteCommands(standaloneRegistry);
    }

    static boolean canUseEnumCommand(ICommandSender source, String name) {
        for (EnumCommand command : EnumCommand.values()) {
            if (command.matches(name)) {
                return !PLAYER_COMMANDS.contains(command) || source instanceof EntityPlayer;
            }
        }
        return false;
    }

    static boolean isEnumCommandName(String name) {
        for (EnumCommand command : EnumCommand.values()) {
            if (command.matches(name)) return true;
        }
        return false;
    }

    private static void registerTime(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        LiteralArgumentBuilder<ICommandSender> set = literal("set")
                .requires(source -> CommandSuggestionDispatcher.canUseLegacyCommand(source, "time"))
                .then(literal("day").executes(context -> SUCCESS))
                .then(literal("night").executes(context -> SUCCESS))
                .then(argument("time", IntegerArgumentType.integer()).executes(context -> SUCCESS));
        registry.accept(literal("time").then(set)
                .then(literal("add").requires(source -> CommandSuggestionDispatcher.canUseLegacyCommand(source, "time"))
                        .then(argument("amount", IntegerArgumentType.integer()).executes(context -> SUCCESS))));
    }

    private static void registerGameModes(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        RequiredArgumentBuilder<ICommandSender, String> mode = argument("mode", CommandWordArgumentType.choices(
                List.of("survival", "s", "creative", "c", "adventure", "a", "0", "1", "2"),
                List.of("survival", "creative", "adventure")));
        mode.executes(context -> SUCCESS)
                .then(player("target", "gamemode").executes(context -> SUCCESS));
        registry.accept(literal("gamemode").then(mode));

        registry.accept(literal("defaultgamemode").then(argument("mode", CommandWordArgumentType.choices(
                        List.of("survival", "s", "creative", "c", "adventure", "a", "0", "1", "2"),
                        List.of("survival", "creative", "adventure")))
                .executes(context -> SUCCESS)));

        registry.accept(literal("difficulty").then(argument("difficulty", CommandWordArgumentType.choices(
                        List.of("peaceful", "p", "easy", "e", "normal", "n", "hard", "h", "0", "1", "2", "3"),
                        List.of("peaceful", "easy", "normal", "hard")))
                .executes(context -> SUCCESS)));
        registry.accept(literal("weather")
                .then(literal("clear").requires(source -> CommandSuggestionDispatcher.canUseLegacyCommand(source, "weather")))
                .then(literal("rain").requires(source -> CommandSuggestionDispatcher.canUseLegacyCommand(source, "weather")))
                .then(literal("thunder").requires(source -> CommandSuggestionDispatcher.canUseLegacyCommand(source, "weather"))));
        registry.accept(literal("toggledownfall").executes(context -> SUCCESS));
    }

    private static void registerGameRules(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        registry.accept(literal("gamerule").then(argument("rule", CommandWordArgumentType.dynamic(MiteCommandTrees::gameRuleNames))
                .suggests(legacy("gamerule")).executes(context -> SUCCESS)
                .then(argument("value", CommandWordArgumentType.choices("true", "false"))
                        .suggests(legacy("gamerule")).executes(context -> SUCCESS))));
    }

    private static void registerInventoryCommands(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        LiteralArgumentBuilder<ICommandSender> clear = literal("clear").executes(context -> SUCCESS);
        clear.then(player("target", "clear").requires(source -> CommandSuggestionDispatcher.canUseLegacyCommand(source, "clear"))
                .executes(context -> SUCCESS)
                .then(argument("itemId", IntegerArgumentType.integer(1)).executes(context -> SUCCESS)
                        .then(argument("metadata", IntegerArgumentType.integer(0)).executes(context -> SUCCESS))));
        registry.accept(clear);

        registry.accept(literal("give").then(player("target", "give")));
        registry.accept(literal("effect").then(player("target", "effect")));
        registry.accept(literal("enchant").then(player("target", "enchant")));
        registry.accept(literal("xp").then(argument("amount", StringArgumentType.word())
                .then(player("target", "xp").executes(context -> SUCCESS))));
    }

    private static void registerPlayerCommands(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        LiteralArgumentBuilder<ICommandSender> spawnpoint = literal("spawnpoint").executes(context -> SUCCESS);
        RequiredArgumentBuilder<ICommandSender, String> target = player("target", "spawnpoint");
        target.executes(context -> SUCCESS)
                .then(argument("x", IntegerArgumentType.integer())
                        .then(argument("y", IntegerArgumentType.integer(0, 256))
                                .then(argument("z", IntegerArgumentType.integer()).executes(context -> SUCCESS))));
        registry.accept(spawnpoint.then(target));

        registry.accept(literal("tp").then(player("target", "tp")
                .executes(context -> SUCCESS)
                .then(player("destination", "tp").executes(context -> SUCCESS))));
        registry.accept(literal("kill").executes(context -> SUCCESS));
        registry.accept(literal("testfor").then(player("target", "testfor").executes(context -> SUCCESS)));
    }

    private static void registerSocialCommands(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        registerMessageAlias(registry, "tell");
        registerMessageAlias(registry, "msg");
        registerMessageAlias(registry, "w");
        registry.accept(literal("me").then(argument("action", StringArgumentType.greedyString()).executes(context -> SUCCESS)));
        registry.accept(literal("say").then(argument("message", StringArgumentType.greedyString()).executes(context -> SUCCESS)));
        registry.accept(literal("ban").then(player("target", "ban").executes(context -> SUCCESS)
                .then(argument("reason", StringArgumentType.greedyString()).executes(context -> SUCCESS))));
        registry.accept(literal("ban-ip").then(argument("address", StringArgumentType.word()).suggests(legacy("ban-ip"))
                .executes(context -> SUCCESS)
                .then(argument("reason", StringArgumentType.greedyString()).executes(context -> SUCCESS))));
        registry.accept(literal("kick").then(player("target", "kick").executes(context -> SUCCESS)
                .then(argument("reason", StringArgumentType.greedyString()).executes(context -> SUCCESS))));
    }

    private static void registerMessageAlias(Consumer<LiteralArgumentBuilder<ICommandSender>> registry, String name) {
        registry.accept(literal(name).then(player("target", "tell")
                .then(argument("message", StringArgumentType.greedyString()).executes(context -> SUCCESS))));
    }

    private static void registerServerCommands(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        registry.accept(literal("pardon").then(argument("target", StringArgumentType.word()).suggests(legacy("pardon"))
                .executes(context -> SUCCESS)));
        registry.accept(literal("pardon-ip").then(argument("address", StringArgumentType.word()).suggests(legacy("pardon-ip"))
                .executes(context -> SUCCESS)));
        registry.accept(literal("banlist").executes(context -> SUCCESS)
                .then(literal("players").executes(context -> SUCCESS))
                .then(literal("ips").executes(context -> SUCCESS)));
        registry.accept(literal("whitelist")
                .then(literal("list").executes(context -> SUCCESS))
                .then(literal("on").executes(context -> SUCCESS))
                .then(literal("off").executes(context -> SUCCESS))
                .then(literal("reload").executes(context -> SUCCESS))
                .then(literal("add").then(argument("target", StringArgumentType.word()).suggests(legacy("whitelist"))
                        .executes(context -> SUCCESS)))
                .then(literal("remove").then(argument("target", StringArgumentType.word()).suggests(legacy("whitelist"))
                        .executes(context -> SUCCESS))));
        registry.accept(literal("op").then(argument("target", StringArgumentType.word()).suggests(legacy("op"))
                .executes(context -> SUCCESS)));
        registry.accept(literal("deop").then(argument("target", StringArgumentType.word()).suggests(legacy("deop"))
                .executes(context -> SUCCESS)));
        registry.accept(literal("setidletimeout").then(argument("minutes", IntegerArgumentType.integer(0))
                .executes(context -> SUCCESS)));
        registry.accept(literal("save-all").executes(context -> SUCCESS).then(literal("flush").executes(context -> SUCCESS)));
        registry.accept(literal("save-off").executes(context -> SUCCESS));
        registry.accept(literal("save-on").executes(context -> SUCCESS));
        registry.accept(literal("stop").executes(context -> SUCCESS));
        registry.accept(literal("list").executes(context -> SUCCESS));
        registry.accept(literal("publish").executes(context -> SUCCESS));
        registry.accept(literal("seed").executes(context -> SUCCESS));
        registry.accept(literal("help")
                .then(argument("page", IntegerArgumentType.integer(1)).executes(context -> SUCCESS))
                .then(argument("command", CommandWordArgumentType.dynamic(MiteCommandTrees::commandNames))
                        .suggests(legacy("help")).executes(context -> SUCCESS)));
        registry.accept(literal("?")
                .then(argument("page", IntegerArgumentType.integer(1)).executes(context -> SUCCESS))
                .then(argument("command", CommandWordArgumentType.dynamic(MiteCommandTrees::commandNames))
                        .suggests(legacy("help")).executes(context -> SUCCESS)));
        registry.accept(literal("debug").then(literal("start").executes(context -> SUCCESS))
                .then(literal("stop").executes(context -> SUCCESS)));
        registerPlaySound(registry);
        registerSpreadPlayers(registry);
    }

    private static void registerPlaySound(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        RequiredArgumentBuilder<ICommandSender, String> player = argument("target", StringArgumentType.word())
                .suggests(playerAndSelectorSuggestions()).executes(context -> SUCCESS);
        RequiredArgumentBuilder<ICommandSender, Double> minimumVolume = argument("minimumVolume",
                DoubleArgumentType.doubleArg(0.0D, 1.0D)).executes(context -> SUCCESS);
        RequiredArgumentBuilder<ICommandSender, Double> pitch = argument("pitch",
                DoubleArgumentType.doubleArg(0.0D, 2.0D)).executes(context -> SUCCESS).then(minimumVolume);
        RequiredArgumentBuilder<ICommandSender, Double> volume = argument("volume",
                DoubleArgumentType.doubleArg(0.0D)).executes(context -> SUCCESS).then(pitch);
        RequiredArgumentBuilder<ICommandSender, MiteCoordinate> z = argument("z", MiteCoordinateArgumentType.coordinate())
                .executes(context -> SUCCESS).then(volume);
        RequiredArgumentBuilder<ICommandSender, MiteCoordinate> y = argument("y", MiteCoordinateArgumentType.coordinate()).then(z);
        player.then(argument("x", MiteCoordinateArgumentType.coordinate()).then(y));
        registry.accept(literal("playsound").then(argument("sound", StringArgumentType.word()).then(player)));
    }

    private static void registerSpreadPlayers(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        RequiredArgumentBuilder<ICommandSender, String> moreTargets = argument("moreTargets", StringArgumentType.greedyString())
                .suggests(playerAndSelectorSuggestions())
                .executes(context -> SUCCESS);
        RequiredArgumentBuilder<ICommandSender, String> targets = argument("targets", StringArgumentType.word())
                .suggests(playerAndSelectorSuggestions()).executes(context -> SUCCESS).then(moreTargets);
        RequiredArgumentBuilder<ICommandSender, String> respectTeams = argument("respectTeams",
                CommandWordArgumentType.choices("true", "false", "1", "0"))
                .then(targets);
        RequiredArgumentBuilder<ICommandSender, Double> maxRange = argument("maxRange",
                DoubleArgumentType.doubleArg(0.0D)).then(respectTeams);
        RequiredArgumentBuilder<ICommandSender, Double> spreadDistance = argument("spreadDistance",
                DoubleArgumentType.doubleArg(0.0D)).then(maxRange);
        RequiredArgumentBuilder<ICommandSender, MiteCoordinate> z = argument("z", MiteCoordinateArgumentType.coordinate()).then(spreadDistance);
        registry.accept(literal("spreadplayers").then(argument("x", MiteCoordinateArgumentType.coordinate()).then(z)));
    }

    private static void registerScoreboard(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        LiteralArgumentBuilder<ICommandSender> objectives = literal("objectives")
                .then(literal("list").executes(context -> SUCCESS))
                .then(literal("add").then(argument("objective", StringArgumentType.word())
                        .then(argument("criteria", StringArgumentType.word()).suggests(legacy("scoreboard"))
                                .executes(context -> SUCCESS)
                                .then(argument("displayName", StringArgumentType.greedyString()).executes(context -> SUCCESS)))))
                .then(literal("remove").then(argument("objective", StringArgumentType.word()).suggests(legacy("scoreboard"))
                        .executes(context -> SUCCESS)))
                .then(literal("setdisplay").then(argument("slot", CommandWordArgumentType.choices("list", "sidebar", "belowName"))
                        .suggests(legacy("scoreboard")).executes(context -> SUCCESS)
                        .then(argument("objective", StringArgumentType.word()).suggests(legacy("scoreboard"))
                                .executes(context -> SUCCESS))));

        LiteralArgumentBuilder<ICommandSender> players = literal("players")
                .then(literal("set").then(player("target", "scoreboard")
                        .then(argument("objective", StringArgumentType.word()).suggests(legacy("scoreboard"))
                                .then(argument("score", IntegerArgumentType.integer()).executes(context -> SUCCESS)))))
                .then(literal("add").then(player("target", "scoreboard")
                        .then(argument("objective", StringArgumentType.word()).suggests(legacy("scoreboard"))
                                .then(argument("score", IntegerArgumentType.integer(1)).executes(context -> SUCCESS)))))
                .then(literal("remove").then(player("target", "scoreboard")
                        .then(argument("objective", StringArgumentType.word()).suggests(legacy("scoreboard"))
                                .then(argument("score", IntegerArgumentType.integer(1)).executes(context -> SUCCESS)))))
                .then(literal("reset").then(argument("target", StringArgumentType.word()).suggests(legacy("scoreboard"))
                        .executes(context -> SUCCESS)))
                .then(literal("list").executes(context -> SUCCESS)
                        .then(argument("target", StringArgumentType.word()).suggests(legacy("scoreboard"))
                                .executes(context -> SUCCESS)));

        LiteralArgumentBuilder<ICommandSender> teams = literal("teams")
                .then(literal("add").then(argument("team", StringArgumentType.word())
                        .executes(context -> SUCCESS)
                        .then(argument("displayName", StringArgumentType.greedyString()).executes(context -> SUCCESS))))
                .then(literal("remove").then(argument("team", StringArgumentType.word()).suggests(legacy("scoreboard"))
                        .executes(context -> SUCCESS)))
                .then(literal("join").then(argument("team", StringArgumentType.word()).suggests(legacy("scoreboard"))
                        .executes(context -> SUCCESS)
                        .then(player("target", "scoreboard").executes(context -> SUCCESS)
                                .then(argument("moreTargets", StringArgumentType.greedyString()).suggests(legacy("scoreboard"))
                                        .executes(context -> SUCCESS)))))
                .then(literal("leave").executes(context -> SUCCESS)
                        .then(player("target", "scoreboard").executes(context -> SUCCESS)
                                .then(argument("moreTargets", StringArgumentType.greedyString()).suggests(legacy("scoreboard"))
                                        .executes(context -> SUCCESS))))
                .then(literal("empty").then(argument("team", StringArgumentType.word()).suggests(legacy("scoreboard"))
                        .executes(context -> SUCCESS)))
                .then(literal("list").executes(context -> SUCCESS)
                        .then(argument("team", StringArgumentType.word()).suggests(legacy("scoreboard"))
                                .executes(context -> SUCCESS)))
                .then(literal("option").then(argument("team", StringArgumentType.word()).suggests(legacy("scoreboard"))
                        .then(argument("option", CommandWordArgumentType.choices("color", "friendlyfire", "seeFriendlyInvisibles"))
                                .suggests(legacy("scoreboard")).executes(context -> SUCCESS)
                                .then(argument("value", StringArgumentType.word()).suggests(legacy("scoreboard"))
                                        .executes(context -> SUCCESS)))));
        registry.accept(literal("scoreboard").then(objectives).then(players).then(teams));
    }

    private static void registerMiteCommands(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        SuggestionProvider<ICommandSender> skills = (context, builder) -> {
            String prefix = builder.getRemaining().toLowerCase(Locale.ROOT);
            for (int index = 0; index < Skill.getNumSkills(); index++) {
                Skill skill = Skill.list[index];
                String skillName = skill.getLocalizedName(false);
                String professionName = skill.getLocalizedName(true);
                if (skillName.toLowerCase(Locale.ROOT).startsWith(prefix)) builder.suggest(skillName);
                if (professionName.toLowerCase(Locale.ROOT).startsWith(prefix)) builder.suggest(professionName);
            }
            return builder.buildFuture();
        };
        registry.accept(literal("skill").requires(source -> source.getEntityWorld().areSkillsEnabled())
                .then(argument("skill", StringArgumentType.greedyString()).suggests(skills).executes(context -> SUCCESS)));
        registry.accept(literal("learn").requires(source -> source instanceof EntityPlayer
                        && source.getEntityWorld().areSkillsEnabled())
                .then(argument("skill", StringArgumentType.greedyString()).suggests(skills).executes(context -> SUCCESS)));
        registry.accept(literal("abandon").requires(source -> source instanceof EntityPlayer
                        && source.getEntityWorld().areSkillsEnabled())
                .then(argument("skill", StringArgumentType.greedyString()).suggests(skills).executes(context -> SUCCESS)));
        registry.accept(literal("skills")
                .then(literal("on").requires(source -> Minecraft.inDevMode()).executes(context -> SUCCESS))
                .then(literal("off").requires(source -> Minecraft.inDevMode()).executes(context -> SUCCESS)));

        registry.accept(literal("mute").requires(source -> source instanceof EntityPlayer).executes(context -> SUCCESS));
        registry.accept(literal("pushout").requires(source -> source instanceof EntityPlayer).executes(context -> SUCCESS));
        registry.accept(literal("hunger").requires(source -> source instanceof EntityPlayer).executes(context -> SUCCESS));
        registry.accept(literal("village").requires(source -> !(source instanceof EntityPlayer)
                || Minecraft.inDevMode()).executes(context -> SUCCESS));
        registry.accept(literal("time").requires(source -> !(source instanceof EntityPlayer)
                || Minecraft.inDevMode()).executes(context -> SUCCESS));
        registry.accept(literal("hour").requires(source -> !(source instanceof EntityPlayer)
                && Minecraft.inDevMode()).executes(context -> SUCCESS));

        registerPrivilegedCommands(registry);
    }

    private static void registerPrivilegedCommands(Consumer<LiteralArgumentBuilder<ICommandSender>> registry) {
        registry.accept(privileged("decoy").executes(context -> SUCCESS)
                .then(argument("entity", StringArgumentType.greedyString()).executes(context -> SUCCESS)));
        registry.accept(privileged("grace").executes(context -> SUCCESS));

        Predicate<ICommandSender> devOrTournament = MiteCommandTrees::isDeveloperOrTournamentPlayer;
        registry.accept(privileged("level").then(argument("level", IntegerArgumentType.integer())
                .requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("heal").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("health").then(argument("health", IntegerArgumentType.integer())
                .requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("fill").executes(context -> SUCCESS)
                .then(argument("radius", IntegerArgumentType.integer()).requires(devOrTournament)
                        .executes(context -> SUCCESS)));
        registry.accept(privileged("nourish").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("starve").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("satiation").then(argument("amount", IntegerArgumentType.integer())
                .requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("nutrition").then(argument("amount", IntegerArgumentType.integer())
                .requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("night").then(literal("vision").requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("test").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("bolt").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("damage").then(literal("armor").requires(devOrTournament).executes(context -> SUCCESS))
                .then(literal("item").requires(devOrTournament).executes(context -> SUCCESS)
                        .then(argument("amount", IntegerArgumentType.integer()).requires(devOrTournament)
                                .executes(context -> SUCCESS))));
        registry.accept(privileged("xp").then(argument("amount", IntegerArgumentType.integer())
                .requires(devOrTournament).executes(context -> SUCCESS)
                .then(player("target", "xp").requires(devOrTournament).executes(context -> SUCCESS))));
        registry.accept(privileged("day").then(argument("day", IntegerArgumentType.integer())
                .requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("end").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("metadata").then(argument("metadata", IntegerArgumentType.integer())
                .requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("repair").then(literal("item").requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("thirst").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("grow").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("tame").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("clear").then(literal("books").requires(devOrTournament).executes(context -> SUCCESS))
                .then(argument("radius", MiteIntegerArgumentType.integerWithBang()).requires(devOrTournament)
                        .executes(context -> SUCCESS)));

        registry.accept(privileged("timber").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("mobs").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("peaceful").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("hostile").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("malnourish").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("protein").then(argument("amount", IntegerArgumentType.integer())
                .requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("essential").then(literal("fats").then(argument("amount", IntegerArgumentType.integer())
                .requires(devOrTournament)
                .executes(context -> SUCCESS))));
        registry.accept(privileged("phytonutrients").then(argument("amount", IntegerArgumentType.integer())
                .requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("insulin").then(argument("amount", IntegerArgumentType.integer())
                .requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("slaughter").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("spawning").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("killall").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("killmobs").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("nomobs").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("recall").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("corrupt").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("crash").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("achievement").then(argument("id", MiteIntegerArgumentType.integerWithBang())
                .requires(devOrTournament)
                .executes(context -> SUCCESS)));
        registry.accept(privileged("books").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("layer").then(literal("air").requires(devOrTournament).executes(context -> SUCCESS))
                .then(argument("block", StringArgumentType.greedyString()).requires(devOrTournament)
                        .executes(context -> SUCCESS)));
        registry.accept(privileged("layermd").then(argument("metadata", IntegerArgumentType.integer())
                .requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("noliquid").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("spawn").then(literal("animals").requires(devOrTournament).executes(context -> SUCCESS))
                .then(literal("squids").requires(devOrTournament).executes(context -> SUCCESS))
                .then(literal("bats").requires(devOrTournament).executes(context -> SUCCESS))
                .then(literal("mobs").requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("bb").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("tile").then(literal("entities").requires(devOrTournament).executes(context -> SUCCESS)));
        registry.accept(privileged("weather").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("cavern").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("cavern+").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("cavern!").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("drill").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(privileged("teleport").requires(devOrTournament).executes(context -> SUCCESS));
        registry.accept(literal("see").requires(MiteCommandTrees::isTournamentPlayer).executes(context -> SUCCESS));
    }

    private static LiteralArgumentBuilder<ICommandSender> privileged(String name) {
        return literal(name).requires(MiteCommandTrees::isPrivilegedPlayer);
    }

    private static boolean isPrivilegedPlayer(ICommandSender source) {
        if (!(source instanceof EntityPlayer player)) return false;
        String name = source.getCommandSenderName();
        return Minecraft.inDevMode() || player.isZevimrgvInTournament() || name.equalsIgnoreCase("Vuce")
                || name.equalsIgnoreCase("Roninpawn") || name.equalsIgnoreCase("ShadowKnight1234");
    }

    private static boolean isDeveloperOrTournamentPlayer(ICommandSender source) {
        return source instanceof EntityPlayer player
                && (Minecraft.inDevMode() || player.isZevimrgvInTournament());
    }

    private static boolean isTournamentPlayer(ICommandSender source) {
        return source instanceof EntityPlayer player && player.isZevimrgvInTournament();
    }

    private static RequiredArgumentBuilder<ICommandSender, String> player(String name, String command) {
        return argument(name, StringArgumentType.word()).suggests(legacy(command));
    }

    private static LiteralArgumentBuilder<ICommandSender> literal(String name) {
        return LiteralArgumentBuilder.<ICommandSender>literal(name);
    }

    private static <T> RequiredArgumentBuilder<ICommandSender, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.<ICommandSender, T>argument(name, type);
    }

    private static SuggestionProvider<ICommandSender> legacy(String command) {
        return CommandSuggestionDispatcher.legacyCompletion(command);
    }

    private static SuggestionProvider<ICommandSender> playerAndSelectorSuggestions() {
        return (context, builder) -> {
            String prefix = builder.getRemaining().toLowerCase(Locale.ROOT);
            for (String username : MinecraftServer.getServer().getAllUsernames()) {
                if (username.toLowerCase(Locale.ROOT).startsWith(prefix)) builder.suggest(username);
            }
            for (String selector : List.of("@p", "@a", "@r")) {
                if (selector.startsWith(prefix)) builder.suggest(selector);
            }
            return builder.buildFuture();
        };
    }

    private static Collection<String> gameRuleNames() {
        return List.of(MinecraftServer.getServer().worldServerForDimension(0).getGameRules().getRules());
    }

    private static Collection<String> commandNames() {
        Map<String, ICommand> commands = CommandSuggestionDispatcher.getLegacyCommands();
        return commands.keySet();
    }

}
