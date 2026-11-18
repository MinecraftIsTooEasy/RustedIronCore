package moddedmite.rustedironcore.api.advancement;

import com.google.gson.JsonObject;

import java.util.function.Predicate;

public record Criterion<T>(SimpleCriterionTrigger<T> trigger, T instance, JsonObject conditions) {

    public <S> boolean matches(SimpleCriterionTrigger<S> source, Predicate<S> predicate) {
        if (trigger != source) return false;
        // Identity of the trigger guarantees that the instance has its registered type.
        @SuppressWarnings("unchecked") S typedInstance = (S) instance;
        return predicate.test(typedInstance);
    }
}
