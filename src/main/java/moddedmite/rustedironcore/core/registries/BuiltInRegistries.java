package moddedmite.rustedironcore.core.registries;

import com.google.common.collect.Maps;
import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.api.util.LogUtil;
import moddedmite.rustedironcore.core.*;
import moddedmite.rustedironcore.data.recipes.RecipeProvider;
import moddedmite.rustedironcore.data.tags.TagsProvider;
import moddedmite.rustedironcore.internal.event.InternalRecipeProvider;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.serialization.Lifecycle;
import moddedmite.rustedironcore.world.item.crafting.RecipeBookCategories;
import moddedmite.rustedironcore.world.item.crafting.RecipeBookCategory;
import moddedmite.rustedironcore.world.item.crafting.RecipeType;
import net.minecraft.*;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@ApiStatus.Internal
@NotVanilla
public class BuiltInRegistries {
    private static final Logger LOGGER = LogUtil.getLogger();
    private static final Map<Identifier, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
    private static final WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY = new MappedRegistry<>(
            ResourceKey.createRegistryKey(Registries.ROOT_REGISTRY_NAME), Lifecycle.stable()
    );


    public static final DefaultedRegistry<Block> BLOCK = registerDefaultedWithIntrusiveHolders(Registries.BLOCK, "minecraft:stone", var0 -> Block.stone);
    public static final DefaultedRegistry<Item> ITEM = registerDefaultedWithIntrusiveHolders(Registries.ITEM, "minecraft:shovelIron", var0 -> Item.shovelIron);
    public static final Registry<Class<? extends Entity>> MOD_ENTITY = registerSimple(Registries.ENTITY, var0 -> EntityItem.class);
    public static final Registry<BiomeGenBase> MOD_BIOME = registerSimple(Registries.BIOME, var0 -> BiomeGenBase.plains);
    public static final Registry<Potion> MOD_POTION = registerSimple(Registries.POTION, var0 -> Potion.moveSpeed);
    public static final Registry<Achievement> ACHIEVEMENT = registerSimple(Registries.ACHIEVEMENT, var0 -> AchievementList.openInventory);
    public static final Registry<TagsProvider<?>> TAGS_PROVIDER = registerSimple(Registries.TAGS_PROVIDER, var0 -> new Object());
    public static final Registry<RecipeProvider> RECIPE_PROVIDER = registerSimple(Registries.RECIPE_PROVIDER, var0 -> new Object());
    public static final Registry<RecipeBookCategory> RECIPE_BOOK_CATEGORY = registerSimple(Registries.RECIPE_BOOK_CATEGORY, RecipeBookCategories::bootstrap);
    public static final Registry<RecipeType<?>> RECIPE_TYPE = registerSimple(Registries.RECIPE_TYPE, var0 -> RecipeType.CRAFTING);


    public static final Registry<? extends Registry<?>> REGISTRY = WRITABLE_REGISTRY;


    private static <T> Registry<T> registerSimple(final ResourceKey<? extends Registry<T>> name, final BuiltInRegistries.RegistryBootstrap<T> loader) {
        return internalRegister(name, new MappedRegistry<>(name, Lifecycle.stable(), false), loader);
    }

    private static <T> Registry<T> registerSimpleWithIntrusiveHolders(
            final ResourceKey<? extends Registry<T>> name, final BuiltInRegistries.RegistryBootstrap<T> loader
    ) {
        return internalRegister(name, new MappedRegistry<>(name, Lifecycle.stable(), true), loader);
    }

    private static <T> DefaultedRegistry<T> registerDefaulted(
            final ResourceKey<? extends Registry<T>> name, final String defaultKey, final BuiltInRegistries.RegistryBootstrap<T> loader
    ) {
        return internalRegister(name, new DefaultedMappedRegistry<>(defaultKey, name, Lifecycle.stable(), false), loader);
    }

    private static <T> DefaultedRegistry<T> registerDefaultedWithIntrusiveHolders(
            final ResourceKey<? extends Registry<T>> name, final String defaultKey, final BuiltInRegistries.RegistryBootstrap<T> loader
    ) {
        return internalRegister(name, new DefaultedMappedRegistry<>(defaultKey, name, Lifecycle.stable(), true), loader);
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    private static <T, R extends WritableRegistry<T>> R internalRegister(
            final ResourceKey<? extends Registry<T>> name, final R registry, final BuiltInRegistries.RegistryBootstrap<T> loader
    ) {
//        Bootstrap.checkBootstrapCalled(() -> "registry " + name.identifier());
        Identifier key = name.identifier();
        LOADERS.put(key, () -> loader.run(registry));
        WRITABLE_REGISTRY.register((ResourceKey<WritableRegistry<?>>) (Object) name, registry, RegistrationInfo.BUILT_IN);
        return registry;
    }

    public static void bootStrap() {
        createContents();
        freeze();
        validate(REGISTRY);
    }

    private static void createContents() {
        LOADERS.forEach((key, value) -> {
            if (value.get() == null) {
                LOGGER.error("Unable to bootstrap registry '{}'", key);
            }
        });
    }

    private static void freeze() {
        REGISTRY.freeze();

        for (Registry<?> registry : REGISTRY) {
            bindBootstrappedTagsToEmpty(registry);
            registry.freeze();
        }
    }

    @NotVanilla
    private static <T extends Registry<?>> void validate(final Registry<T> registry) {
        registry.forEach(r -> {
//            if (r.keySet().isEmpty()) {
//                Util.logAndPauseIfInIde("Registry '" + registry.getKey((T)r) + "' was empty after loading");
//            }

            if (r instanceof DefaultedRegistry) {
                Identifier key = ((DefaultedRegistry)r).getDefaultKey();
                Objects.requireNonNull(r.getValue(key), "Missing default of DefaultedMappedRegistry: " + key);
            }
        });
    }

    public static <T> HolderGetter<T> acquireBootstrapRegistrationLookup(final Registry<T> registry) {
        return ((WritableRegistry)registry).createRegistrationLookup();
    }

    private static void bindBootstrappedTagsToEmpty(final Registry<?> registry) {
        ((MappedRegistry)registry).bindAllTagsToEmpty();
    }

    @FunctionalInterface
    private interface RegistryBootstrap<T> {
        Object run(Registry<T> registry);
    }
}
