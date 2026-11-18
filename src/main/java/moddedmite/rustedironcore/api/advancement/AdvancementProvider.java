package moddedmite.rustedironcore.api.advancement;

import java.util.function.Consumer;

/** Emits Java-built definitions for a data generation run. */
@FunctionalInterface
public interface AdvancementProvider {

    void generate(Consumer<Advancement> output);
}
