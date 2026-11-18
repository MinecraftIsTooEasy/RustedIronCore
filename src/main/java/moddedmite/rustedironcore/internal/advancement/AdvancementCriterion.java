package moddedmite.rustedironcore.internal.advancement;

import moddedmite.rustedironcore.api.advancement.Advancement;
import moddedmite.rustedironcore.api.advancement.Criterion;

public record AdvancementCriterion(Advancement advancement, String name, Criterion<?> criterion) {

}
