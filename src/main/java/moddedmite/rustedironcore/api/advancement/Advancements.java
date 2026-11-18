package moddedmite.rustedironcore.api.advancement;

import huix.glacier.api.registry.sync.RegistryHelper;
import huix.glacier.util.Identifier;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.item.VanillaItems;
import moddedmite.rustedironcore.internal.advancement.AdvancementJson;
import moddedmite.rustedironcore.internal.advancement.AdvancementAdapters;
import moddedmite.rustedironcore.internal.advancement.AdvancementManager;
import moddedmite.rustedironcore.internal.advancement.AdvancementRewards;
import moddedmite.rustedironcore.internal.advancement.AdvancementResources;
import moddedmite.rustedironcore.internal.advancement.AdvancementTags;
import moddedmite.rustedironcore.internal.advancement.AdvancementLocationPredicates;
import moddedmite.rustedironcore.internal.advancement.AdvancementItemPredicates;
import moddedmite.rustedironcore.internal.advancement.AdvancementPotions;
import moddedmite.rustedironcore.internal.advancement.AdvancementLootConditions;
import moddedmite.rustedironcore.internal.advancement.AdvancementLootFunctions;
import moddedmite.rustedironcore.internal.advancement.AdvancementStatistics;
import moddedmite.rustedironcore.internal.advancement.AdvancementEntitySubPredicates;
import moddedmite.rustedironcore.internal.advancement.AdvancementBlockStates;
import moddedmite.rustedironcore.internal.advancement.AdvancementLoadConditions;
import net.minecraft.Block;
import net.minecraft.EntityPlayer;
import net.minecraft.Entity;
import net.minecraft.BiomeGenBase;
import net.minecraft.Enchantment;
import net.minecraft.Potion;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;
import net.minecraft.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.nio.file.Path;

/**
 * Server-owned advancement definitions and extension registry. JSON is read from
 * {@code data/<namespace>/advancements/} (1.20), {@code data/<namespace>/advancement/}
 * (1.21), and the legacy {@code assets/<namespace>/advancements/} directory.
 * Register triggers and item/state/reward adapters during mod initialization.
 * Unsupported built-in predicates are rejected with a load diagnostic; modern
 * Java classes and arbitrary item components require an adapter in the owning mod.
 */
public class Advancements {

    private static final Map<String, SimpleCriterionTrigger<?>> TRIGGERS = new LinkedHashMap<>();
    private static final Map<SimpleCriterionTrigger<?>, ResourceLocation> TRIGGER_IDS = new IdentityHashMap<>();
    private static final List<Function<String, ItemStack>> ITEM_RESOLVERS = new ArrayList<>();
    private static final Map<String, List<Function<String, ItemStack>>> NAMESPACED_ITEM_RESOLVERS = new HashMap<>();
    private static final Map<String, Predicate<ItemStack>> ITEM_TAGS = new HashMap<>();
    private static final Map<String, Predicate<Block>> BLOCK_TAGS = new HashMap<>();
    private static final Map<Block, BlockStateResolver> BLOCK_STATES = new IdentityHashMap<>();

    public static <T extends SimpleCriterionTrigger<?>> T registerTrigger(ResourceLocation id, T trigger) {
        AdvancementJson.id(id.toString());
        if (TRIGGERS.containsKey(id.toString()) || TRIGGER_IDS.containsKey(trigger)) throw new IllegalArgumentException("Duplicate advancement trigger: " + id);
        TRIGGERS.put(id.toString(), trigger);
        TRIGGER_IDS.put(trigger, id);
        return trigger;
    }

    public static SimpleCriterionTrigger<?> getTrigger(ResourceLocation id) {
        return TRIGGERS.get(id.toString());
    }

    public static ResourceLocation getTriggerId(SimpleCriterionTrigger<?> trigger) {
        ResourceLocation id = TRIGGER_IDS.get(trigger);
        if (id == null) throw new IllegalArgumentException("Advancement trigger is not registered");
        return id;
    }

