package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.Advancement;
import moddedmite.rustedironcore.api.advancement.AdvancementProgress;
import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.api.advancement.AdvancementContext;
import moddedmite.rustedironcore.api.advancement.AdvancementRecipes;
import net.minecraft.Entity;
import net.minecraft.EntityLiving;
import net.minecraft.EntityLivingBase;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.NBTTagCompound;
import net.minecraft.Potion;
import net.minecraft.PotionEffect;
import net.minecraft.Raycast;
import net.minecraft.RaycastCollision;

import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AdvancementEntityPredicates {

    public static void validate(JsonElement json) {
        if (json == null || json.isJsonNull()) return;
        if (json.isJsonArray()) {
            AdvancementLootConditions.validateAll(json);
            return;
        }
        JsonObject predicate = json.getAsJsonObject();
        AdvancementPredicates.keys(predicate, "type", "distance", "location", "stepping_on", "movement", "effects", "nbt", "flags", "equipment",
                "type_specific", "vehicle", "passenger", "targeted_entity", "periodic_tick", "team", "slots", "movement_affected_by");
        AdvancementPredicates.validateIds(predicate.get("type"));
        for (String key : Set.of("location", "stepping_on", "movement_affected_by")) if (predicate.has(key)) AdvancementLocationPredicates.validate(predicate.getAsJsonObject(key));
        if (predicate.has("slots")) predicate.getAsJsonObject("slots").entrySet().forEach(entry -> {
            AdvancementSlots.validate(entry.getKey());
            AdvancementItemPredicates.validate(entry.getValue().getAsJsonObject());
        });
        if (predicate.has("distance")) validateDistance(predicate.getAsJsonObject("distance"));
        if (predicate.has("movement")) {
            JsonObject movement = predicate.getAsJsonObject("movement");
            AdvancementPredicates.keys(movement, "x", "y", "z", "speed", "horizontal_speed", "vertical_speed", "fall_distance");
            movement.entrySet().forEach(entry -> AdvancementPredicates.validateRange(entry.getValue()));
        }
        if (predicate.has("nbt")) AdvancementItemPredicates.pattern(predicate.get("nbt").getAsString());
        if (predicate.has("flags")) {
            JsonObject flags = predicate.getAsJsonObject("flags");
            AdvancementPredicates.keys(flags, "is_on_fire", "is_sneaking", "is_sprinting", "is_swimming", "is_on_ground", "is_baby", "is_flying");
            for (JsonElement flag : flags.entrySet().stream().map(Map.Entry::getValue).toList()) {
                if (!flag.isJsonPrimitive() || !flag.getAsJsonPrimitive().isBoolean()) throw new IllegalArgumentException("Entity flag must be boolean");
            }
        }
        if (predicate.has("equipment")) {
            JsonObject equipment = predicate.getAsJsonObject("equipment");
            AdvancementPredicates.keys(equipment, "mainhand", "offhand", "head", "chest", "legs", "feet");
            equipment.entrySet().forEach(entry -> AdvancementItemPredicates.validate(entry.getValue().getAsJsonObject()));
        }
        if (predicate.has("effects")) validateEffects(predicate.getAsJsonObject("effects"));
        for (String key : Set.of("vehicle", "passenger", "targeted_entity")) validate(predicate.get(key));
        if (predicate.has("periodic_tick")) AdvancementJson.integer(predicate.get("periodic_tick"), 1, Integer.MAX_VALUE);
        if (predicate.has("type_specific")) AdvancementEntitySubPredicates.validate(predicate.getAsJsonObject("type_specific"));
    }

    public static void validateDistance(JsonObject json) {
        AdvancementPredicates.keys(json, "x", "y", "z", "absolute", "horizontal");
        json.entrySet().forEach(entry -> AdvancementPredicates.validateRange(entry.getValue()));
    }

    public static boolean distance(JsonObject json, double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
        double x = Math.abs(toX - fromX), y = Math.abs(toY - fromY), z = Math.abs(toZ - fromZ);
        return AdvancementPredicates.range(json.get("x"), x) && AdvancementPredicates.range(json.get("y"), y)
                && AdvancementPredicates.range(json.get("z"), z) && AdvancementPredicates.range(json.get("horizontal"), Math.sqrt(x * x + z * z))
                && AdvancementPredicates.range(json.get("absolute"), Math.sqrt(x * x + y * y + z * z));
    }

    public static boolean matches(JsonElement json, EntityPlayer player, Entity entity) {
        return matches(json, player, entity, player.worldObj.rand);
    }

    public static boolean matches(JsonElement json, EntityPlayer player, Entity entity, Random random) {
        if (json == null || json.isJsonNull()) return true;
        if (json.isJsonArray()) {
            return AdvancementLootConditions.all(json, AdvancementContext.of(player, entity).withRandom(random));
        }
        if (entity == null) return false;
        JsonObject predicate = json.getAsJsonObject();
        if (predicate.has("team") && (!(entity instanceof EntityLivingBase living) || living.getTeam() == null
                || !living.getTeam().func_96661_b().equals(predicate.get("team").getAsString()))) return false;
        if (predicate.has("type") && !AdvancementPredicates.anyId(predicate.get("type"), id -> AdvancementAdapters.entityType(id, entity))) return false;
        if (predicate.has("periodic_tick") && entity.ticksExisted % predicate.get("periodic_tick").getAsInt() != 0) return false;
        if (predicate.has("distance") && !distance(predicate.getAsJsonObject("distance"), player.posX, player.posY, player.posZ, entity.posX, entity.posY, entity.posZ)) return false;
        if (predicate.has("location") && !AdvancementLocationPredicates.matches(predicate.getAsJsonObject("location"), entity.worldObj, entity.posX, entity.posY, entity.posZ)) return false;
        if (predicate.has("stepping_on") && !AdvancementLocationPredicates.matches(predicate.getAsJsonObject("stepping_on"), entity.worldObj, entity.posX, entity.posY - 0.2, entity.posZ)) return false;
        if (predicate.has("movement_affected_by") && !AdvancementLocationPredicates.matches(predicate.getAsJsonObject("movement_affected_by"), entity.worldObj, entity.posX, entity.posY - 0.5, entity.posZ)) return false;
        if (predicate.has("slots")) for (Map.Entry<String, JsonElement> entry : predicate.getAsJsonObject("slots").entrySet())
            if (!AdvancementSlots.matches(entry.getKey(), entry.getValue().getAsJsonObject(), entity)) return false;
        if (predicate.has("movement")) {
            JsonObject movement = predicate.getAsJsonObject("movement");
            double x = entity.motionX, y = entity.motionY, z = entity.motionZ;
            if (!AdvancementPredicates.range(movement.get("x"), x) || !AdvancementPredicates.range(movement.get("y"), y)
                    || !AdvancementPredicates.range(movement.get("z"), z) || !AdvancementPredicates.range(movement.get("speed"), Math.sqrt(x * x + y * y + z * z))
                    || !AdvancementPredicates.range(movement.get("horizontal_speed"), Math.sqrt(x * x + z * z))
                    || !AdvancementPredicates.range(movement.get("vertical_speed"), Math.abs(y)) || !AdvancementPredicates.range(movement.get("fall_distance"), entity.fallDistance)) return false;
        }
        if (predicate.has("flags")) {
            for (Map.Entry<String, JsonElement> entry : predicate.getAsJsonObject("flags").entrySet()) {
                boolean value = switch (entry.getKey()) {
                    case "is_on_fire" -> entity.isBurning();
                    case "is_sneaking" -> entity.isSneaking();
                    case "is_sprinting" -> entity.isSprinting();
                    case "is_on_ground" -> entity.onGround;
                    case "is_baby" -> entity instanceof EntityLivingBase living && living.isChild();
                    case "is_swimming" -> false;
                    case "is_flying" -> entity instanceof EntityPlayer owner && owner.capabilities.isFlying;
                    default -> throw new IllegalArgumentException("Unknown entity flag: " + entry.getKey());
                };
                if (entry.getValue().getAsBoolean() != value) return false;
            }
        }
        if (predicate.has("nbt")) {
            NBTTagCompound data = new NBTTagCompound();
            entity.writeToNBT(data);
            if (entity instanceof EntityPlayer owner && owner.getHeldItemStack() != null) data.setCompoundTag("SelectedItem", owner.getHeldItemStack().writeToNBT(new NBTTagCompound()));
            if (!AdvancementItemPredicates.pattern(predicate.get("nbt").getAsString()).test(data)) return false;
        }
        if (predicate.has("equipment")) {
            if (!(entity instanceof EntityLivingBase living)) return false;
            for (Map.Entry<String, JsonElement> entry : predicate.getAsJsonObject("equipment").entrySet()) {
                int slot = switch (entry.getKey()) { case "mainhand" -> 0; case "feet" -> 1; case "legs" -> 2; case "chest" -> 3; case "head" -> 4; default -> -1; };
                ItemStack stack = slot < 0 ? null : living.getCurrentItemOrArmor(slot);
                if (!AdvancementItemPredicates.matches(entry.getValue().getAsJsonObject(), stack)) return false;
            }
        }
        if (predicate.has("effects") && !effects(predicate.getAsJsonObject("effects"), entity)) return false;
        if (!matches(predicate.get("vehicle"), player, entity.ridingEntity, random) || !matches(predicate.get("passenger"), player, entity.riddenByEntity, random)) return false;
        if (predicate.has("targeted_entity") && !matches(predicate.get("targeted_entity"), player, entity instanceof EntityLiving living ? living.getAttackTarget() : null, random)) return false;
        return !predicate.has("type_specific") || AdvancementEntitySubPredicates.matches(predicate.getAsJsonObject("type_specific"), player, entity);
    }

    public static void validateEffects(JsonObject json) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            AdvancementJson.id(entry.getKey());
            JsonObject effect = entry.getValue().getAsJsonObject();
            AdvancementPredicates.keys(effect, "amplifier", "duration", "ambient", "visible");
            AdvancementPredicates.validateRange(effect.get("amplifier"));
            AdvancementPredicates.validateRange(effect.get("duration"));
            for (String field : Set.of("ambient", "visible")) if (effect.has(field)
                    && (!effect.get(field).isJsonPrimitive() || !effect.get(field).getAsJsonPrimitive().isBoolean())) throw new IllegalArgumentException("Effect flag must be boolean: " + field);
        }
    }

    public static boolean effects(JsonObject json, Entity entity) {
        if (!(entity instanceof EntityLivingBase living)) return false;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            Potion type = AdvancementAdapters.effect(entry.getKey());
            PotionEffect active = type == null ? null : living.getActivePotionEffect(type);
            if (active == null) return false;
            JsonObject predicate = entry.getValue().getAsJsonObject();
            if (!AdvancementPredicates.range(predicate.get("amplifier"), active.getAmplifier()) || !AdvancementPredicates.range(predicate.get("duration"), active.getDuration())
                    || predicate.has("ambient") && predicate.get("ambient").getAsBoolean() != active.getIsAmbient()
                    || predicate.has("visible") && !predicate.get("visible").getAsBoolean()) return false;
        }
        return true;
    }

    public static void validatePlayer(JsonObject json) {
        AdvancementPredicates.keys(json, "type", "level", "gamemode", "advancements", "recipes", "stats", "looking_at");
        String type = AdvancementJson.string(json, "type", "player");
        if (!type.equals("player") && !type.equals("minecraft:player")) throw new IllegalArgumentException("Unsupported entity type-specific predicate: " + type);
        AdvancementPredicates.validateRange(json.get("level"));
        if (json.has("gamemode")) {
            JsonElement modes = json.get("gamemode");
            if (modes.isJsonArray()) modes.getAsJsonArray().forEach(mode -> validateGameMode(mode.getAsString()));
            else validateGameMode(modes.getAsString());
        }
        validate(json.get("looking_at"));
        if (json.has("stats")) for (JsonElement stat : json.getAsJsonArray("stats")) AdvancementStatistics.validate(stat.getAsJsonObject());
        if (json.has("recipes")) for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("recipes").entrySet()) {
            AdvancementJson.id(entry.getKey());
            if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isBoolean()) throw new IllegalArgumentException("Recipe requirement must be boolean");
        }
        if (json.has("advancements")) for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("advancements").entrySet()) {
            AdvancementJson.id(entry.getKey());
            if (!entry.getValue().isJsonObject() && (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isBoolean())) throw new IllegalArgumentException("Advancement requirement must be a boolean or criterion map");
            if (entry.getValue().isJsonObject()) for (JsonElement completed : entry.getValue().getAsJsonObject().entrySet().stream().map(Map.Entry::getValue).toList())
                if (!completed.isJsonPrimitive() || !completed.getAsJsonPrimitive().isBoolean()) throw new IllegalArgumentException("Criterion requirement must be boolean");
        }
    }

    public static boolean player(JsonObject json, Entity entity) {
        if (!(entity instanceof EntityPlayer player)) return false;
        if (!AdvancementPredicates.range(json.get("level"), player.getExperienceLevel())) return false;
        if (json.has("looking_at")) {
            RaycastCollision hit = new Raycast(player, 1, 100).performVsBlocksAndEntities().getNearestCollision();
            if (hit == null || !hit.isEntity() || !matches(json.get("looking_at"), player, hit.getEntityHit())) return false;
        }
        if (json.has("stats")) for (JsonElement stat : json.getAsJsonArray("stats")) if (!AdvancementStatistics.matches(stat.getAsJsonObject(), player)) return false;
        if (json.has("recipes")) for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("recipes").entrySet())
            if (AdvancementRecipes.isUnlocked(player, AdvancementJson.id(entry.getKey())) != entry.getValue().getAsBoolean()) return false;
        if (json.has("gamemode")) {
            String mode = player.capabilities.isCreativeMode ? "creative" : player.capabilities.allowEdit ? "survival" : "adventure";
            if (!AdvancementPredicates.anyId(json.get("gamemode"), mode::equals)) return false;
        }
        if (json.has("advancements")) for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("advancements").entrySet()) {
            Advancement advancement = Advancements.get(AdvancementJson.id(entry.getKey()));
            if (advancement == null) return false;
            AdvancementProgress progress = Advancements.getProgress(player).getProgress(advancement.id());
            if (entry.getValue().isJsonObject()) {
                for (Map.Entry<String, JsonElement> criterion : entry.getValue().getAsJsonObject().entrySet())
                    if (progress.hasCriterion(criterion.getKey()) != criterion.getValue().getAsBoolean()) return false;
            } else if (progress.isDone(advancement) != entry.getValue().getAsBoolean()) return false;
        }
        return true;
    }

    private static void validateGameMode(String mode) {
        if (!Set.of("survival", "creative", "adventure", "spectator").contains(mode)) throw new IllegalArgumentException("Unknown game mode: " + mode);
    }
}
