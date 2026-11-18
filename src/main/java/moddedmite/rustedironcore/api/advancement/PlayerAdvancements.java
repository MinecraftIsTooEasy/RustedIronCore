package moddedmite.rustedironcore.api.advancement;

import moddedmite.rustedironcore.internal.advancement.AdvancementNetwork;
import net.minecraft.EntityPlayer;
import net.minecraft.NBTTagCompound;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;

/** Criterion completion is stored in the player's own save and survives death and dimension changes. */
public class PlayerAdvancements {

    private final Map<String, AdvancementProgress> progress = new LinkedHashMap<>();
    private final Set<String> recipes = new LinkedHashSet<>();

    public boolean unlockRecipe(ResourceLocation id) {
        return recipes.add(id.toString());
    }

    public boolean hasRecipe(ResourceLocation id) {
        return recipes.contains(id.toString());
    }

    public Set<String> recipes() {
        return Collections.unmodifiableSet(recipes);
    }

    public void replaceRecipes(Set<String> ids) {
        recipes.clear();
        recipes.addAll(ids);
    }

    public AdvancementProgress getProgress(ResourceLocation id) {
        return getProgress(id.toString());
    }

    public AdvancementProgress getProgress(String id) {
        return progress.computeIfAbsent(id, ignored -> new AdvancementProgress());
    }

    public Map<String, AdvancementProgress> progress() {
        return Collections.unmodifiableMap(progress);
    }

    public boolean award(EntityPlayer player, Advancement advancement, String criterion) {
        if (!(player instanceof ServerPlayer serverPlayer) || !advancement.criteria().containsKey(criterion)) return false;
        AdvancementProgress state = getProgress(advancement.id().toString());
        boolean wasDone = state.isDone(advancement);
        if (!state.award(criterion)) return false;
        boolean completed = !wasDone && state.isDone(advancement);
        if (completed) Advancements.completed(serverPlayer, advancement);
        AdvancementNetwork.sendProgress(serverPlayer, advancement, state, completed);
        return true;
    }

    public boolean revoke(EntityPlayer player, Advancement advancement, String criterion) {
        if (!(player instanceof ServerPlayer serverPlayer)) return false;
        AdvancementProgress state = getProgress(advancement.id().toString());
        if (!state.revoke(criterion)) return false;
        AdvancementNetwork.sendProgress(serverPlayer, advancement, state, false);
        return true;
    }

    public NBTTagCompound write() {
        NBTTagCompound result = new NBTTagCompound();
        progress.forEach((id, state) -> {
            NBTTagCompound criteria = new NBTTagCompound();
            state.criteria().forEach(criteria::setLong);
            result.setCompoundTag(id, criteria);
        });
        NBTTagCompound unlocked = new NBTTagCompound();
        recipes.forEach(id -> unlocked.setBoolean(id, true));
        result.setCompoundTag("_recipes", unlocked);
        return result;
    }

    public void read(NBTTagCompound data) {
        progress.clear();
        recipes.clear();
        NBTTagCompound unlocked = data.getCompoundTag("_recipes");
        for (Object id : unlocked.tagMap.keySet()) if (unlocked.getBoolean((String) id)) recipes.add((String) id);
        for (Object rawId : data.tagMap.keySet()) {
            String id = (String) rawId;
            if (id.equals("_recipes")) continue;
            if (!(data.getTag(id) instanceof NBTTagCompound criteria)) continue;
            AdvancementProgress state = getProgress(id);
            for (Object rawName : criteria.tagMap.keySet()) {
                String name = (String) rawName;
                state.restore(name, criteria.getLong(name));
            }
        }
    }
}
