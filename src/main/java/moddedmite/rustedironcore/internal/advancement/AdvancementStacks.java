package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.internal.chat.NbtPathEvaluator;
import net.minecraft.ChatMessageComponent;
import net.minecraft.Enchantment;
import net.minecraft.ItemStack;
import net.minecraft.NBTBase;
import net.minecraft.NBTTagCompound;
import net.minecraft.NBTTagList;
import net.minecraft.NBTTagString;

import java.util.Map;

public class AdvancementStacks {

    public static ItemStack create(JsonObject json) {
        AdvancementPredicates.keys(json, "id", "item", "data", "count", "nbt", "components");
        ItemStack stack = Advancements.resolveItem(AdvancementJson.string(json, "id", AdvancementJson.string(json, "item", "minecraft:paper")));
        if (stack == null || stack.getItem() == null) throw new IllegalArgumentException("Unknown advancement item: " + json);
        if (json.has("data")) stack.setItemSubtype(AdvancementJson.integer(json.get("data"), 0, 32767));
        if (json.has("count")) stack.stackSize = AdvancementJson.integer(json.get("count"), 1, 65536);
        if (json.has("nbt")) stack.setTagCompound(NbtPathEvaluator.parseCompound(json.get("nbt").getAsString()));
        if (json.has("components")) apply(json.getAsJsonObject("components"), stack);
        return stack;
    }

    public static void apply(JsonObject components, ItemStack stack) {
        for (Map.Entry<String, JsonElement> entry : components.entrySet()) switch (AdvancementJson.id(entry.getKey()).toString()) {
            case "minecraft:damage" -> stack.setItemDamage(AdvancementJson.integer(entry.getValue(), 0, Integer.MAX_VALUE));
            case "minecraft:custom_name", "minecraft:item_name" -> {
                name(stack, ChatMessageComponent.createFromJson(entry.getValue().toString()).toStringWithFormatting(true));
            }
            case "minecraft:custom_data" -> {
                JsonElement value = entry.getValue();
                NBTTagCompound data = NbtPathEvaluator.parseCompound(value.isJsonPrimitive() ? value.getAsString() : value.toString());
                merge(tag(stack), data);
            }
            case "minecraft:lore" -> {
                NBTTagList lore = new NBTTagList("Lore");
                for (JsonElement line : entry.getValue().getAsJsonArray()) lore.appendTag(new NBTTagString("", ChatMessageComponent.createFromJson(line.toString()).toStringWithFormatting(true)));
                NBTTagCompound display = tag(stack).getCompoundTag("display");
                display.setTag("Lore", lore);
                tag(stack).setCompoundTag("display", display);
            }
            case "minecraft:enchantments", "minecraft:stored_enchantments" -> {
                JsonObject enchantments = entry.getValue().getAsJsonObject();
                AdvancementPredicates.keys(enchantments, "levels", "show_in_tooltip");
                if (!AdvancementJson.flag(enchantments, "show_in_tooltip", true)) throw new IllegalArgumentException("Hidden enchantment tooltips require a native item component adapter");
                NBTTagList list = new NBTTagList();
                for (Map.Entry<String, JsonElement> level : enchantments.getAsJsonObject("levels").entrySet()) {
                    Enchantment enchantment = AdvancementAdapters.enchantment(level.getKey());
                    if (enchantment == null) throw new IllegalArgumentException("Unknown enchantment: " + level.getKey());
                    NBTTagCompound value = new NBTTagCompound();
                    value.setShort("id", (short) enchantment.effectId);
                    value.setShort("lvl", (short) AdvancementJson.integer(level.getValue(), 0, 255));
                    list.appendTag(value);
                }
                tag(stack).setTag(entry.getKey().endsWith("stored_enchantments") ? "StoredEnchantments" : "ench", list);
            }
            default -> AdvancementItemPredicates.applyComponent(entry.getKey(), entry.getValue(), stack);
        }
    }

    public static void name(ItemStack stack, String name) {
        NBTTagCompound display = tag(stack).getCompoundTag("display");
        display.setString("Name", name);
        tag(stack).setCompoundTag("display", display);
    }

    public static void merge(NBTTagCompound target, NBTTagCompound source) {
        for (Object raw : source.getTags()) {
            NBTBase child = (NBTBase) raw;
            if (child instanceof NBTTagCompound compound && target.getTag(child.getName()) instanceof NBTTagCompound existing) merge(existing, compound);
            else target.setTag(child.getName(), child.copy());
        }
    }

    public static NBTTagCompound tag(ItemStack stack) {
        if (stack.stackTagCompound == null) stack.setTagCompound(new NBTTagCompound());
        return stack.stackTagCompound;
    }
}
