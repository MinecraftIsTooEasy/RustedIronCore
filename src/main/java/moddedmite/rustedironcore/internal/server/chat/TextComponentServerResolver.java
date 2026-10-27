package moddedmite.rustedironcore.internal.server.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.api.chat.TextColor;
import moddedmite.rustedironcore.api.chat.TextHoverEvent;
import moddedmite.rustedironcore.api.chat.TextStyle;
import moddedmite.rustedironcore.internal.chat.NbtPathEvaluator;
import moddedmite.rustedironcore.mixin.accessor.ScoreboardScoresAccessor;
import net.minecraft.ChunkCoordinates;
import net.minecraft.Entity;
import net.minecraft.EntityPlayer;
import net.minecraft.ICommandSender;
import net.minecraft.NBTBase;
import net.minecraft.NBTTagCompound;
import net.minecraft.PlayerSelector;
import net.minecraft.Packet3Chat;
import net.minecraft.Score;
import net.minecraft.ScoreObjective;
import net.minecraft.Scoreboard;
import net.minecraft.ServerPlayer;
import net.minecraft.TileEntity;
import net.minecraft.World;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class TextComponentServerResolver {

    private static final TextComponent DEFAULT_SELECTOR_SEPARATOR = TextComponent.literal(", ")
            .setStyle(TextStyle.EMPTY.withColor(TextColor.parse("gray")));
    private static final ThreadLocal<ICommandSender> COMMAND_SOURCE = new ThreadLocal<>();

    public static void withCommandSource(ICommandSender source, Runnable action) {
        ICommandSender previous = COMMAND_SOURCE.get();
        COMMAND_SOURCE.set(source);
        try {
            action.run();
        } finally {
            if (previous == null) COMMAND_SOURCE.remove();
            else COMMAND_SOURCE.set(previous);
        }
    }

    @Nullable
    public static ICommandSender getCommandSource() {
        return COMMAND_SOURCE.get();
    }

    public static Packet3Chat resolvePacket(Packet3Chat packet, ICommandSender source, ServerPlayer recipient) {
        try {
            TextComponent component = TextComponentSerializer.fromJson(packet.message);
            if (!hasDynamicContent(component)) return packet;

            String message = TextComponentSerializer.toJson(resolve(component, source, recipient));
            if (message.length() > 32767) return packet;
            Packet3Chat resolved = new Packet3Chat(message, packet.getIsServer());
            resolved.permission_override = packet.permission_override;
            return resolved;
        } catch (RuntimeException ignored) {
            return packet;
        }
    }

    public static boolean hasDynamicContent(TextComponent component) {
        return hasDynamicContent(component, new HashSet<>());
    }

    public static TextComponent resolve(TextComponent component, ICommandSender sender, Entity contextEntity) {
        return resolveComponent(component, sender, contextEntity);
    }

    private static boolean hasDynamicContent(TextComponent component, Set<TextComponent> visited) {
        if (!visited.add(component)) return false;
        TextComponent.Content content = component.getContent();
        if (content instanceof TextComponent.Score || content instanceof TextComponent.Selector) return true;
        if (content instanceof TextComponent.Nbt nbt && !(nbt.source() instanceof TextComponent.StorageData)) return true;
        if (content instanceof TextComponent.Translatable translatable) {
            for (Object argument : translatable.arguments()) {
                if (argument instanceof TextComponent child && hasDynamicContent(child, visited)) return true;
            }
        }
        if (content instanceof TextComponent.Selector selector
                && selector.separator().filter(child -> hasDynamicContent(child, visited)).isPresent()) return true;
        if (content instanceof TextComponent.Nbt nbt
                && nbt.separator().filter(child -> hasDynamicContent(child, visited)).isPresent()) return true;

        TextHoverEvent hover = component.getStyle().getHoverEvent();
        if (hover != null && hover.action() == TextHoverEvent.Action.SHOW_TEXT) {
            try {
                TextComponent hoverText = TextComponentSerializer.fromJson(hover.contents());
                if (hasDynamicContent(hoverText, visited)) return true;
            } catch (RuntimeException ignored) {
            }
        }
        for (TextComponent sibling : component.getSiblings()) {
            if (hasDynamicContent(sibling, visited)) return true;
        }
        return false;
    }

    private static TextComponent resolveComponent(TextComponent component, ICommandSender sender, Entity contextEntity) {
        TextComponent.Content content = component.getContent();
        TextComponent resolved;
        if (content instanceof TextComponent.Score score) {
            resolved = resolveScore(score, sender, contextEntity);
        } else if (content instanceof TextComponent.Selector selector) {
            resolved = resolveSelector(selector, sender, contextEntity);
        } else if (content instanceof TextComponent.Nbt nbt && !(nbt.source() instanceof TextComponent.StorageData)) {
            resolved = resolveNbt(nbt, sender, contextEntity);
        } else {
            resolved = new TextComponent(resolveContent(content, sender, contextEntity));
        }
        resolved.setStyle(resolveStyle(component.getStyle(), sender, contextEntity));
        for (TextComponent sibling : component.getSiblings()) {
            resolved.append(resolveComponent(sibling, sender, contextEntity));
        }
        return resolved;
    }

    private static TextComponent.Content resolveContent(TextComponent.Content content, ICommandSender sender, Entity contextEntity) {
        if (content instanceof TextComponent.Translatable translatable) {
            List<Object> arguments = new ArrayList<>(translatable.arguments().size());
            for (Object argument : translatable.arguments()) {
                arguments.add(argument instanceof TextComponent component
                        ? resolveComponent(component, sender, contextEntity) : argument);
            }
            return new TextComponent.Translatable(translatable.key(), translatable.fallback(), arguments);
        }
        return content;
    }

    private static TextStyle resolveStyle(TextStyle style, ICommandSender sender, Entity contextEntity) {
        TextHoverEvent hover = style.getHoverEvent();
        if (hover == null || hover.action() != TextHoverEvent.Action.SHOW_TEXT) return style;
        try {
            TextComponent hoverText = TextComponentSerializer.fromJson(hover.contents());
            TextComponent resolvedHover = resolveComponent(hoverText, sender, contextEntity);
            JsonElement json = TextComponentSerializer.toJsonElement(resolvedHover);
            return style.withHoverEvent(new TextHoverEvent(TextHoverEvent.Action.SHOW_TEXT, json));
        } catch (RuntimeException ignored) {
            return style;
        }
    }

    private static TextComponent resolveScore(TextComponent.Score score, ICommandSender sender, Entity contextEntity) {
        String scoreName = score.name();
        List<Entity> namedTargets = selectEntities(scoreName, sender);
        if (scoreName.startsWith("@")) {
            if (namedTargets.size() == 1) {
                scoreName = scoreHolderName(namedTargets.get(0));
            } else if (namedTargets.size() > 1) {
                return TextComponent.literal("");
            }
        } else if (namedTargets.size() == 1) {
            scoreName = scoreHolderName(namedTargets.get(0));
        }
        if ("*".equals(scoreName) && contextEntity != null) scoreName = scoreHolderName(contextEntity);

        World world = sender.getEntityWorld();
        Scoreboard scoreboard = world.getScoreboard();
        ScoreObjective objective = scoreboard.getObjective(score.objective());
        if (objective == null) return TextComponent.literal("");
        Map<String, Map<ScoreObjective, Score>> scores = ((ScoreboardScoresAccessor) scoreboard).ric$getScores();
        Map<ScoreObjective, Score> playerScores = scores.get(scoreName);
        Score value = playerScores == null ? null : playerScores.get(objective);
        return TextComponent.literal(value == null ? "" : Integer.toString(value.getScorePoints()));
    }

    private static String scoreHolderName(Entity entity) {
        return entity instanceof EntityPlayer player ? player.getCommandSenderName() : entity.getUniqueIDSilent().toString();
    }

    private static TextComponent resolveSelector(TextComponent.Selector selector, ICommandSender sender, Entity contextEntity) {
        List<Entity> targets = selectEntities(selector.pattern(), sender);
        TextComponent result = TextComponent.literal("");
        if (targets.isEmpty()) return result;

        TextComponent separator = selector.separator().map(value -> resolveComponent(value, sender, contextEntity))
                .orElseGet(() -> DEFAULT_SELECTOR_SEPARATOR.copy());
        for (int i = 0; i < targets.size(); i++) {
            if (i > 0) result.append(separator.copy());
            result.append(TextComponent.literal(targets.get(i).getTranslatedEntityName()));
        }
        return result;
    }

    private static TextComponent resolveNbt(TextComponent.Nbt nbt, ICommandSender sender, Entity contextEntity) {
        List<NBTTagCompound> sources = readNbtSources(nbt.source(), sender, contextEntity);
        List<NBTBase> values = new ArrayList<>();
        for (NBTTagCompound source : sources) values.addAll(NbtPathEvaluator.get(source, nbt.path()));
        TextComponent result = TextComponent.literal("");
        if (values.isEmpty()) return result;

        TextComponent separator = nbt.separator().map(value -> resolveComponent(value, sender, contextEntity))
                .orElseGet(() -> TextComponent.literal(", "));
        boolean first = true;
        for (NBTBase value : values) {
            TextComponent rendered = renderNbtValue(value, nbt.interpret(), sender, contextEntity);
            if (rendered == null) continue;
            if (!first) result.append(separator.copy());
            result.append(rendered);
            first = false;
        }
        return result;
    }

    private static TextComponent renderNbtValue(NBTBase value, boolean interpret, ICommandSender sender, Entity contextEntity) {
        String serialized = NbtPathEvaluator.getAsString(value);
        if (interpret) {
            try {
                TextComponent component = TextComponentSerializer.fromJson(new JsonParser().parse(serialized));
                return resolveComponent(component, sender, contextEntity);
            } catch (RuntimeException ignored) {
                return null;
            }
        }
        return TextComponent.literal(serialized);
    }

    private static List<NBTTagCompound> readNbtSources(TextComponent.NbtSource source, ICommandSender sender,
                                                        Entity contextEntity) {
        World world = sender.getEntityWorld();
        if (source instanceof TextComponent.BlockData block) {
            int[] position = parseBlockPosition(block.position(), sender);
            if (position == null || !world.blockExists(position[0], position[1], position[2])) return List.of();
            TileEntity tile = world.getBlockTileEntity(position[0], position[1], position[2]);
            if (tile == null) return List.of();
            NBTTagCompound data = new NBTTagCompound();
            tile.writeToNBT(data);
            return List.of(data);
        }
        if (source instanceof TextComponent.EntityData entityData) {
            List<Entity> entities = selectEntities(entityData.selector(), sender);
            List<NBTTagCompound> data = new ArrayList<>(entities.size());
            for (Entity entity : entities) {
                NBTTagCompound tag = new NBTTagCompound();
                entity.writeToNBT(tag);
                data.add(tag);
            }
            return data;
        }
        return List.of();
    }

    private static int[] parseBlockPosition(String position, ICommandSender sender) {
        String[] coordinates = position.trim().split("\\s+");
        if (coordinates.length != 3) return null;
        ChunkCoordinates base = sender.getPlayerCoordinates();
        if (base == null) return null;
        try {
            return new int[]{parseCoordinate(coordinates[0], base.posX),
                    parseCoordinate(coordinates[1], base.posY), parseCoordinate(coordinates[2], base.posZ)};
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static int parseCoordinate(String coordinate, int base) {
        if (coordinate.equals("~")) return base;
        if (coordinate.startsWith("~")) return base + Integer.parseInt(coordinate.substring(1));
        return Integer.parseInt(coordinate);
    }

    private static List<Entity> selectEntities(String pattern, ICommandSender sender) {
        if (pattern.equals("@s")) return sender instanceof Entity entity ? List.of(entity) : List.of();
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return List.of();

        if (!pattern.startsWith("@")) {
            try {
                UUID id = UUID.fromString(pattern);
                for (World world : server.worldServers) {
                    if (world == null) continue;
                    for (Object value : world.loadedEntityList) {
                        if (value instanceof Entity entity && id.equals(entity.getUniqueIDSilent())) {
                            return List.of(entity);
                        }
                    }
                }
                return List.of();
            } catch (IllegalArgumentException ignored) {
                ServerPlayer player = server.getConfigurationManager().getPlayerForUsername(pattern);
                return player == null ? List.of() : List.of(player);
            }
        }

        if (!pattern.equals("@p") && !pattern.equals("@a") && !pattern.equals("@r")) return List.of();

        try {
            ServerPlayer[] selected = PlayerSelector.matchPlayers(sender, pattern);
            if (selected == null) return List.of();
            List<Entity> result = new ArrayList<>(selected.length);
            for (ServerPlayer entity : selected) result.add(entity);
            return result;
        } catch (RuntimeException ignored) {
            return List.of();
        }
    }
}
