package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.api.advancement.JsonPredicate;
import moddedmite.rustedironcore.api.advancement.ItemComponent;
import moddedmite.rustedironcore.internal.chat.NbtPathEvaluator;
import net.minecraft.Enchantment;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.NBTBase;
import net.minecraft.NBTTagCompound;
import net.minecraft.NBTTagList;
import net.minecraft.ChatMessageComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class AdvancementItemPredicates {

    private static final Map<String, Predicate<NBTBase>> NBT_PATTERNS = new HashMap<>();
    private static final Map<String, JsonPredicate<ItemStack>> COMPONENTS = new HashMap<>();
    private static final Map<String, JsonPredicate<ItemStack>> SUB_PREDICATES = new HashMap<>();

    public static void registerComponent(String id, boolean partial, JsonPredicate<ItemStack> predicate) {
        (partial ? SUB_PREDICATES : COMPONENTS).put(AdvancementJson.id(id).toString(), predicate);
    }

    public static void applyComponent(String id, JsonElement value, ItemStack stack) {
        JsonPredicate<ItemStack> predicate = component(COMPONENTS, id);
        if (!(predicate instanceof ItemComponent component)) throw new IllegalArgumentException("Item component has no stack writer: " + id);
        component.validate(value);
        component.apply(value, stack);
    }

    public static void clear() {
        NBT_PATTERNS.clear();
    }

    public static void validate(JsonObject json) {
        AdvancementPredicates.keys(json, "item", "items", "tag", "data", "count", "durability", "nbt", "enchantments", "stored_enchantments", "potion", "components", "predicates");
        if (json.has("potion")) AdvancementJson.id(json.get("potion").getAsString());
        if (json.has("item")) AdvancementJson.id(json.get("item").getAsString());
        AdvancementPredicates.validateIds(json.get("items"));
        if (json.has("tag")) AdvancementJson.id(json.get("tag").getAsString());
        for (String field : Set.of("data", "count", "durability")) AdvancementPredicates.validateRange(json.get(field));
        if (json.has("nbt")) pattern(json.get("nbt").getAsString());
        for (String field : Set.of("enchantments", "stored_enchantments")) {
            if (json.has(field)) validateEnchantments(json.get(field));
        }
        if (json.has("components")) for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("components").entrySet()) {
            switch (entry.getKey()) {
                case "minecraft:damage", "minecraft:max_damage" -> AdvancementJson.integer(entry.getValue(), 0, Integer.MAX_VALUE);
                case "minecraft:custom_data" -> pattern(snbt(entry.getValue()));
                case "minecraft:custom_name" -> ChatMessageComponent.createFromJson(entry.getValue().toString());
                default -> component(COMPONENTS, entry.getKey()).validate(entry.getValue());
            }
        }
        if (json.has("predicates")) for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("predicates").entrySet()) {
            switch (entry.getKey()) {
                case "minecraft:damage" -> {
                    JsonObject damage = entry.getValue().getAsJsonObject();
                    AdvancementPredicates.keys(damage, "damage", "durability");
                    AdvancementPredicates.validateRange(damage.get("damage"));
                    AdvancementPredicates.validateRange(damage.get("durability"));
                }
                case "minecraft:enchantments", "minecraft:stored_enchantments" -> validateEnchantments(entry.getValue());
                case "minecraft:custom_data" -> pattern(snbt(entry.getValue()));
                case "minecraft:potion_contents" -> {
                    AdvancementPredicates.keys(entry.getValue().getAsJsonObject(), "potions");
                    AdvancementPredicates.validateIds(entry.getValue().getAsJsonObject().get("potions"));
                }
                default -> component(SUB_PREDICATES, entry.getKey()).validate(entry.getValue());
            }
        }
    }

    public static boolean matches(JsonObject json, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return matchesEmpty(json);
        if (json.has("potion") && !potion(json.get("potion").getAsString(), stack)) return false;
        if (json.has("item") && !itemId(json.get("item").getAsString(), stack)) return false;
        if (json.has("items") && !AdvancementPredicates.anyId(json.get("items"), id -> itemId(id, stack))) return false;
        if (json.has("tag") && !Advancements.matchesItemTag(AdvancementJson.id(json.get("tag").getAsString()).toString(), stack)) return false;
        if (!AdvancementPredicates.range(json.get("data"), stack.getItemSubtype()) || !AdvancementPredicates.range(json.get("count"), stack.stackSize)) return false;
        if (json.has("durability") && !(json.get("durability").isJsonObject() && json.getAsJsonObject("durability").entrySet().isEmpty())
                && (!stack.isItemStackDamageable() || !AdvancementPredicates.range(json.get("durability"), stack.getMaxDamage() - stack.getItemDamage()))) return false;
        if (json.has("nbt") && !pattern(json.get("nbt").getAsString()).test(stack.stackTagCompound)) return false;
        for (String field : Set.of("enchantments", "stored_enchantments")) {
            if (json.has(field) && !enchantments(json.get(field), enchantmentTags(stack, field.equals("stored_enchantments")))) return false;
        }
        if (json.has("components")) for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("components").entrySet()) {
            boolean matches = switch (entry.getKey()) {
                case "minecraft:damage" -> stack.getItemDamage() == entry.getValue().getAsInt();
                case "minecraft:max_damage" -> stack.getMaxDamage() == entry.getValue().getAsInt();
                case "minecraft:custom_data" -> stack.stackTagCompound != null && exactData(entry.getValue(), stack.stackTagCompound);
                case "minecraft:custom_name" -> stack.hasDisplayName() && stack.getDisplayName().equals(ChatMessageComponent.createFromJson(entry.getValue().toString()).toStringWithFormatting(true));
                default -> component(COMPONENTS, entry.getKey()).matches(entry.getValue(), stack);
            };
            if (!matches) return false;
        }
        if (json.has("predicates")) for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("predicates").entrySet()) {
            boolean matches = switch (entry.getKey()) {
                case "minecraft:damage" -> stack.isItemStackDamageable()
                        && AdvancementPredicates.range(entry.getValue().getAsJsonObject().get("damage"), stack.getItemDamage())
                        && AdvancementPredicates.range(entry.getValue().getAsJsonObject().get("durability"), stack.getMaxDamage() - stack.getItemDamage());
                case "minecraft:enchantments", "minecraft:stored_enchantments" -> enchantments(entry.getValue(), enchantmentTags(stack, entry.getKey().equals("minecraft:stored_enchantments")));
                case "minecraft:custom_data" -> pattern(snbt(entry.getValue())).test(stack.stackTagCompound == null ? new NBTTagCompound() : stack.stackTagCompound);
                case "minecraft:potion_contents" -> !entry.getValue().getAsJsonObject().has("potions")
                        || AdvancementPredicates.anyId(entry.getValue().getAsJsonObject().get("potions"), id -> potion(id, stack));
                default -> component(SUB_PREDICATES, entry.getKey()).matches(entry.getValue(), stack);
            };
            if (!matches) return false;
        }
        return true;
    }

    private static boolean matchesEmpty(JsonObject json) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            JsonElement value = entry.getValue();
            boolean matches = switch (entry.getKey()) {
                case "count", "data" -> AdvancementPredicates.range(value, 0);
                case "item" -> AdvancementJson.id(value.getAsString()).toString().equals("minecraft:air");
                case "items" -> AdvancementPredicates.anyId(value, id -> !id.startsWith("#") && AdvancementJson.id(id).toString().equals("minecraft:air"));
                case "potion" -> AdvancementJson.id(value.getAsString()).toString().equals("minecraft:empty");
                case "enchantments", "stored_enchantments" -> value.getAsJsonArray().size() == 0;
                case "durability", "components" -> value.getAsJsonObject().entrySet().isEmpty();
                case "predicates" -> value.getAsJsonObject().entrySet().stream().allMatch(predicate -> switch (AdvancementJson.id(predicate.getKey()).toString()) {
                    case "minecraft:custom_data" -> pattern(snbt(predicate.getValue())).test(new NBTTagCompound());
                    case "minecraft:enchantments", "minecraft:stored_enchantments" -> predicate.getValue().getAsJsonArray().size() == 0;
                    default -> false;
                });
                default -> false;
            };
            if (!matches) return false;
        }
        return true;
    }

    public static boolean potion(String id, ItemStack stack) {
        return AdvancementPotions.matches(id, stack);
    }

    private static boolean exactData(JsonElement json, NBTTagCompound actual) {
        return NbtPathEvaluator.exactPredicate(snbt(json)).test(actual);
    }

    private static JsonPredicate<ItemStack> component(Map<String, JsonPredicate<ItemStack>> adapters, String id) {
        JsonPredicate<ItemStack> predicate = adapters.get(AdvancementJson.id(id).toString());
        if (predicate == null) throw new IllegalArgumentException("Unsupported item component predicate: " + id);
        return predicate;
    }

    private static boolean itemId(String id, ItemStack actual) {
        if (id.startsWith("#")) return Advancements.matchesItemTag(AdvancementJson.id(id.substring(1)).toString(), actual);
        ItemStack expected = Advancements.resolveItem(id);
        return expected != null && expected.getItem() == actual.getItem()
                && (expected.getItem().isDamageable() || expected.getItem().getNumSubtypes() <= 1
                || expected.getItemSubtype() == 32767 || expected.getItemSubtype() == actual.getItemSubtype());
    }

    public static Predicate<NBTBase> pattern(String snbt) {
        return NBT_PATTERNS.computeIfAbsent(snbt, NbtPathEvaluator::predicate);
    }

    private static String snbt(JsonElement value) {
        return value.isJsonPrimitive() ? value.getAsString() : value.toString();
    }

    private static NBTTagList enchantmentTags(ItemStack stack, boolean stored) {
        if (!stored) return stack.getEnchantmentTagList();
        return stack.getItem() == Item.enchantedBook ? stack.getStoredEnchantmentTagList() : null;
    }

    private static void validateEnchantments(JsonElement predicates) {
        for (JsonElement element : predicates.getAsJsonArray()) {
            JsonObject predicate = element.getAsJsonObject();
            AdvancementPredicates.keys(predicate, "enchantment", "enchantments", "levels");
            if (predicate.has("enchantment")) AdvancementJson.id(predicate.get("enchantment").getAsString());
            AdvancementPredicates.validateIds(predicate.get("enchantments"));
            AdvancementPredicates.validateRange(predicate.get("levels"));
        }
    }

    private static boolean enchantments(JsonElement predicates, NBTTagList tags) {
        for (JsonElement element : predicates.getAsJsonArray()) {
            JsonObject predicate = element.getAsJsonObject();
            boolean found = false;
            if (tags != null) for (int i = 0; i < tags.tagCount(); i++) {
                NBTTagCompound tag = (NBTTagCompound) tags.tagAt(i);
                if (!AdvancementPredicates.range(predicate.get("levels"), tag.getShort("lvl"))) continue;
                int effect = tag.getShort("id");
                if (predicate.has("enchantment") && !enchantmentId(predicate.get("enchantment").getAsString(), effect)) continue;
                if (predicate.has("enchantments") && !AdvancementPredicates.anyId(predicate.get("enchantments"), id -> enchantmentId(id, effect))) continue;
                found = true;
                break;
            }
            if (!found) return false;
        }
        return true;
    }

    private static boolean enchantmentId(String id, int effect) {
        if (id.startsWith("#")) return AdvancementTags.matches("enchantment", id.substring(1), value -> enchantmentId(value, effect));
        Enchantment enchantment = AdvancementAdapters.enchantment(id);
        return enchantment != null && enchantment.effectId == effect;
    }
}
