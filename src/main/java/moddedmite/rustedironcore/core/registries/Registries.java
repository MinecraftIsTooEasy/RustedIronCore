package moddedmite.rustedironcore.core.registries;

import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.core.Registry;
import moddedmite.rustedironcore.data.recipes.RecipeProvider;
import moddedmite.rustedironcore.data.tags.TagsProvider;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.world.item.crafting.Recipe;
import moddedmite.rustedironcore.world.item.crafting.RecipeBookCategory;
import moddedmite.rustedironcore.world.item.crafting.RecipeType;
import net.minecraft.*;

@NotVanilla
public class Registries {
    public static final Identifier ROOT_REGISTRY_NAME = Identifier.withDefaultNamespace("root");

    public static final ResourceKey<Registry<Block>> BLOCK = createRegistryKey("block");

    public static final ResourceKey<Registry<Item>> ITEM = createRegistryKey("item");

    public static final ResourceKey<Registry<Class<? extends Entity>>> ENTITY = createRegistryKey("entity");
    public static final ResourceKey<Registry<BiomeGenBase>> BIOME = createRegistryKey("biome");
    public static final ResourceKey<Registry<Potion>> POTION = createRegistryKey("potion");
    public static final ResourceKey<Registry<Achievement>> ACHIEVEMENT = createRegistryKey("achievement");
    public static final ResourceKey<Registry<TagsProvider<?>>> TAGS_PROVIDER = createRegistryKey("tags_provider");
    public static final ResourceKey<Registry<RecipeProvider>> RECIPE_PROVIDER = createRegistryKey("recipe_provider");
    public static final ResourceKey<Registry<Recipe<?>>> RECIPE = createRegistryKey("recipe");
    public static final ResourceKey<Registry<RecipeBookCategory>> RECIPE_BOOK_CATEGORY = createRegistryKey("recipe_book_category");
    public static final ResourceKey<Registry<RecipeType<?>>> RECIPE_TYPE = createRegistryKey("recipe_type");

//    public static ResourceKey<Level> levelStemToLevel(final ResourceKey<LevelStem> levelStem) {
//        return ResourceKey.create(DIMENSION, levelStem.identifier());
//    }
//
//    public static ResourceKey<LevelStem> levelToLevelStem(final ResourceKey<Level> level) {
//        return ResourceKey.create(LEVEL_STEM, level.identifier());
//    }

    private static <T> ResourceKey<Registry<T>> createRegistryKey(final String name) {
        return ResourceKey.createRegistryKey(Identifier.withDefaultNamespace(name));
    }

    private static String registryDirPath(final ResourceKey<? extends Registry<?>> registryKey) {
        return registryKey.identifier().getPath();
    }

    public static String elementsDirPath(final ResourceKey<? extends Registry<?>> registryKey) {
        return registryDirPath(registryKey);
    }

    public static String tagsDirPath(final ResourceKey<? extends Registry<?>> registryKey) {
        return "tags/" + registryDirPath(registryKey);
    }

    public static String componentsDirPath(final ResourceKey<? extends Registry<?>> registryKey) {
        return "components/" + registryDirPath(registryKey);
    }
}
