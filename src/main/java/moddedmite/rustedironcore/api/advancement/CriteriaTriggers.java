package moddedmite.rustedironcore.api.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.internal.advancement.AdvancementPredicates;
import moddedmite.rustedironcore.internal.advancement.AdvancementJson;
import moddedmite.rustedironcore.internal.advancement.AdvancementDamagePredicates;
import moddedmite.rustedironcore.internal.advancement.AdvancementEntityPredicates;
import moddedmite.rustedironcore.internal.advancement.AdvancementLocationPredicates;
import moddedmite.rustedironcore.internal.advancement.AdvancementMatching;
import moddedmite.rustedironcore.internal.advancement.AdvancementItemPredicates;
import net.minecraft.Block;
import net.minecraft.EntityPlayer;
import net.minecraft.Entity;
import net.minecraft.DamageSource;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;
import net.minecraft.World;

import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;

public class CriteriaTriggers {

    public static final SimpleCriterionTrigger<JsonObject> TICK = register("tick", json -> AdvancementPredicates.keys(json, "player"));
    public static final SimpleCriterionTrigger<JsonObject> IMPOSSIBLE = register("impossible", json -> AdvancementPredicates.keys(json, "player"));
    public static final SimpleCriterionTrigger<JsonObject> INVENTORY_CHANGED = register("inventory_changed", json -> {
        AdvancementPredicates.keys(json, "player", "items", "slots");
        if (json.has("items")) for (JsonElement item : json.getAsJsonArray("items")) AdvancementPredicates.validateItem(item.getAsJsonObject());
        if (json.has("slots")) {
            JsonObject slots = json.getAsJsonObject("slots");
            AdvancementPredicates.keys(slots, "occupied", "empty", "full");
            slots.entrySet().forEach(entry -> AdvancementPredicates.validateRange(entry.getValue()));
        }
    });
    public static final SimpleCriterionTrigger<JsonObject> CONSUME_ITEM = register("consume_item", json -> {
        AdvancementPredicates.keys(json, "player", "item");
        if (json.has("item")) AdvancementPredicates.validateItem(json.getAsJsonObject("item"));
    });
    public static final SimpleCriterionTrigger<JsonObject> PLACED_BLOCK = register("placed_block", json -> {
        AdvancementPredicates.keys(json, "player", "block", "state", "location", "item");
        if (json.has("state")) AdvancementPredicates.validateState(json.getAsJsonObject("state"));
        AdvancementPredicates.validateBlockConditions(json.get("location"));
        if (json.has("item")) AdvancementPredicates.validateItem(json.getAsJsonObject("item"));
    });
    public static final SimpleCriterionTrigger<JsonObject> ITEM_USED_ON_BLOCK = register("item_used_on_block", json -> {
        AdvancementPredicates.keys(json, "player", "location", "item");
        AdvancementPredicates.validateBlockConditions(json.get("location"));
        if (json.has("item")) AdvancementPredicates.validateItem(json.getAsJsonObject("item"));
    });
    public static final SimpleCriterionTrigger<JsonObject> CHANGED_DIMENSION = register("changed_dimension", json -> AdvancementPredicates.keys(json, "player", "from", "to"));
    public static final SimpleCriterionTrigger<JsonObject> DEFAULT_BLOCK_USE = registerBlockUse("default_block_use");
    public static final SimpleCriterionTrigger<JsonObject> ANY_BLOCK_USE = registerBlockUse("any_block_use");


