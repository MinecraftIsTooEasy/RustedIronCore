package moddedmite.rustedironcore.api.model;

import huix.glacier.util.Identifier;
import net.minecraft.Item;

import java.util.function.IntPredicate;

/** Explicit legacy item/metadata mapping; no global vanilla identity guesses are made by RIC. */
public record JsonItemDefinitionBinding(Item item, IntPredicate subtype, Identifier definition) {}
