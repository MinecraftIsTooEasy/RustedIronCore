package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.AdvancementContext;
import moddedmite.rustedironcore.api.advancement.LootFunction;
import moddedmite.rustedironcore.api.chat.ChatComponents;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.internal.chat.NbtPathEvaluator;
import net.minecraft.Enchantment;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.NBTTagCompound;
import net.minecraft.NBTTagList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Reward stack transformations shared by table, pool and entry functions. */
public class AdvancementLootFunctions {

    private static final Map<String, LootFunction> ADAPTERS = new HashMap<>();

    public static void register(String id, LootFunction function) {
        ADAPTERS.put(AdvancementJson.id(id).toString(), function);
    }

    public static void validate(JsonElement functions) {
        if (functions == null) return;
        for (JsonElement element : functions.getAsJsonArray()) {
            JsonObject function = element.getAsJsonObject();
            AdvancementLootConditions.validateAll(function.get("conditions"));
            String type = AdvancementJson.id(function.get("function").getAsString()).toString();
            LootFunction adapter = ADAPTERS.get(type);
            if (adapter != null) { adapter.validate(function); continue; }
            switch (type) {
                case "minecraft:set_count", "minecraft:set_damage" -> {
                    String field = type.equals("minecraft:set_count") ? "count" : "damage";
                    AdvancementPredicates.keys(function, "function", "conditions", field, "add");
                    AdvancementNumberProviders.validate(function.get(field));
                }
                case "minecraft:set_name" -> {
                    AdvancementPredicates.keys(function, "function", "conditions", "name", "entity", "target");
                    if (function.has("name")) TextComponentSerializer.fromJson(function.get("name"));
                    if (function.has("entity") && !function.get("entity").getAsString().equals("this")) throw new IllegalArgumentException("Reward name context must be this");
                    String target = AdvancementJson.string(function, "target", "custom_name");
                    if (!target.equals("custom_name") && !target.equals("item_name")) throw new IllegalArgumentException("Unknown name target: " + target);
                }
                case "minecraft:limit_count" -> {
                    AdvancementPredicates.keys(function, "function", "conditions", "limit");
                    AdvancementNumberProviders.validateIntRange(function.get("limit"));
                }
                case "minecraft:set_nbt", "minecraft:set_custom_data" -> {
                    AdvancementPredicates.keys(function, "function", "conditions", "tag");
                    NbtPathEvaluator.parseCompound(function.get("tag").getAsString());
                }
                case "minecraft:set_components" -> {
                    AdvancementPredicates.keys(function, "function", "conditions", "components");
                    AdvancementStacks.apply(function.getAsJsonObject("components"), new ItemStack(Item.paper));
                }
                case "minecraft:set_enchantments" -> {
                    AdvancementPredicates.keys(function, "function", "conditions", "enchantments", "add");
                    if (function.has("enchantments")) for (Map.Entry<String, JsonElement> enchantment : function.getAsJsonObject("enchantments").entrySet()) {
                        if (AdvancementAdapters.enchantment(enchantment.getKey()) == null) throw new IllegalArgumentException("Unknown enchantment: " + enchantment.getKey());
                        AdvancementNumberProviders.validate(enchantment.getValue());
                    }
                }
                default -> throw new IllegalArgumentException("Unsupported loot function: " + type);
            }
            if (function.has("add") && !function.get("add").getAsJsonPrimitive().isBoolean()) throw new IllegalArgumentException("Loot add flag must be boolean");
        }
    }

    public static void apply(JsonElement functions, List<ItemStack> stacks, AdvancementContext context) {
        if (functions == null) return;
        for (JsonElement element : functions.getAsJsonArray()) {
            JsonObject function = element.getAsJsonObject();
            if (!AdvancementLootConditions.all(function.get("conditions"), context)) continue;
            String type = AdvancementJson.id(function.get("function").getAsString()).toString();
            LootFunction adapter = ADAPTERS.get(type);
            for (ItemStack stack : stacks) {
                if (adapter != null) { adapter.apply(function, stack, context); continue; }
                switch (type) {
                    case "minecraft:set_count" -> stack.stackSize = count((long) AdvancementNumberProviders.sampleInt(function.get("count"), context.random())
                            + (AdvancementJson.flag(function, "add", false) ? stack.stackSize : 0));
                    case "minecraft:set_damage" -> {
                        if (stack.isItemStackDamageable()) {
                            double remaining = AdvancementNumberProviders.sample(function.get("damage"), context.random());
                            if (AdvancementJson.flag(function, "add", false)) remaining += 1.0 - (double) stack.getItemDamage() / stack.getMaxDamage();
                            stack.setItemDamage((int) Math.floor((1 - Math.max(0, Math.min(1, remaining))) * stack.getMaxDamage()));
                        }
                    }
                    case "minecraft:set_name" -> {
                        if (function.has("name")) {
                            TextComponent text = TextComponentSerializer.fromJson(function.get("name"));
                            if (function.has("entity") && context.player() != null) text = ChatComponents.resolveFor(text, context.player(), context.entity());
                            AdvancementStacks.name(stack, ChatComponents.create(text).toStringWithFormatting(true));
                        }
                    }
                    case "minecraft:limit_count" -> stack.stackSize = count(AdvancementNumberProviders.clamp(function.get("limit"), stack.stackSize, context.random()));
                    case "minecraft:set_nbt", "minecraft:set_custom_data" -> AdvancementStacks.merge(AdvancementStacks.tag(stack), NbtPathEvaluator.parseCompound(function.get("tag").getAsString()));
                    case "minecraft:set_components" -> AdvancementStacks.apply(function.getAsJsonObject("components"), stack);
                    case "minecraft:set_enchantments" -> enchant(stack, function, context.random());
                    default -> throw new IllegalArgumentException("Unsupported loot function: " + type);
                }
            }
        }
    }

    private static int count(long count) {
        return (int) Math.max(0, Math.min(65536, count));
    }

    private static void enchant(ItemStack stack, JsonObject function, Random random) {
        if (stack.getItem() == Item.book) stack.itemID = Item.enchantedBook.itemID;
        String field = stack.getItem() == Item.enchantedBook ? "StoredEnchantments" : "ench";
        Map<Integer, Integer> levels = new LinkedHashMap<>();
        NBTTagList previous = AdvancementStacks.tag(stack).getTagList(field);
        for (int i = 0; i < previous.tagCount(); i++) {
            NBTTagCompound value = (NBTTagCompound) previous.tagAt(i);
            levels.put((int) value.getShort("id"), (int) value.getShort("lvl"));
        }
        if (function.has("enchantments")) for (Map.Entry<String, JsonElement> entry : function.getAsJsonObject("enchantments").entrySet()) {
            Enchantment enchantment = AdvancementAdapters.enchantment(entry.getKey());
            long level = AdvancementNumberProviders.sampleInt(entry.getValue(), random);
            if (AdvancementJson.flag(function, "add", false)) level += levels.getOrDefault(enchantment.effectId, 0);
            levels.put(enchantment.effectId, (int) Math.max(0, Math.min(255, level)));
        }
        NBTTagList result = new NBTTagList(field);
        levels.forEach((id, level) -> {
            if (level == 0) return;
            NBTTagCompound entry = new NBTTagCompound();
            entry.setShort("id", id.shortValue());
            entry.setShort("lvl", level.shortValue());
            result.appendTag(entry);
        });
        AdvancementStacks.tag(stack).setTag(field, result);
    }
}
