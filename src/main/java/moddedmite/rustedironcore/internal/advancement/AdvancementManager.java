package moddedmite.rustedironcore.internal.advancement;

import moddedmite.rustedironcore.api.advancement.Advancement;
import moddedmite.rustedironcore.api.advancement.AdvancementProgress;
import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.api.advancement.PlayerAdvancements;
import moddedmite.rustedironcore.api.advancement.SimpleCriterionTrigger;
import moddedmite.rustedironcore.api.util.LogUtil;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/** Loads server definitions and indexes their criterion listeners. */
public class AdvancementManager {

    private static final Map<String, Advancement> DEFINITIONS = new LinkedHashMap<>();
    private static final Map<String, Advancement> REGISTERED = new LinkedHashMap<>();
    private static final Map<SimpleCriterionTrigger<?>, List<AdvancementCriterion>> LISTENERS = new IdentityHashMap<>();
    private static final Set<AdvancementCriterion> FAILED_CRITERIA = new HashSet<>();
    private static boolean loaded;

    public static void register(Advancement advancement) {
        if (REGISTERED.putIfAbsent(advancement.id().toString(), advancement) != null) throw new IllegalArgumentException("Duplicate advancement: " + advancement.id());
        loaded = false;
    }

    public static Advancement get(ResourceLocation id) {
        ensureLoaded();
        return DEFINITIONS.get(id.toString());
    }

    public static Collection<Advancement> all() {
        ensureLoaded();
        return List.copyOf(DEFINITIONS.values());
    }

    public static <T> void trigger(SimpleCriterionTrigger<T> trigger, EntityPlayer player, Predicate<T> condition) {
        if (!(player instanceof ServerPlayer)) return;
        ensureLoaded();
        PlayerAdvancements state = Advancements.getProgress(player);
        for (AdvancementCriterion entry : LISTENERS.getOrDefault(trigger, List.of())) {
            AdvancementProgress progress = state.getProgress(entry.advancement().id());
            if (progress.hasCriterion(entry.name()) || progress.isDone(entry.advancement())) continue;
            if (FAILED_CRITERIA.contains(entry)) continue;
            boolean matches;
            try {
                matches = AdvancementPredicates.player(entry.criterion().conditions().get("player"), player)
                        && entry.criterion().matches(trigger, condition);
            } catch (RuntimeException exception) {
                FAILED_CRITERIA.add(entry);
                LogUtil.getLogger().warn("Disabled advancement criterion {} / {} until reload", entry.advancement().id(), entry.name(), exception);
                continue;
            }
            if (matches) state.award(player, entry.advancement(), entry.name());
        }
    }

    public static synchronized void ensureLoaded() {
        if (!loaded) load();
    }

    public static void reload() {
        synchronized (AdvancementManager.class) {
            load();
        }
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null && server.getConfigurationManager() != null) {
            for (Object raw : server.getConfigurationManager().playerEntityList) AdvancementNetwork.sendSnapshot((ServerPlayer) raw);
        }
    }

    private static void load() {
        loaded = true;
        DEFINITIONS.clear();
        LISTENERS.clear();
        FAILED_CRITERIA.clear();
        AdvancementItemPredicates.clear();
        AdvancementTags.reload();
        AdvancementLootConditions.reload();
        AdvancementFunctions.reload();
        AdvancementLootTables.reload();
        AdvancementResources.load().forEach((id, json) -> {
            try {
                var selected = AdvancementLoadConditions.select(json);
                if (selected == null) return;
                Advancement advancement = AdvancementJson.parse(id, selected, true);
                AdvancementRewards.validate(advancement.rewards());
                DEFINITIONS.put(id.toString(), advancement);
            } catch (RuntimeException exception) {
                LogUtil.getLogger().warn("Unable to load advancement {}: {}", id, exception.getMessage());
            }
        });
        REGISTERED.forEach((id, definition) -> {
            try {
                var selected = AdvancementLoadConditions.select(definition.json());
                if (selected == null) return;
                Advancement advancement = AdvancementJson.parse(definition.id(), selected, true);
                AdvancementRewards.validate(advancement.rewards());
                DEFINITIONS.putIfAbsent(id, advancement);
            } catch (RuntimeException exception) {
                LogUtil.getLogger().warn("Unable to load registered advancement {}: {}", id, exception.getMessage());
            }
        });
        Set<String> invalid = new HashSet<>();
        for (Advancement advancement : DEFINITIONS.values()) {
            Set<String> parents = new HashSet<>();
            Advancement current = advancement;
            while (current.parent() != null) {
                if (parents.size() >= 256 || !parents.add(current.id().toString()) || !DEFINITIONS.containsKey(current.parent().toString())) {
                    invalid.add(advancement.id().toString());
                    break;
                }
                current = DEFINITIONS.get(current.parent().toString());
            }
        }
        invalid.forEach(id -> {
            DEFINITIONS.remove(id);
            LogUtil.getLogger().warn("Unable to load advancement {}: missing parent, cycle, or parent chain deeper than 256", id);
        });
        for (Advancement advancement : DEFINITIONS.values()) {
            advancement.criteria().forEach((name, criterion) -> LISTENERS.computeIfAbsent(criterion.trigger(), ignored -> new ArrayList<>())
                    .add(new AdvancementCriterion(advancement, name, criterion)));
        }
        LogUtil.getLogger().info("Loaded {} JSON advancements", DEFINITIONS.size());
    }
}
