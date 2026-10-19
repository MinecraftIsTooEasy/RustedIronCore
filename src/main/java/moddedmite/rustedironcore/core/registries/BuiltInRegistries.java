package moddedmite.rustedironcore.core.registries;

import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.core.DefaultedMappedRegistry;
import moddedmite.rustedironcore.core.DefaultedRegistry;
import moddedmite.rustedironcore.core.Registry;
import moddedmite.rustedironcore.data.tags.TagsProvider;
import moddedmite.rustedironcore.resources.Identifier;
import net.minecraft.*;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@NotVanilla
public class BuiltInRegistries {
    public static final DefaultedRegistry<Block> BLOCK = new DefaultedMappedRegistry<>(Identifier.withDefaultNamespace("air"), true);
    public static final DefaultedRegistry<Item> ITEM = new DefaultedMappedRegistry<>(Identifier.withDefaultNamespace("air"), true);
    public static final Registry<Class<? extends Entity>> MOD_ENTITY = Registry.create();
    public static final Registry<BiomeGenBase> MOD_BIOME = Registry.create();
    public static final Registry<Potion> MOD_POTION = Registry.create();
    public static final Registry<Achievement> ACHIEVEMENT = Registry.create();
    public static final Registry<TagsProvider<?>> TAGS_PROVIDER = Registry.create();
}
