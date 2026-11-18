package moddedmite.rustedironcore.internal.advancement;

import moddedmite.rustedironcore.api.util.LogUtil;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdvancementFunctions {

    private static final Map<String, List<String>> FUNCTIONS = new HashMap<>();
    private static final ThreadLocal<int[]> BUDGET = new ThreadLocal<>();

    public static void reload() {
        FUNCTIONS.clear();
        AdvancementResources.loadFunctions().forEach((id, lines) -> {
            List<String> commands = new ArrayList<>();
            for (String line : lines) {
                String command = line.strip();
                if (!command.isEmpty() && !command.startsWith("#")) commands.add(command);
            }
            FUNCTIONS.put(id.toString(), List.copyOf(commands));
        });
    }

    public static void validate(String id) {
        validate(AdvancementJson.id(id).toString(), new HashSet<>());
    }

    private static void validate(String id, Set<String> visiting) {
        List<String> commands = FUNCTIONS.get(id);
        if (commands == null) throw new IllegalArgumentException("Missing advancement function: " + id);
        if (visiting.size() >= 256 || !visiting.add(id)) throw new IllegalArgumentException("Cyclic advancement function: " + id);
        for (String command : commands) {
            if (command.startsWith("/") || command.startsWith("$")) throw new IllegalArgumentException("Unsupported function command: " + command);
            String[] parts = command.split("\\s+", 2);
            if (parts[0].equals("function")) {
                if (parts.length != 2) throw new IllegalArgumentException("Missing function identifier");
                validate(AdvancementJson.id(parts[1]).toString(), visiting);
            } else {
                MinecraftServer server = MinecraftServer.getServer();
                String name = parts[0];
                if (server != null && !server.getCommandManager().getCommands().containsKey(name)) throw new IllegalArgumentException("Unknown native function command: " + name);
            }
        }
        visiting.remove(id);
    }

    public static void run(String id, ServerPlayer player) {
        boolean root = BUDGET.get() == null;
        if (root) BUDGET.set(new int[]{65536});
        try {
            run(AdvancementJson.id(id).toString(), new AdvancementFunctionSource(player), BUDGET.get());
        } finally {
            if (root) BUDGET.remove();
        }
    }

    private static void run(String id, AdvancementFunctionSource source, int[] remaining) {
        for (String command : FUNCTIONS.get(id)) {
            if (--remaining[0] < 0) {
                LogUtil.getLogger().warn("Advancement function {} exceeded 65536 commands", id);
                return;
            }
            String[] parts = command.split("\\s+", 2);
            if (parts[0].equals("function")) run(AdvancementJson.id(parts[1]).toString(), source, remaining);
            else MinecraftServer.getServer().getCommandManager().executeCommand(source, source.resolveSelf(command), false);
        }
    }
}