    public static final SimpleCriterionTrigger<JsonObject> LOCATION = registerPlayerTrigger("location");
    public static final SimpleCriterionTrigger<JsonObject> SLEPT_IN_BED = registerPlayerTrigger("slept_in_bed");
    public static final SimpleCriterionTrigger<JsonObject> START_RIDING = registerPlayerTrigger("started_riding");
    public static final SimpleCriterionTrigger<JsonObject> ENTER_BLOCK = register("enter_block", json -> {
        AdvancementPredicates.keys(json, "player", "block", "state");
        if (json.has("block")) AdvancementPredicates.validateIds(json.get("block"));
        if (json.has("state")) AdvancementPredicates.validateState(json.getAsJsonObject("state"));
    });
    public static final SimpleCriterionTrigger<JsonObject> PLAYER_KILLED_ENTITY = registerKillTrigger("player_killed_entity");
    public static final SimpleCriterionTrigger<JsonObject> ENTITY_KILLED_PLAYER = registerKillTrigger("entity_killed_player");
    public static final SimpleCriterionTrigger<JsonObject> PLAYER_HURT_ENTITY = registerHurtTrigger("player_hurt_entity", "entity");
    public static final SimpleCriterionTrigger<JsonObject> ENTITY_HURT_PLAYER = registerHurtTrigger("entity_hurt_player", null);
    public static final SimpleCriterionTrigger<JsonObject> TAME_ANIMAL = registerEntityTrigger("tame_animal", "entity");
    public static final SimpleCriterionTrigger<JsonObject> SUMMONED_ENTITY = registerEntityTrigger("summoned_entity", "entity");
    public static final SimpleCriterionTrigger<JsonObject> BRED_ANIMALS = register("bred_animals", json -> {
        AdvancementPredicates.keys(json, "player", "parent", "partner", "child");
        for (String field : List.of("parent", "partner", "child")) AdvancementPredicates.validateEntity(json.get(field));
    });
    public static final SimpleCriterionTrigger<JsonObject> CURED_ZOMBIE_VILLAGER = register("cured_zombie_villager", json -> {
        AdvancementPredicates.keys(json, "player", "zombie", "villager");
        AdvancementPredicates.validateEntity(json.get("zombie"));
        AdvancementPredicates.validateEntity(json.get("villager"));
    });
    public static final SimpleCriterionTrigger<JsonObject> TRADE = register("villager_trade", json -> {
        AdvancementPredicates.keys(json, "player", "villager", "item");
        AdvancementPredicates.validateEntity(json.get("villager"));
        if (json.has("item")) AdvancementPredicates.validateItem(json.getAsJsonObject("item"));
    });
    public static final SimpleCriterionTrigger<JsonObject> FILLED_BUCKET = registerItemTrigger("filled_bucket");
    public static final SimpleCriterionTrigger<JsonObject> BREWED_POTION = register("brewed_potion", json -> {
        AdvancementPredicates.keys(json, "player", "potion");
        if (json.has("potion")) AdvancementJson.id(json.get("potion").getAsString());
    });
    public static final SimpleCriterionTrigger<JsonObject> USING_ITEM = registerItemTrigger("using_item");
    public static final SimpleCriterionTrigger<JsonObject> ENCHANTED_ITEM = register("enchanted_item", json -> {
        AdvancementPredicates.keys(json, "player", "item", "levels");
        if (json.has("item")) AdvancementPredicates.validateItem(json.getAsJsonObject("item"));
        AdvancementPredicates.validateRange(json.get("levels"));
    });
    public static final SimpleCriterionTrigger<JsonObject> ITEM_DURABILITY_CHANGED = register("item_durability_changed", json -> {
        AdvancementPredicates.keys(json, "player", "item", "durability", "delta");
        if (json.has("item")) AdvancementPredicates.validateItem(json.getAsJsonObject("item"));
        AdvancementPredicates.validateRange(json.get("durability"));
        AdvancementPredicates.validateRange(json.get("delta"));
    });
    public static final SimpleCriterionTrigger<JsonObject> EFFECTS_CHANGED = register("effects_changed", json -> {
        AdvancementPredicates.keys(json, "player", "effects", "source");
        if (json.has("effects")) AdvancementEntityPredicates.validateEffects(json.getAsJsonObject("effects"));
        AdvancementPredicates.validateEntity(json.get("source"));
    });
    public static final SimpleCriterionTrigger<JsonObject> CONSTRUCT_BEACON = register("construct_beacon", json -> {
        AdvancementPredicates.keys(json, "player", "level");
        AdvancementPredicates.validateRange(json.get("level"));
    });
    public static final SimpleCriterionTrigger<JsonObject> USED_ENDER_EYE = register("used_ender_eye", json -> {
        AdvancementPredicates.keys(json, "player", "distance");
        AdvancementPredicates.validateRange(json.get("distance"));
    });
    public static final SimpleCriterionTrigger<JsonObject> FISHING_ROD_HOOKED = register("fishing_rod_hooked", json -> {
        AdvancementPredicates.keys(json, "player", "rod", "entity", "item");
        for (String field : List.of("rod", "item")) if (json.has(field)) AdvancementPredicates.validateItem(json.getAsJsonObject(field));
        AdvancementPredicates.validateEntity(json.get("entity"));
    });
    public static final SimpleCriterionTrigger<JsonObject> PLAYER_INTERACTED_WITH_ENTITY = register("player_interacted_with_entity", json -> {
        AdvancementPredicates.keys(json, "player", "item", "entity");
        if (json.has("item")) AdvancementPredicates.validateItem(json.getAsJsonObject("item"));
        AdvancementPredicates.validateEntity(json.get("entity"));
    });
    public static final SimpleCriterionTrigger<JsonObject> NETHER_TRAVEL = registerDistanceTrigger("nether_travel");
    public static final SimpleCriterionTrigger<JsonObject> FALL_FROM_HEIGHT = registerDistanceTrigger("fall_from_height");
    public static final SimpleCriterionTrigger<JsonObject> RIDE_ENTITY_IN_LAVA = registerDistanceTrigger("ride_entity_in_lava");
    public static final SimpleCriterionTrigger<JsonObject> LEVITATION = register("levitation", json -> {
        AdvancementPredicates.keys(json, "player", "distance", "duration");
        if (json.has("distance")) AdvancementEntityPredicates.validateDistance(json.getAsJsonObject("distance"));
        AdvancementPredicates.validateRange(json.get("duration"));
    });
    public static final SimpleCriterionTrigger<JsonObject> USED_TOTEM = registerItemTrigger("used_totem");
    public static final SimpleCriterionTrigger<JsonObject> SHOT_CROSSBOW = registerItemTrigger("shot_crossbow");
    public static final SimpleCriterionTrigger<JsonObject> RAID_WIN = registerPlayerTrigger("hero_of_the_village");
    public static final SimpleCriterionTrigger<JsonObject> RAID_OMEN = registerPlayerTrigger("voluntary_exile");
    public static final SimpleCriterionTrigger<JsonObject> AVOID_VIBRATION = registerPlayerTrigger("avoid_vibration");
    public static final SimpleCriterionTrigger<JsonObject> KILL_MOB_NEAR_SCULK_CATALYST = registerKillTrigger("kill_mob_near_sculk_catalyst");
    public static final SimpleCriterionTrigger<JsonObject> ALLAY_DROP_ITEM_ON_BLOCK = registerBlockUse("allay_drop_item_on_block");
    public static final SimpleCriterionTrigger<JsonObject> HONEY_BLOCK_SLIDE = register("slide_down_block", json -> {
        AdvancementPredicates.keys(json, "player", "block", "state");
        if (json.has("block")) AdvancementPredicates.validateIds(json.get("block"));
        if (json.has("state")) AdvancementPredicates.validateState(json.getAsJsonObject("state"));
    });
    public static final SimpleCriterionTrigger<JsonObject> BEE_NEST_DESTROYED = register("bee_nest_destroyed", json -> {
        AdvancementPredicates.keys(json, "player", "block", "item", "num_bees_inside");
        if (json.has("block")) AdvancementPredicates.validateIds(json.get("block"));
        if (json.has("item")) AdvancementPredicates.validateItem(json.getAsJsonObject("item"));
        AdvancementPredicates.validateRange(json.get("num_bees_inside"));
    });
    public static final SimpleCriterionTrigger<JsonObject> TARGET_BLOCK_HIT = register("target_hit", json -> {
        AdvancementPredicates.keys(json, "player", "signal_strength", "projectile");
        AdvancementPredicates.validateRange(json.get("signal_strength"));
        AdvancementPredicates.validateEntity(json.get("projectile"));
    });
    public static final SimpleCriterionTrigger<JsonObject> CHANNELED_LIGHTNING = register("channeled_lightning", json -> {
        AdvancementPredicates.keys(json, "player", "victims");
        if (json.has("victims")) for (JsonElement victim : json.getAsJsonArray("victims")) AdvancementPredicates.validateEntity(victim);
    });
    public static final SimpleCriterionTrigger<JsonObject> KILLED_BY_CROSSBOW = register("killed_by_crossbow", json -> {
        AdvancementPredicates.keys(json, "player", "victims", "unique_entity_types");
        if (json.has("victims")) for (JsonElement victim : json.getAsJsonArray("victims")) AdvancementPredicates.validateEntity(victim);
        AdvancementPredicates.validateRange(json.get("unique_entity_types"));
    });
    public static final SimpleCriterionTrigger<JsonObject> THROWN_ITEM_PICKED_UP_BY_ENTITY = registerPickupTrigger("thrown_item_picked_up_by_entity");
    public static final SimpleCriterionTrigger<JsonObject> THROWN_ITEM_PICKED_UP_BY_PLAYER = registerPickupTrigger("thrown_item_picked_up_by_player");
    public static final SimpleCriterionTrigger<JsonObject> LIGHTNING_STRIKE = register("lightning_strike", json -> {
        AdvancementPredicates.keys(json, "player", "lightning", "bystander");
        AdvancementPredicates.validateEntity(json.get("lightning"));
        AdvancementPredicates.validateEntity(json.get("bystander"));
    });
    public static final SimpleCriterionTrigger<JsonObject> FALL_AFTER_EXPLOSION = register("fall_after_explosion", json -> {
        AdvancementPredicates.keys(json, "player", "distance", "start_position", "cause");
        if (json.has("distance")) AdvancementEntityPredicates.validateDistance(json.getAsJsonObject("distance"));
        if (json.has("start_position")) AdvancementPredicates.validateLocation(json.getAsJsonObject("start_position"));
        AdvancementPredicates.validateEntity(json.get("cause"));
    });
    public static final SimpleCriterionTrigger<JsonObject> RECIPE_UNLOCKED = register("recipe_unlocked", json -> {
        AdvancementPredicates.keys(json, "player", "recipe");
        AdvancementPredicates.validateIds(json.get("recipe"));
    });
    public static final SimpleCriterionTrigger<JsonObject> RECIPE_CRAFTED = register("recipe_crafted", json -> {
        AdvancementPredicates.keys(json, "player", "recipe_id", "ingredients");
        AdvancementPredicates.validateIds(json.get("recipe_id"));
        if (json.has("ingredients")) for (var ingredient : json.getAsJsonArray("ingredients")) AdvancementPredicates.validateItem(ingredient.getAsJsonObject());
    });
    public static final SimpleCriterionTrigger<JsonObject> CRAFTER_RECIPE_CRAFTED = register("crafter_recipe_crafted", json -> {
        AdvancementPredicates.keys(json, "player", "recipe_id", "ingredients");
        AdvancementPredicates.validateIds(json.get("recipe_id"));
        if (json.has("ingredients")) for (JsonElement item : json.getAsJsonArray("ingredients")) AdvancementPredicates.validateItem(item.getAsJsonObject());
    });
    public static final SimpleCriterionTrigger<JsonObject> GENERATE_LOOT = register("player_generates_container_loot", json -> {
        AdvancementPredicates.keys(json, "player", "loot_table");
        AdvancementPredicates.validateIds(json.get("loot_table"));
    });