    public static void register(Advancement advancement) {
        AdvancementManager.register(advancement);
    }

    /** Adds a directory or ZIP data pack; world/datapacks entries have higher priority. */
    public static void registerDataPack(Path root) {
        AdvancementResources.registerPack(root);
    }

    public static void registerEntityType(ResourceLocation id, Predicate<Entity> predicate) {
        AdvancementAdapters.registerEntityType(id.toString(), predicate);
    }

    public static void registerEnchantment(ResourceLocation id, Enchantment enchantment) {
        AdvancementAdapters.registerEnchantment(id.toString(), enchantment);
    }

    public static void registerEffect(ResourceLocation id, Potion effect) {
        AdvancementAdapters.registerEffect(id.toString(), effect);
    }

    public static void registerBiome(ResourceLocation id, Predicate<BiomeGenBase> predicate) {
        AdvancementAdapters.registerBiome(id.toString(), predicate);
    }

    public static void registerStructure(ResourceLocation id, LocationCondition predicate) {
        AdvancementLocationPredicates.registerStructure(id.toString(), predicate);
    }

    public static void registerPotion(ResourceLocation id, Predicate<ItemStack> predicate) {
        AdvancementPotions.register(id.toString(), predicate);
    }

    public static void registerItemComponent(ResourceLocation id, boolean partial, JsonPredicate<ItemStack> predicate) {
        AdvancementItemPredicates.registerComponent(id.toString(), partial, predicate);
    }

    public static void registerLootCondition(ResourceLocation id, JsonPredicate<AdvancementContext> predicate) {
        AdvancementLootConditions.register(id.toString(), predicate);
    }

    public static void registerLoadCondition(ResourceLocation id, Predicate<JsonObject> condition) {
        AdvancementLoadConditions.register(id.toString(), condition);
    }

    public static boolean hasRegisteredTag(String type, String id) {
        return type.equals("item") && ITEM_TAGS.containsKey(id) || type.equals("block") && BLOCK_TAGS.containsKey(id);
    }

    public static void registerLootFunction(ResourceLocation id, LootFunction function) {
        AdvancementLootFunctions.register(id.toString(), function);
    }

    public static void registerStatistic(ResourceLocation type, ResourceLocation id, ToDoubleFunction<EntityPlayer> getter) {
        AdvancementStatistics.register(type.toString(), id.toString(), getter);
    }

    public static void registerEntitySubPredicate(ResourceLocation id, JsonPredicate<AdvancementContext> predicate) {
        AdvancementEntitySubPredicates.register(id.toString(), predicate);
    }

    /** Registers a legacy global adapter. Prefer the namespace overload for mod-owned items. */
    public static void registerItemResolver(Function<String, ItemStack> resolver) {
        ITEM_RESOLVERS.add(resolver);
    }

    /** The adapter receives a qualified ID and is called only for this namespace. */
    public static void registerItemResolver(String namespace, Function<String, ItemStack> resolver) {
        if (!namespace.matches("[a-z0-9_.-]+")) throw new IllegalArgumentException("Invalid item namespace: " + namespace);
        NAMESPACED_ITEM_RESOLVERS.computeIfAbsent(namespace, ignored -> new ArrayList<>()).add(resolver);
    }

    public static void registerItemTag(ResourceLocation id, Predicate<ItemStack> predicate) {
        ITEM_TAGS.put(id.toString(), predicate);
    }

    public static boolean matchesItemTag(String id, ItemStack stack) {
        Predicate<ItemStack> predicate = ITEM_TAGS.get(id);
        return AdvancementTags.allowsRegisteredEntries("item", id) && predicate != null && stack != null && predicate.test(stack) || AdvancementTags.matches("item", id, value -> {
            if (value.startsWith("#")) return matchesItemTag(value.substring(1), stack);
            ItemStack expected = resolveItem(value);
            return expected != null && stack != null && expected.getItem() == stack.getItem()
                    && (expected.getItem().getNumSubtypes() <= 1 || expected.getItem().isDamageable() || expected.getItemSubtype() == stack.getItemSubtype());
        });
    }

