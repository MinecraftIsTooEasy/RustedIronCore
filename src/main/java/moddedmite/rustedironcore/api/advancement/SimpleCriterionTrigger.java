package moddedmite.rustedironcore.api.advancement;

import com.google.gson.JsonObject;
import net.minecraft.EntityPlayer;

import java.util.function.Predicate;

/**
 * MITE trigger API with a 1.20-style {@link #createInstance(JsonObject)} decoder or a
 * 1.21-style {@link #codec()} decoder/encoder. Forge and Mojang classes are not required.
 * Register once during mod initialization with {@link Advancements#registerTrigger}.
 *
 * <pre>{@code
 * var trigger = Advancements.registerTrigger(new ResourceLocation("example", "action"),
 *     new SimpleCriterionTrigger<>(new CriterionCodec<>(
 *         json -> json.get("count").getAsInt(),
 *         count -> { var json = new JsonObject(); json.addProperty("count", count); return json; })));
 * trigger.trigger(player, requiredCount -> actualCount >= requiredCount);
 * }</pre>
 * A legacy decoder may instead override {@code createInstance(JsonObject)} and use
 * {@link #createCriterion(JsonObject, Object)} when creating criteria from Java.
 */
public class SimpleCriterionTrigger<T> {

    private final CriterionCodec<T> instanceCodec;

    protected SimpleCriterionTrigger() {
        instanceCodec = null;
    }

    public SimpleCriterionTrigger(CriterionCodec<T> codec) {
        instanceCodec = codec;
    }

    public CriterionCodec<T> codec() {
        if (instanceCodec == null) throw new UnsupportedOperationException("Override codec() or createInstance(JsonObject)");
        return instanceCodec;
    }

    protected T createInstance(JsonObject conditions) {
        return codec().decode(conditions);
    }

    public Criterion<T> createCriterion(T instance) {
        return new Criterion<>(this, instance, codec().encode(instance));
    }

    public Criterion<T> createCriterion(JsonObject conditions, T instance) {
        return new Criterion<>(this, instance, conditions);
    }

    public final Criterion<T> parse(JsonObject conditions) {
        return new Criterion<>(this, createInstance(conditions), conditions);
    }

    public void trigger(EntityPlayer player, Predicate<T> condition) {
        Advancements.trigger(this, player, condition);
    }
}
