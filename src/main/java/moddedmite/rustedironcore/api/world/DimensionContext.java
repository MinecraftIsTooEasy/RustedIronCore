package moddedmite.rustedironcore.api.world;

import net.minecraft.WorldProvider;

import java.util.function.Supplier;

/**
 * Some params for the dimension. For other properties, override methods in your {@link WorldProvider}.
 */
public record DimensionContext(Supplier<WorldProvider> worldProviderFactory,
                               boolean hasSkyLight,
                               boolean hasCeiling
) {
}
