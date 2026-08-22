package moddedmite.rustedironcore.registry;

import net.minecraft.BiomeGenBase;
import net.minecraft.Entity;
import net.minecraft.Potion;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class Registries {
    public static final Registry<Class<? extends Entity>> MOD_ENTITY = Registry.create();
    public static final Registry<BiomeGenBase> MOD_BIOME = Registry.create();
    public static final Registry<Potion> MOD_POTION = Registry.create();
}