    private static SimpleCriterionTrigger<JsonObject> registerPlayerTrigger(String name) {
        return register(name, json -> AdvancementPredicates.keys(json, "player"));
    }

    private static SimpleCriterionTrigger<JsonObject> registerItemTrigger(String name) {
        return register(name, json -> {
            AdvancementPredicates.keys(json, "player", "item");
            if (json.has("item")) AdvancementPredicates.validateItem(json.getAsJsonObject("item"));
        });
    }

    private static SimpleCriterionTrigger<JsonObject> registerEntityTrigger(String name, String field) {
        return register(name, json -> {
            AdvancementPredicates.keys(json, "player", field);
            AdvancementPredicates.validateEntity(json.get(field));
        });
    }

    private static SimpleCriterionTrigger<JsonObject> registerKillTrigger(String name) {
        return register(name, json -> {
            AdvancementPredicates.keys(json, "player", "entity", "killing_blow");
            AdvancementPredicates.validateEntity(json.get("entity"));
            if (json.has("killing_blow")) AdvancementDamagePredicates.validateSource(json.getAsJsonObject("killing_blow"));
        });
    }

    private static SimpleCriterionTrigger<JsonObject> registerHurtTrigger(String name, String entity) {
        return register(name, json -> {
            if (entity == null) AdvancementPredicates.keys(json, "player", "damage");
            else AdvancementPredicates.keys(json, "player", "damage", entity);
            if (json.has("damage")) AdvancementDamagePredicates.validate(json.getAsJsonObject("damage"));
            if (entity != null) AdvancementPredicates.validateEntity(json.get(entity));
        });
    }