    public static void registerBlockTag(ResourceLocation id, Predicate<Block> predicate) {
        BLOCK_TAGS.put(id.toString(), predicate);
    }

    public static boolean matchesBlockTag(String id, Block block) {
        Predicate<Block> predicate = BLOCK_TAGS.get(id);
        return AdvancementTags.allowsRegisteredEntries("block", id) && predicate != null && block != null && predicate.test(block) || AdvancementTags.matches("block", id, value -> {
            if (value.startsWith("#")) return matchesBlockTag(value.substring(1), block);
            ItemStack expected = resolveItem(value);
            return expected != null && block != null && expected.itemID == block.blockID;
        });
    }

    public static boolean matchesBlockTag(String id, World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        Predicate<Block> predicate = BLOCK_TAGS.get(id);
        return AdvancementTags.allowsRegisteredEntries("block", id) && predicate != null && block != null && predicate.test(block)
                || AdvancementTags.matches("block", id, member -> AdvancementLocationPredicates.blockId(member, world, x, y, z));
    }

    public static void registerBlockStateResolver(Block block, BlockStateResolver resolver) {
        BLOCK_STATES.put(block, resolver);
    }

    public static Map<String, String> blockState(World world, int x, int y, int z) {
        BlockStateResolver resolver = BLOCK_STATES.get(world.getBlock(x, y, z));
        if (resolver != null) return resolver.resolve(world, x, y, z);
        return AdvancementBlockStates.resolve(world, x, y, z);
    }

    public static ItemStack resolveItem(String id) {
        ResourceLocation location = AdvancementJson.id(id);
        id = location.toString();
        for (Function<String, ItemStack> resolver : NAMESPACED_ITEM_RESOLVERS.getOrDefault(location.getResourceDomain(), List.of())) {
            ItemStack stack = resolver.apply(id);
            if (stack != null) return stack.copy();
        }
        for (Function<String, ItemStack> resolver : ITEM_RESOLVERS) {
            ItemStack stack = resolver.apply(id);
            if (stack != null) return stack.copy();
        }
        ItemStack vanilla = VanillaItems.resolve(id);
        if (vanilla != null) return vanilla;
        Item item = RegistryHelper.getItem(new Identifier(id));
        return item == null ? null : new ItemStack(item);
    }

    /** Register MITE equivalents for loot tables, recipe unlocks, or functions used by rewards. */
    public static void registerReward(String kind, ResourceLocation id, Consumer<ServerPlayer> reward) {
        AdvancementRewards.register(kind, id, reward);
    }

    public static void onCompleted(BiConsumer<ServerPlayer, Advancement> listener) {
        AdvancementRewards.onCompleted(listener);
    }

    public static PlayerAdvancements getProgress(EntityPlayer player) {
        return ((AdvancementPlayer) player).ric$getAdvancements();
    }

    public static Advancement get(ResourceLocation id) {
        return AdvancementManager.get(id);
    }

    public static Collection<Advancement> all() {
        return AdvancementManager.all();
    }

    public static boolean award(EntityPlayer player, ResourceLocation advancement, String criterion) {
        Advancement definition = get(advancement);
        return definition != null && getProgress(player).award(player, definition, criterion);
    }

    public static boolean revoke(EntityPlayer player, ResourceLocation advancement, String criterion) {
        Advancement definition = get(advancement);
        return definition != null && getProgress(player).revoke(player, definition, criterion);
    }

    public static <T> void trigger(SimpleCriterionTrigger<T> trigger, EntityPlayer player, Predicate<T> condition) {
        AdvancementManager.trigger(trigger, player, condition);
    }

    public static void ensureLoaded() {
        AdvancementManager.ensureLoaded();
    }

    public static void reload() {
        AdvancementManager.reload();
    }

    public static void completed(ServerPlayer player, Advancement advancement) {
        AdvancementRewards.completed(player, advancement);
    }
}