    private static SimpleCriterionTrigger<JsonObject> registerDistanceTrigger(String name) {
        return register(name, json -> {
            AdvancementPredicates.keys(json, "player", "distance", "start_position");
            if (json.has("distance")) AdvancementEntityPredicates.validateDistance(json.getAsJsonObject("distance"));
            if (json.has("start_position")) AdvancementPredicates.validateLocation(json.getAsJsonObject("start_position"));
        });
    }

    private static SimpleCriterionTrigger<JsonObject> registerBlockUse(String name) {
        return register(name, json -> {
            AdvancementPredicates.keys(json, "player", "location");
            AdvancementPredicates.validateBlockConditions(json.get("location"));
        });
    }

    private static SimpleCriterionTrigger<JsonObject> registerPickupTrigger(String name) {
        return register(name, json -> {
            AdvancementPredicates.keys(json, "player", "item", "entity");
            if (json.has("item")) AdvancementPredicates.validateItem(json.getAsJsonObject("item"));
            AdvancementPredicates.validateEntity(json.get("entity"));
        });
    }

    private static SimpleCriterionTrigger<JsonObject> register(String name, Consumer<JsonObject> validator) {
        return Advancements.registerTrigger(new ResourceLocation("minecraft", name), new SimpleCriterionTrigger<>(
                new CriterionCodec<>(json -> { validator.accept(json); return json; }, json -> json)));
    }

    public static void init() {
        Advancements.registerItemTag(new ResourceLocation("minecraft", "logs"), stack -> stack.itemID == Block.wood.blockID);
        Advancements.registerItemTag(new ResourceLocation("minecraft", "logs_that_burn"), stack -> stack.itemID == Block.wood.blockID);
        Advancements.registerItemTag(new ResourceLocation("minecraft", "planks"), stack -> stack.itemID == Block.planks.blockID);
    }

    public static void inventoryChanged(EntityPlayer player) {
        List<ItemStack> items = new ArrayList<>();
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (stack != null && stack.stackSize > 0) items.add(stack);
        }
        inventoryChanged(player, items);
    }

    public static void inventoryChanged(EntityPlayer player, ItemStack changed) {
        inventoryChanged(player, changed == null || changed.stackSize <= 0 ? List.of() : List.of(changed));
    }

    public static void inventoryChanged(EntityPlayer player, List<ItemStack> changed) {
        INVENTORY_CHANGED.trigger(player, json -> {
            int occupied = 0, full = 0;
            for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
                ItemStack stack = player.inventory.getStackInSlot(slot);
                if (stack == null || stack.stackSize <= 0) continue;
                occupied++;
                if (stack.stackSize >= stack.getMaxStackSize()) full++;
            }
            if (json.has("slots")) {
                JsonObject slots = json.getAsJsonObject("slots");
                if (!AdvancementPredicates.range(slots.get("occupied"), occupied)
                        || !AdvancementPredicates.range(slots.get("empty"), player.inventory.getSizeInventory() - occupied)
                        || !AdvancementPredicates.range(slots.get("full"), full)) return false;
            }
            if (!json.has("items")) return true;
            if (json.getAsJsonArray("items").size() == 1) {
                JsonObject predicate = json.getAsJsonArray("items").get(0).getAsJsonObject();
                return changed.stream().anyMatch(stack -> stack != null && stack.stackSize > 0 && AdvancementPredicates.item(predicate, stack));
            }
            for (JsonElement predicate : json.getAsJsonArray("items")) {
                boolean found = false;
                for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
                    ItemStack stack = player.inventory.getStackInSlot(slot);
                    if (stack != null && stack.stackSize > 0 && AdvancementPredicates.item(predicate.getAsJsonObject(), stack)) { found = true; break; }
                }
                if (!found) return false;
            }
            return true;
        });
    }

    public static void consumedItem(EntityPlayer player, ItemStack stack) {
        CONSUME_ITEM.trigger(player, json -> !json.has("item") || AdvancementPredicates.item(json.getAsJsonObject("item"), stack));
    }

    public static void placedBlock(EntityPlayer player, World world, int x, int y, int z, ItemStack stack) {
        PLACED_BLOCK.trigger(player, json -> AdvancementPredicates.block(json, world, x, y, z)
                && AdvancementPredicates.blockConditions(json.get("location"), player, world, x, y, z, stack)
                && (!json.has("item") || AdvancementPredicates.item(json.getAsJsonObject("item"), stack)));
    }

    public static void itemUsedOnBlock(EntityPlayer player, World world, int x, int y, int z, ItemStack stack) {
        ITEM_USED_ON_BLOCK.trigger(player, json -> AdvancementPredicates.blockConditions(json.get("location"), player, world, x, y, z, stack)
                && (!json.has("item") || AdvancementPredicates.item(json.getAsJsonObject("item"), stack)));
        ANY_BLOCK_USE.trigger(player, json -> AdvancementPredicates.blockConditions(json.get("location"), player, world, x, y, z, stack));
    }

    public static void defaultBlockUse(EntityPlayer player, World world, int x, int y, int z) {
        DEFAULT_BLOCK_USE.trigger(player, json -> AdvancementPredicates.blockConditions(json.get("location"), player, world, x, y, z, player.getHeldItemStack()));
        ANY_BLOCK_USE.trigger(player, json -> AdvancementPredicates.blockConditions(json.get("location"), player, world, x, y, z, player.getHeldItemStack()));
    }

    public static void changedDimension(EntityPlayer player, int from, int to) {
        CHANGED_DIMENSION.trigger(player, json -> (!json.has("from") || AdvancementPredicates.dimension(json.get("from").getAsString(), from))
                && (!json.has("to") || AdvancementPredicates.dimension(json.get("to").getAsString(), to)));
    }
    public static void killed(EntityPlayer player, Entity entity, DamageSource source, boolean killedPlayer) {
        SimpleCriterionTrigger<JsonObject> trigger = killedPlayer ? ENTITY_KILLED_PLAYER : PLAYER_KILLED_ENTITY;
        trigger.trigger(player, json -> AdvancementPredicates.entity(json.get("entity"), player, entity)
                && (!json.has("killing_blow") || AdvancementDamagePredicates.source(json.getAsJsonObject("killing_blow"), player, source)));
    }

    public static void hurt(EntityPlayer player, Entity entity, DamageSource source, float dealt, float taken, boolean blocked, boolean hurtPlayer) {
        SimpleCriterionTrigger<JsonObject> trigger = hurtPlayer ? ENTITY_HURT_PLAYER : PLAYER_HURT_ENTITY;
        trigger.trigger(player, json -> AdvancementPredicates.entity(json.get("entity"), player, entity)
                && (!json.has("damage") || AdvancementDamagePredicates.matches(json.getAsJsonObject("damage"), player, source, dealt, taken, blocked)));
    }

    public static void enchantedItem(EntityPlayer player, ItemStack stack, int levels) {
        ENCHANTED_ITEM.trigger(player, json -> itemCondition(json, "item", stack) && AdvancementPredicates.range(json.get("levels"), levels));
    }

    public static void durabilityChanged(EntityPlayer player, ItemStack stack, int oldDamage) {
        ITEM_DURABILITY_CHANGED.trigger(player, json -> itemCondition(json, "item", stack)
                && AdvancementPredicates.range(json.get("durability"), stack.getMaxDamage() - stack.getItemDamage())
                && AdvancementPredicates.range(json.get("delta"), oldDamage - stack.getItemDamage()));
    }

    public static void effectsChanged(EntityPlayer player, Entity source) {
        EFFECTS_CHANGED.trigger(player, json -> (!json.has("effects") || AdvancementEntityPredicates.effects(json.getAsJsonObject("effects"), player))
                && AdvancementPredicates.entity(json.get("source"), player, source));
    }

    public static void traded(EntityPlayer player, Entity merchant, ItemStack result) {
        TRADE.trigger(player, json -> AdvancementPredicates.entity(json.get("villager"), player, merchant) && itemCondition(json, "item", result));
    }

    public static void bredAnimals(EntityPlayer player, Entity parent, Entity partner, Entity child) {
        BRED_ANIMALS.trigger(player, json -> AdvancementPredicates.entity(json.get("child"), player, child)
                && ((AdvancementPredicates.entity(json.get("parent"), player, parent) && AdvancementPredicates.entity(json.get("partner"), player, partner))
                || (AdvancementPredicates.entity(json.get("parent"), player, partner) && AdvancementPredicates.entity(json.get("partner"), player, parent))));
    }

    public static void curedZombieVillager(EntityPlayer player, Entity zombie, Entity villager) {
        CURED_ZOMBIE_VILLAGER.trigger(player, json -> AdvancementPredicates.entity(json.get("zombie"), player, zombie)
                && AdvancementPredicates.entity(json.get("villager"), player, villager));
    }

    public static void tamedAnimal(EntityPlayer player, Entity animal) {
        TAME_ANIMAL.trigger(player, json -> AdvancementPredicates.entity(json.get("entity"), player, animal));
    }

    public static void interactedWithEntity(EntityPlayer player, ItemStack stack, Entity entity) {
        PLAYER_INTERACTED_WITH_ENTITY.trigger(player, json -> itemCondition(json, "item", stack) && AdvancementPredicates.entity(json.get("entity"), player, entity));
    }

    public static void fishingRodHooked(EntityPlayer player, ItemStack rod, Entity hooked, List<ItemStack> loot) {
        FISHING_ROD_HOOKED.trigger(player, json -> itemCondition(json, "rod", rod)
                && AdvancementPredicates.entity(json.get("entity"), player, hooked)
                && (!json.has("item") || loot.stream().anyMatch(stack -> AdvancementPredicates.item(json.getAsJsonObject("item"), stack))));
    }

    public static void filledBucket(EntityPlayer player, ItemStack stack) {
        FILLED_BUCKET.trigger(player, json -> itemCondition(json, "item", stack));
    }

    public static void brewedPotion(EntityPlayer player, ItemStack stack) {
        BREWED_POTION.trigger(player, json -> !json.has("potion") || AdvancementItemPredicates.potion(json.get("potion").getAsString(), stack));
    }

    public static void constructedBeacon(EntityPlayer player, int level) {
        CONSTRUCT_BEACON.trigger(player, json -> AdvancementPredicates.range(json.get("level"), level));
    }

    public static void usedEnderEye(EntityPlayer player, double targetX, double targetZ) {
        double x = player.posX - targetX, z = player.posZ - targetZ;
        USED_ENDER_EYE.trigger(player, json -> AdvancementPredicates.range(json.get("distance"), Math.sqrt(x * x + z * z)));
    }

    public static void travelled(SimpleCriterionTrigger<JsonObject> trigger, EntityPlayer player, double startX, double startY, double startZ) {
        trigger.trigger(player, json -> (!json.has("distance") || AdvancementEntityPredicates.distance(json.getAsJsonObject("distance"), startX, startY, startZ, player.posX, player.posY, player.posZ))
                && (!json.has("start_position") || AdvancementLocationPredicates.matches(json.getAsJsonObject("start_position"), player.worldObj, startX, startY, startZ)));
    }

    public static void recipeUnlocked(EntityPlayer player, ResourceLocation recipe) {
        RECIPE_UNLOCKED.trigger(player, json -> !json.has("recipe") || AdvancementJson.id(json.get("recipe").getAsString()).equals(recipe));
    }

    public static void recipeCrafted(EntityPlayer player, ResourceLocation recipe, List<ItemStack> ingredients) {
        recipeCrafted(RECIPE_CRAFTED, player, recipe, ingredients);
    }

    public static void recipeCrafted(SimpleCriterionTrigger<JsonObject> trigger, EntityPlayer player, ResourceLocation recipe, List<ItemStack> ingredients) {
        trigger.trigger(player, json -> {
            if (json.has("recipe_id") && !AdvancementJson.id(json.get("recipe_id").getAsString()).equals(recipe)) return false;
            if (!json.has("ingredients")) return true;
            List<JsonObject> conditions = new ArrayList<>();
            for (JsonElement condition : json.getAsJsonArray("ingredients")) conditions.add(condition.getAsJsonObject());
            return AdvancementMatching.distinct(conditions, ingredients, AdvancementPredicates::item);
        });
    }

    public static void generatedLoot(EntityPlayer player, ResourceLocation table) {
        GENERATE_LOOT.trigger(player, json -> !json.has("loot_table") || AdvancementJson.id(json.get("loot_table").getAsString()).equals(table));
    }

    /** Event entry points for mechanics supplied by extension mods (totems, crossbows, raids, bees, etc.). */
    public static void itemEvent(SimpleCriterionTrigger<JsonObject> trigger, EntityPlayer player, ItemStack item) {
        trigger.trigger(player, json -> itemCondition(json, "item", item));
    }

    public static void blockEvent(SimpleCriterionTrigger<JsonObject> trigger, EntityPlayer player, World world, int x, int y, int z, ItemStack item) {
        trigger.trigger(player, json -> AdvancementPredicates.block(json, world, x, y, z)
                && AdvancementPredicates.blockConditions(json.get("location"), player, world, x, y, z, item));
    }

    public static void summonedEntity(EntityPlayer player, Entity entity) {
        SUMMONED_ENTITY.trigger(player, json -> AdvancementPredicates.entity(json.get("entity"), player, entity));
    }

    public static void pickedUpThrownItem(EntityPlayer player, ItemStack item, Entity other, boolean playerPickedUp) {
        (playerPickedUp ? THROWN_ITEM_PICKED_UP_BY_PLAYER : THROWN_ITEM_PICKED_UP_BY_ENTITY).trigger(player,
                json -> itemCondition(json, "item", item) && AdvancementPredicates.entity(json.get("entity"), player, other));
    }

    public static void lightningStrike(EntityPlayer player, Entity lightning, List<Entity> bystanders) {
        LIGHTNING_STRIKE.trigger(player, json -> AdvancementPredicates.entity(json.get("lightning"), player, lightning)
                && (!json.has("bystander") || bystanders.stream().anyMatch(entity -> AdvancementPredicates.entity(json.get("bystander"), player, entity))));
    }

    public static void channeledLightning(EntityPlayer player, List<Entity> victims) {
        CHANNELED_LIGHTNING.trigger(player, json -> {
            if (json.has("victims")) for (JsonElement predicate : json.getAsJsonArray("victims"))
                if (victims.stream().noneMatch(entity -> AdvancementPredicates.entity(predicate, player, entity))) return false;
            return true;
        });
    }

    public static void crossbowKills(EntityPlayer player, List<Entity> victims, int uniqueTypes) {
        KILLED_BY_CROSSBOW.trigger(player, json -> {
            if (!AdvancementPredicates.range(json.get("unique_entity_types"), uniqueTypes)) return false;
            List<JsonElement> predicates = new ArrayList<>();
            if (json.has("victims")) json.getAsJsonArray("victims").forEach(predicates::add);
            return AdvancementMatching.distinct(predicates, victims, (predicate, entity) -> AdvancementPredicates.entity(predicate, player, entity));
        });
    }

    public static void beeNestDestroyed(EntityPlayer player, World world, int x, int y, int z, ItemStack tool, int bees) {
        BEE_NEST_DESTROYED.trigger(player, json -> AdvancementPredicates.block(json, world, x, y, z)
                && itemCondition(json, "item", tool) && AdvancementPredicates.range(json.get("num_bees_inside"), bees));
    }

    public static void targetHit(EntityPlayer player, int signal, Entity projectile) {
        TARGET_BLOCK_HIT.trigger(player, json -> AdvancementPredicates.range(json.get("signal_strength"), signal)
                && AdvancementPredicates.entity(json.get("projectile"), player, projectile));
    }

    public static void levitated(EntityPlayer player, double x, double y, double z, int duration) {
        LEVITATION.trigger(player, json -> AdvancementPredicates.range(json.get("duration"), duration)
                && (!json.has("distance") || AdvancementEntityPredicates.distance(json.getAsJsonObject("distance"), x, y, z, player.posX, player.posY, player.posZ)));
    }

    public static void fellAfterExplosion(EntityPlayer player, double x, double y, double z, Entity cause) {
        FALL_AFTER_EXPLOSION.trigger(player, json -> AdvancementPredicates.entity(json.get("cause"), player, cause)
                && (!json.has("distance") || AdvancementEntityPredicates.distance(json.getAsJsonObject("distance"), x, y, z, player.posX, player.posY, player.posZ))
                && (!json.has("start_position") || AdvancementLocationPredicates.matches(json.getAsJsonObject("start_position"), player.worldObj, x, y, z)));
    }

    public static void tick(EntityPlayer player) {
        TICK.trigger(player, json -> true);
        if (player.isUsingItem()) USING_ITEM.trigger(player, json -> itemCondition(json, "item", player.getItemInUse()));
        int x = (int) Math.floor(player.posX), y = (int) Math.floor(player.posY), z = (int) Math.floor(player.posZ);
        ENTER_BLOCK.trigger(player, json -> AdvancementPredicates.block(json, player.worldObj, x, y, z));
        if (player.ticksExisted % 20 == 0) {
            LOCATION.trigger(player, json -> true);
        }
    }

    private static boolean itemCondition(JsonObject json, String field, ItemStack stack) {
        return !json.has(field) || AdvancementPredicates.item(json.getAsJsonObject(field), stack);
    }

}
