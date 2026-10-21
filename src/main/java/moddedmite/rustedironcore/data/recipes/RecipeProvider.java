package moddedmite.rustedironcore.data.recipes;

import moddedmite.rustedironcore.annotations.NotFinished;
import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.core.HolderGetter;
import moddedmite.rustedironcore.core.registries.BuiltInRegistries;
import moddedmite.rustedironcore.tags.TagKey;
import moddedmite.rustedironcore.util.RecipeHelper;
import moddedmite.rustedironcore.world.item.ItemStackTemplate;
import moddedmite.rustedironcore.world.item.crafting.Ingredient;
import moddedmite.rustedironcore.world.level.ItemLike;
import net.minecraft.CraftingManager;
import net.minecraft.Item;

@NotFinished
public abstract class RecipeProvider {
    private final HolderGetter<Item> items;
    //	private final HolderGetter<TrimPattern> trimPatterns;
    protected final RecipeOutput output;

    //	protected final BootstrapContext<Advancement> advancementOutput;
//	private static final Map<BlockFamily.Variant, RecipeProvider.FamilyCraftingRecipeProvider> SHAPE_BUILDERS = ImmutableMap.<BlockFamily.Variant, RecipeProvider.FamilyCraftingRecipeProvider>builder()
//		.put(BlockFamily.Variant.BUTTON, (context, result, base) -> context.buttonBuilder(result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.CHISELED, (context, result, base) -> context.chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.CARPET, (context, result, base) -> context.carpetBuilder(RecipeCategory.DECORATIONS, result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.CUT, (context, result, base) -> context.cutBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.DOOR, (context, result, base) -> context.doorBuilder(result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.CUSTOM_FENCE, (context, result, base) -> context.fenceBuilder(result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.FENCE, (context, result, base) -> context.fenceBuilder(result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.CUSTOM_FENCE_GATE, (context, result, base) -> context.fenceGateBuilder(result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.FENCE_GATE, (context, result, base) -> context.fenceGateBuilder(result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.SIGN, (context, result, base) -> context.signBuilder(result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.CUSTOM_HANGING_SIGN, (context, result, base) -> context.hangingSignBuilder(result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.HANGING_SIGN, (context, result, base) -> context.hangingSignBuilder(result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.SLAB, (context, result, base) -> context.slabBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.STAIRS, (context, result, base) -> context.stairBuilder(result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.PRESSURE_PLATE, (context, result, base) -> context.pressurePlateBuilder(RecipeCategory.REDSTONE, result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.POLISHED, (context, result, base) -> context.polishedBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.TRAPDOOR, (context, result, base) -> context.trapdoorBuilder(result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.WALL, (context, result, base) -> context.wallBuilder(RecipeCategory.DECORATIONS, result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.BRICKS, (context, result, base) -> context.bricksBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.TILES, (context, result, base) -> context.tilesBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(base)))
//		.put(BlockFamily.Variant.PILLAR, (context, result, base) -> context.pillarBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(base)))
//		.build();
//	private static final Map<BlockFamily.Variant, RecipeProvider.FamilyStonecutterRecipeProvider> STONECUTTER_RECIPE_BUILDERS = ImmutableMap.<BlockFamily.Variant, RecipeProvider.FamilyStonecutterRecipeProvider>builder()
//		.put(BlockFamily.Variant.SLAB, (context, result, base) -> context.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, result, base, 2))
//		.put(BlockFamily.Variant.STAIRS, (context, result, base) -> context.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, result, base, 1))
//		.put(BlockFamily.Variant.BRICKS, (context, result, base) -> context.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, result, base, 1))
//		.put(BlockFamily.Variant.WALL, (context, result, base) -> context.stonecutterResultFromBase(RecipeCategory.DECORATIONS, result, base, 1))
//		.put(BlockFamily.Variant.CHISELED, (context, result, base) -> context.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, result, base, 1))
//		.put(BlockFamily.Variant.POLISHED, (context, result, base) -> context.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, result, base, 1))
//		.put(BlockFamily.Variant.CUT, (context, result, base) -> context.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, result, base, 1))
//		.put(BlockFamily.Variant.TILES, (context, result, base) -> context.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, result, base, 1))
//		.put(BlockFamily.Variant.PILLAR, (context, result, base) -> context.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, result, base, 1))
//		.put(BlockFamily.Variant.COBBLED, (context, result, base) -> context.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, result, base, 1))
//		.build();
//
    @SuppressWarnings("unchecked")
    @NotVanilla
    protected RecipeProvider() {
        this.items = BuiltInRegistries.ITEM;
        this.output = recipe -> {
            RecipeHelper.buildRecipe(recipe);
            CraftingManager.getInstance().getRecipeList().add(recipe);
        };
    }

//	protected RecipeProvider(final BootstrapContext<Recipe<?>> recipeOutput, final BootstrapContext<Advancement> advancementOutput) {
//		this.advancementOutput = advancementOutput;
//		this.output = new RecipeOutput() {
//			@Override
//			public void accept(final ResourceKey<Recipe<?>> id, final Recipe<?> recipe, final @Nullable AdvancementHolder advancementHolder) {
//				recipeOutput.register(id, recipe);
//				if (advancementHolder != null) {
//					this.acceptAdvancement(advancementHolder);
//				}
//			}
//
//			private void acceptAdvancement(final AdvancementHolder advancementHolder) {
//				advancementHolder.register(advancementOutput);
//			}
//
//			@Override
//			public Advancement.Builder advancement() {
//				return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
//			}
//
//			@Override
//			public <S> HolderGetter<S> lookup(final ResourceKey<? extends Registry<? extends S>> key) {
//				return recipeOutput.lookup(key);
//			}
//
//			@Deprecated
//			@Override
//			public <S> Stream<Holder.Reference<S>> listContextElements(final ResourceKey<? extends Registry<? extends S>> key) {
//				return recipeOutput.listContextElements(key);
//			}
//		};
//		this.items = recipeOutput.lookup(Registries.ITEM);
//		this.trimPatterns = recipeOutput.lookup(Registries.TRIM_PATTERN);
//	}

    public abstract void buildRecipes();

//	public void generateForEnabledBlockFamilies(final FeatureFlagSet flagSet) {
//		BlockFamilies.getAllFamilies().forEach(family -> this.generateRecipes(family, flagSet));
//	}
//

//	public void oneToOneConversionRecipe(final ItemLike product, final ItemLike resource, final @Nullable String group) {
//		this.oneToOneConversionRecipe(product, resource, group, 1);
//	}
//

//	public void oneToOneConversionRecipe(final ItemLike product, final ItemLike resource, final @Nullable String group, final int productCount) {
//		this.shapeless(RecipeCategory.MISC, product, productCount)
//			.requires(resource)
//			.group(group)
//			.unlockedBy(getHasName(resource), this.has(resource))
//			.save(this.output, getConversionRecipeName(product, resource));
//	}
//

//	public void oreSmelting(
//		final List<ItemLike> smeltables,
//		final RecipeCategory craftingCategory,
//		final CookingBookCategory cookingCategory,
//		final ItemLike result,
//		final float experience,
//		final int cookingTime,
//		final String group
//	) {
//		this.oreCooking(SmeltingRecipe::new, smeltables, craftingCategory, cookingCategory, result, experience, cookingTime, group, "_from_smelting");
//	}
//

//	public void oreBlasting(
//		final List<ItemLike> smeltables,
//		final RecipeCategory craftingCategory,
//		final CookingBookCategory cookingCategory,
//		final ItemLike result,
//		final float experience,
//		final int cookingTime,
//		final String group
//	) {
//		this.oreCooking(BlastingRecipe::new, smeltables, craftingCategory, cookingCategory, result, experience, cookingTime, group, "_from_blasting");
//	}
//

//	public final <T extends AbstractCookingRecipe> void oreCooking(
//		final AbstractCookingRecipe.Factory<T> factory,
//		final List<ItemLike> smeltables,
//		final RecipeCategory craftingCategory,
//		final CookingBookCategory cookingCategory,
//		final ItemLike result,
//		final float experience,
//		final int cookingTime,
//		final String group,
//		final String fromDesc
//	) {
//		for (ItemLike item : smeltables) {
//			SimpleCookingRecipeBuilder.generic(Ingredient.of(item), craftingCategory, cookingCategory, result, experience, cookingTime, factory)
//				.group(group)
//				.unlockedBy(getHasName(item), this.has(item))
//				.save(this.output, getItemName(result) + fromDesc + "_" + getItemName(item));
//		}
//	}
//

//	public void netheriteSmithing(final Item base, final RecipeCategory category, final Item result) {
//		SmithingTransformRecipeBuilder.smithing(
//				Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(base), this.tag(ItemTags.NETHERITE_TOOL_MATERIALS), category, result
//			)
//			.unlocks("has_netherite_ingot", this.has(ItemTags.NETHERITE_TOOL_MATERIALS))
//			.save(this.output, getItemName(result) + "_smithing");
//	}
//

//	public void trimSmithing(final Item trimTemplate, final ResourceKey<TrimPattern> patternId, final ResourceKey<Recipe<?>> id) {
//		Holder.Reference<TrimPattern> pattern = this.trimPatterns.getOrThrow(patternId);
//		SmithingTrimRecipeBuilder.smithingTrim(
//				Ingredient.of(trimTemplate), this.tag(ItemTags.TRIMMABLE_ARMOR), this.tag(ItemTags.TRIM_MATERIALS), pattern, RecipeCategory.MISC
//			)
//			.unlocks("has_smithing_trim_template", this.has(trimTemplate))
//			.save(this.output, id);
//	}
//

//	public void twoByTwoPacker(final RecipeCategory category, final ItemLike result, final ItemLike ingredient) {
//		this.shaped(category, result, 1)
//			.define('#', ingredient)
//			.pattern("##")
//			.pattern("##")
//			.unlockedBy(getHasName(ingredient), this.has(ingredient))
//			.save(this.output);
//	}
//

//	public void threeByThreePacker(final RecipeCategory category, final ItemLike result, final ItemLike ingredient, final String unlockedBy) {
//		this.shapeless(category, result).requires(ingredient, 9).unlockedBy(unlockedBy, this.has(ingredient)).save(this.output);
//	}
//

//	public void threeByThreePacker(final RecipeCategory category, final ItemLike result, final ItemLike ingredient) {
//		this.threeByThreePacker(category, result, ingredient, getHasName(ingredient));
//	}
//

//	public void planksFromLog(final ItemLike result, final TagKey<Item> logs, final int count) {
//		this.shapeless(RecipeCategory.BUILDING_BLOCKS, result, count).requires(logs).group("planks").unlockedBy("has_log", this.has(logs)).save(this.output);
//	}
//

//	public void planksFromLogs(final ItemLike result, final TagKey<Item> logs, final int count) {
//		this.shapeless(RecipeCategory.BUILDING_BLOCKS, result, count).requires(logs).group("planks").unlockedBy("has_logs", this.has(logs)).save(this.output);
//	}
//

//	public void woodFromLogs(final ItemLike result, final ItemLike log) {
//		this.shaped(RecipeCategory.BUILDING_BLOCKS, result, 3)
//			.define('#', log)
//			.pattern("##")
//			.pattern("##")
//			.group("bark")
//			.unlockedBy("has_log", this.has(log))
//			.save(this.output);
//	}
//

//	public void woodenBoat(final ItemLike result, final ItemLike planks) {
//		this.shaped(RecipeCategory.TRANSPORTATION, result)
//			.define('#', planks)
//			.pattern("# #")
//			.pattern("###")
//			.group("boat")
//			.unlockedBy("in_water", insideOf(Blocks.WATER))
//			.save(this.output);
//	}
//

//	public void chestBoat(final ItemLike chestBoat, final ItemLike boat) {
//		this.shapeless(RecipeCategory.TRANSPORTATION, chestBoat)
//			.requires(Blocks.CHEST)
//			.requires(boat)
//			.group("chest_boat")
//			.unlockedBy("has_boat", this.has(ItemTags.BOATS))
//			.save(this.output);
//	}
//

//	public final RecipeBuilder buttonBuilder(final ItemLike result, final Ingredient base) {
//		return this.shapeless(RecipeCategory.REDSTONE, result).requires(base);
//	}
//

//	public RecipeBuilder doorBuilder(final ItemLike result, final Ingredient base) {
//		return this.shaped(RecipeCategory.REDSTONE, result, 3).define('#', base).pattern("##").pattern("##").pattern("##");
//	}
//

//	public final RecipeBuilder fenceBuilder(final ItemLike result, final Ingredient base) {
//		int count = result == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
//		Item base2 = result == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
//		return this.shaped(RecipeCategory.DECORATIONS, result, count).define('W', base).define('#', base2).pattern("W#W").pattern("W#W");
//	}
//

//	public final RecipeBuilder fenceGateBuilder(final ItemLike result, final Ingredient planks) {
//		return this.shaped(RecipeCategory.REDSTONE, result).define('#', Items.STICK).define('W', planks).pattern("#W#").pattern("#W#");
//	}
//

//	public void pressurePlate(final ItemLike result, final ItemLike base) {
//		this.pressurePlateBuilder(RecipeCategory.REDSTONE, result, Ingredient.of(base)).unlockedBy(getHasName(base), this.has(base)).save(this.output);
//	}
//

//	public final RecipeBuilder pressurePlateBuilder(final RecipeCategory category, final ItemLike result, final Ingredient base) {
//		return this.shaped(category, result).define('#', base).pattern("##");
//	}
//

//	public void slab(final RecipeCategory category, final ItemLike result, final ItemLike base) {
//		this.slabBuilder(category, result, Ingredient.of(base)).unlockedBy(getHasName(base), this.has(base)).save(this.output);
//	}
//

//	public void shelf(final ItemLike result, final ItemLike strippedLogs) {
//		this.shaped(RecipeCategory.DECORATIONS, result, 6)
//			.define('#', strippedLogs)
//			.pattern("###")
//			.pattern("   ")
//			.pattern("###")
//			.group("shelf")
//			.unlockedBy(getHasName(strippedLogs), this.has(strippedLogs))
//			.save(this.output);
//	}
//

//	public RecipeBuilder slabBuilder(final RecipeCategory category, final ItemLike result, final Ingredient base) {
//		return this.shaped(category, result, 6).define('#', base).pattern("###");
//	}
//

//	public RecipeBuilder stairBuilder(final ItemLike result, final Ingredient base) {
//		return this.shaped(RecipeCategory.BUILDING_BLOCKS, result, 4).define('#', base).pattern("#  ").pattern("## ").pattern("###");
//	}
//

//	public RecipeBuilder trapdoorBuilder(final ItemLike result, final Ingredient base) {
//		return this.shaped(RecipeCategory.REDSTONE, result, 2).define('#', base).pattern("###").pattern("###");
//	}
//

//	public final RecipeBuilder signBuilder(final ItemLike result, final Ingredient planks) {
//		return this.shaped(RecipeCategory.DECORATIONS, result, 3)
//			.group("sign")
//			.define('#', planks)
//			.define('X', Items.STICK)
//			.pattern("###")
//			.pattern("###")
//			.pattern(" X ");
//	}
//

//	public RecipeBuilder hangingSignBuilder(final ItemLike result, final Ingredient ingredient) {
//		return this.shaped(RecipeCategory.DECORATIONS, result, 6)
//			.group("hanging_sign")
//			.define('#', ingredient)
//			.define('X', Items.IRON_CHAIN)
//			.pattern("X X")
//			.pattern("###")
//			.pattern("###");
//	}
//

//	public void colorItemWithDye(final List<Item> dyes, final List<Item> items, final String groupName, final RecipeCategory category) {
//		this.colorWithDye(dyes, items, null, groupName, category);
//	}
//

//	public void colorWithDye(
//		final List<Item> dyes, final List<Item> dyedItems, final @Nullable Item uncoloredItem, final String groupName, final RecipeCategory category
//	) {
//		for (int dyeIndex = 0; dyeIndex < dyes.size(); dyeIndex++) {
//			Item dye = dyes.get(dyeIndex);
//			Item dyedItem = dyedItems.get(dyeIndex);
//			Stream<Item> sourceItems = dyedItems.stream().filter(b -> !b.equals(dyedItem));
//			if (uncoloredItem != null) {
//				sourceItems = Stream.concat(sourceItems, Stream.of(uncoloredItem));
//			}
//
//			this.shapeless(category, dyedItem)
//				.requires(dye)
//				.requires(Ingredient.of(sourceItems))
//				.group(groupName)
//				.unlockedBy("has_needed_dye", this.has(dye))
//				.save(this.output, "dye_" + getItemName(dyedItem));
//		}
//	}
//

//	public void carpet(final ItemLike result, final ItemLike sourceItem) {
//		this.shaped(RecipeCategory.DECORATIONS, result, 3)
//			.define('#', sourceItem)
//			.pattern("##")
//			.group("carpet")
//			.unlockedBy(getHasName(sourceItem), this.has(sourceItem))
//			.save(this.output);
//	}
//

//	public void bedFromPlanksAndWool(final ItemLike result, final ItemLike wool) {
//		this.shaped(RecipeCategory.DECORATIONS, result)
//			.define('#', wool)
//			.define('X', ItemTags.PLANKS)
//			.pattern("###")
//			.pattern("XXX")
//			.group("bed")
//			.unlockedBy(getHasName(wool), this.has(wool))
//			.save(this.output);
//	}
//

//	public void banner(final ItemLike result, final ItemLike wool) {
//		this.shaped(RecipeCategory.DECORATIONS, result)
//			.define('#', wool)
//			.define('|', Items.STICK)
//			.pattern("###")
//			.pattern("###")
//			.pattern(" | ")
//			.group("banner")
//			.unlockedBy(getHasName(wool), this.has(wool))
//			.save(this.output);
//		SpecialRecipeBuilder.special(() -> new BannerDuplicateRecipe(Ingredient.of(result), new ItemStackTemplate(result.asItem())))
//			.save(this.output, getItemName(result) + "_duplicate");
//	}
//

//	public void stainedGlassFromGlassAndDye(final ItemLike result, final ItemLike dye) {
//		this.shaped(RecipeCategory.BUILDING_BLOCKS, result, 8)
//			.define('#', Blocks.GLASS)
//			.define('X', dye)
//			.pattern("###")
//			.pattern("#X#")
//			.pattern("###")
//			.group("stained_glass")
//			.unlockedBy("has_glass", this.has(Blocks.GLASS))
//			.save(this.output);
//	}
//

//	public void dryGhast(final ItemLike result) {
//		this.shaped(RecipeCategory.BUILDING_BLOCKS, result, 1)
//			.define('#', Items.GHAST_TEAR)
//			.define('X', Items.SOUL_SAND)
//			.pattern("###")
//			.pattern("#X#")
//			.pattern("###")
//			.group("dry_ghast")
//			.unlockedBy(getHasName(Items.GHAST_TEAR), this.has(Items.GHAST_TEAR))
//			.save(this.output);
//	}
//

//	public void harness(final ItemLike result, final ItemLike wool) {
//		this.shaped(RecipeCategory.COMBAT, result)
//			.define('#', wool)
//			.define('G', Items.GLASS)
//			.define('L', Items.LEATHER)
//			.pattern("LLL")
//			.pattern("G#G")
//			.group("harness")
//			.unlockedBy("has_dried_ghast", this.has(Blocks.DRIED_GHAST))
//			.save(this.output);
//	}
//

//	public void stainedGlassPaneFromStainedGlass(final ItemLike result, final ItemLike stainedGlass) {
//		this.shaped(RecipeCategory.DECORATIONS, result, 16)
//			.define('#', stainedGlass)
//			.pattern("###")
//			.pattern("###")
//			.group("stained_glass_pane")
//			.unlockedBy("has_glass", this.has(stainedGlass))
//			.save(this.output);
//	}
//

//	public void stainedGlassPaneFromGlassPaneAndDye(final ItemLike result, final ItemLike dye) {
//		this.shaped(RecipeCategory.DECORATIONS, result, 8)
//			.define('#', Blocks.GLASS_PANE)
//			.define('$', dye)
//			.pattern("###")
//			.pattern("#$#")
//			.pattern("###")
//			.group("stained_glass_pane")
//			.unlockedBy("has_glass_pane", this.has(Blocks.GLASS_PANE))
//			.unlockedBy(getHasName(dye), this.has(dye))
//			.save(this.output, getConversionRecipeName(result, Blocks.GLASS_PANE));
//	}
//

//	public void coloredTerracottaFromTerracottaAndDye(final ItemLike result, final ItemLike dye) {
//		this.shaped(RecipeCategory.BUILDING_BLOCKS, result, 8)
//			.define('#', Blocks.TERRACOTTA)
//			.define('X', dye)
//			.pattern("###")
//			.pattern("#X#")
//			.pattern("###")
//			.group("stained_terracotta")
//			.unlockedBy("has_terracotta", this.has(Blocks.TERRACOTTA))
//			.save(this.output);
//	}
//

//	public void concretePowder(final ItemLike result, final ItemLike dye) {
//		this.shapeless(RecipeCategory.BUILDING_BLOCKS, result, 8)
//			.requires(dye)
//			.requires(Blocks.SAND, 4)
//			.requires(Blocks.GRAVEL, 4)
//			.group("concrete_powder")
//			.unlockedBy("has_sand", this.has(Blocks.SAND))
//			.unlockedBy("has_gravel", this.has(Blocks.GRAVEL))
//			.save(this.output);
//	}
//

//	public void candle(final ItemLike result, final ItemLike dye) {
//		this.shapeless(RecipeCategory.DECORATIONS, result)
//			.requires(Blocks.CANDLE)
//			.requires(dye)
//			.group("dyed_candle")
//			.unlockedBy(getHasName(dye), this.has(dye))
//			.save(this.output);
//	}
//

//	public void wall(final RecipeCategory category, final ItemLike result, final ItemLike base) {
//		this.wallBuilder(category, result, Ingredient.of(base)).unlockedBy(getHasName(base), this.has(base)).save(this.output);
//	}
//

//	public final RecipeBuilder wallBuilder(final RecipeCategory category, final ItemLike result, final Ingredient base) {
//		return this.shaped(category, result, 6).define('#', base).pattern("###").pattern("###");
//	}
//

//	public final RecipeBuilder bricksBuilder(final RecipeCategory category, final ItemLike result, final Ingredient base) {
//		return this.shaped(category, result, 4).define('#', base).pattern("##").pattern("##");
//	}
//

//	public final RecipeBuilder tilesBuilder(final RecipeCategory category, final ItemLike result, final Ingredient base) {
//		return this.shaped(category, result, 4).define('#', base).pattern("##").pattern("##");
//	}
//

//	public final RecipeBuilder pillarBuilder(final RecipeCategory category, final ItemLike result, final Ingredient base) {
//		return this.shaped(category, result, 2).define('#', base).pattern("#").pattern("#");
//	}
//

//	public void polished(final RecipeCategory category, final ItemLike result, final ItemLike base) {
//		this.polishedBuilder(category, result, Ingredient.of(base)).unlockedBy(getHasName(base), this.has(base)).save(this.output);
//	}
//

//	public final RecipeBuilder polishedBuilder(final RecipeCategory category, final ItemLike result, final Ingredient base) {
//		return this.shaped(category, result, 4).define('S', base).pattern("SS").pattern("SS");
//	}
//

//	public void cut(final RecipeCategory category, final ItemLike result, final ItemLike base) {
//		this.cutBuilder(category, result, Ingredient.of(base)).unlockedBy(getHasName(base), this.has(base)).save(this.output);
//	}
//

//	public final ShapedRecipeBuilder cutBuilder(final RecipeCategory category, final ItemLike result, final Ingredient base) {
//		return this.shaped(category, result, 4).define('#', base).pattern("##").pattern("##");
//	}
//

//	public void chiseled(final RecipeCategory category, final ItemLike result, final ItemLike base) {
//		this.chiseledBuilder(category, result, Ingredient.of(base)).unlockedBy(getHasName(base), this.has(base)).save(this.output);
//	}
//

//	public void mosaicBuilder(final RecipeCategory category, final ItemLike result, final ItemLike base) {
//		this.shaped(category, result).define('#', base).pattern("#").pattern("#").unlockedBy(getHasName(base), this.has(base)).save(this.output);
//	}
//

//	public ShapedRecipeBuilder chiseledBuilder(final RecipeCategory category, final ItemLike result, final Ingredient base) {
//		return this.shaped(category, result).define('#', base).pattern("#").pattern("#");
//	}
//

//	public ShapedRecipeBuilder carpetBuilder(final RecipeCategory category, final ItemLike result, final Ingredient base) {
//		return this.shaped(category, result, 3).define('#', base).pattern("##").group("carpet");
//	}
//

//	public void stonecutterResultFromBase(final RecipeCategory category, final ItemLike result, final ItemLike base) {
//		this.stonecutterResultFromBase(category, result, base, 1);
//	}
//

//	public void stonecutterResultFromBase(final RecipeCategory category, final ItemLike result, final ItemLike base, final int count) {
//		SingleItemRecipeBuilder.stonecutting(Ingredient.of(base), category, result, count)
//			.unlockedBy(getHasName(base), this.has(base))
//			.save(this.output, getConversionRecipeName(result, base) + "_stonecutting");
//	}
//

//	public final void smeltingResultFromBase(final ItemLike result, final ItemLike base) {
//		SimpleCookingRecipeBuilder.smelting(Ingredient.of(base), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS, result, 0.1F, 200)
//			.unlockedBy(getHasName(base), this.has(base))
//			.save(this.output);
//	}
//

//	public void nineBlockStorageRecipes(
//		final RecipeCategory unpackedFormCategory, final ItemLike unpackedForm, final RecipeCategory packedFormCategory, final ItemLike packedForm
//	) {
//		this.nineBlockStorageRecipes(
//			unpackedFormCategory, unpackedForm, packedFormCategory, packedForm, getSimpleRecipeName(packedForm), null, getSimpleRecipeName(unpackedForm), null
//		);
//	}
//

//	public void nineBlockStorageRecipesWithCustomPacking(
//		final RecipeCategory unpackedFormCategory,
//		final ItemLike unpackedForm,
//		final RecipeCategory packedFormCategory,
//		final ItemLike packedForm,
//		final String packingRecipeId,
//		final String packingRecipeGroup
//	) {
//		this.nineBlockStorageRecipes(
//			unpackedFormCategory, unpackedForm, packedFormCategory, packedForm, packingRecipeId, packingRecipeGroup, getSimpleRecipeName(unpackedForm), null
//		);
//	}
//

//	public void nineBlockStorageRecipesRecipesWithCustomUnpacking(
//		final RecipeCategory unpackedFormCategory,
//		final ItemLike unpackedForm,
//		final RecipeCategory packedFormCategory,
//		final ItemLike packedForm,
//		final String unpackingRecipeId,
//		final String unpackingRecipeGroup
//	) {
//		this.nineBlockStorageRecipes(
//			unpackedFormCategory, unpackedForm, packedFormCategory, packedForm, getSimpleRecipeName(packedForm), null, unpackingRecipeId, unpackingRecipeGroup
//		);
//	}
//

//	public final void nineBlockStorageRecipes(
//		final RecipeCategory unpackedFormCategory,
//		final ItemLike unpackedForm,
//		final RecipeCategory packedFormCategory,
//		final ItemLike packedForm,
//		final String packingRecipeId,
//		final @Nullable String packingRecipeGroup,
//		final String unpackingRecipeId,
//		final @Nullable String unpackingRecipeGroup
//	) {
//		this.shapeless(unpackedFormCategory, unpackedForm, 9)
//			.requires(packedForm)
//			.group(unpackingRecipeGroup)
//			.unlockedBy(getHasName(packedForm), this.has(packedForm))
//			.save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.parse(unpackingRecipeId)));
//		this.shaped(packedFormCategory, packedForm)
//			.define('#', unpackedForm)
//			.pattern("###")
//			.pattern("###")
//			.pattern("###")
//			.group(packingRecipeGroup)
//			.unlockedBy(getHasName(unpackedForm), this.has(unpackedForm))
//			.save(this.output, ResourceKey.create(Registries.RECIPE, Identifier.parse(packingRecipeId)));
//	}
//

//	public void copySmithingTemplate(final ItemLike smithingTemplate, final ItemLike baseMaterial) {
//		this.shaped(RecipeCategory.MISC, smithingTemplate, 2)
//			.define('#', Items.DIAMOND)
//			.define('C', baseMaterial)
//			.define('S', smithingTemplate)
//			.pattern("#S#")
//			.pattern("#C#")
//			.pattern("###")
//			.unlockedBy(getHasName(smithingTemplate), this.has(smithingTemplate))
//			.save(this.output);
//	}
//

//	public void copySmithingTemplate(final ItemLike smithingTemplate, final Ingredient baseMaterials) {
//		this.shaped(RecipeCategory.MISC, smithingTemplate, 2)
//			.define('#', Items.DIAMOND)
//			.define('C', baseMaterials)
//			.define('S', smithingTemplate)
//			.pattern("#S#")
//			.pattern("#C#")
//			.pattern("###")
//			.unlockedBy(getHasName(smithingTemplate), this.has(smithingTemplate))
//			.save(this.output);
//	}
//

//	public <T extends AbstractCookingRecipe> void cookRecipes(final String source, final AbstractCookingRecipe.Factory<T> factory, final int cookingTime) {
//		this.simpleCookingRecipe(source, factory, cookingTime, Items.BEEF, Items.COOKED_BEEF, 0.35F);
//		this.simpleCookingRecipe(source, factory, cookingTime, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
//		this.simpleCookingRecipe(source, factory, cookingTime, Items.COD, Items.COOKED_COD, 0.35F);
//		this.simpleCookingRecipe(source, factory, cookingTime, Items.KELP, Items.DRIED_KELP, 0.1F);
//		this.simpleCookingRecipe(source, factory, cookingTime, Items.SALMON, Items.COOKED_SALMON, 0.35F);
//		this.simpleCookingRecipe(source, factory, cookingTime, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
//		this.simpleCookingRecipe(source, factory, cookingTime, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
//		this.simpleCookingRecipe(source, factory, cookingTime, Items.POTATO, Items.BAKED_POTATO, 0.35F);
//		this.simpleCookingRecipe(source, factory, cookingTime, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
//	}
//

//	public final <T extends AbstractCookingRecipe> void simpleCookingRecipe(
//		final String source,
//		final AbstractCookingRecipe.Factory<T> factory,
//		final int cookingTime,
//		final ItemLike base,
//		final ItemLike result,
//		final float experience
//	) {
//		SimpleCookingRecipeBuilder.generic(Ingredient.of(base), RecipeCategory.FOOD, CookingBookCategory.FOOD, result, experience, cookingTime, factory)
//			.unlockedBy(getHasName(base), this.has(base))
//			.save(this.output, getItemName(result) + "_from_" + source);
//	}
//

//	public void waxRecipes(final FeatureFlagSet flagSet) {
//		HoneycombItem.WAXABLES
//			.get()
//			.forEach(
//				(block, waxedBlock) -> {
//					if (waxedBlock.requiredFeatures().isSubsetOf(flagSet)) {
//						Pair<RecipeCategory, String> pair = HoneycombItem.WAXED_RECIPES
//							.getOrDefault(waxedBlock, Pair.of(RecipeCategory.BUILDING_BLOCKS, getItemName(waxedBlock)));
//						RecipeCategory recipeCategory = pair.getFirst();
//						String group = pair.getSecond();
//						this.shapeless(recipeCategory, waxedBlock)
//							.requires(block)
//							.requires(Items.HONEYCOMB)
//							.group(group)
//							.unlockedBy(getHasName(block), this.has(block))
//							.save(this.output, getConversionRecipeName(waxedBlock, Items.HONEYCOMB));
//					}
//				}
//			);
//	}
//

//	public void grate(final Block grateBlock, final Block material) {
//		this.shaped(RecipeCategory.BUILDING_BLOCKS, grateBlock, 4)
//			.define('M', material)
//			.pattern(" M ")
//			.pattern("M M")
//			.pattern(" M ")
//			.group(getItemName(grateBlock))
//			.unlockedBy(getHasName(material), this.has(material))
//			.save(this.output);
//	}
//

//	public void copperBulb(final Block copperBulb, final Block copperMaterial) {
//		this.shaped(RecipeCategory.REDSTONE, copperBulb, 4)
//			.define('C', copperMaterial)
//			.define('R', Items.REDSTONE)
//			.define('B', Items.BLAZE_ROD)
//			.pattern(" C ")
//			.pattern("CBC")
//			.pattern(" R ")
//			.unlockedBy(getHasName(copperMaterial), this.has(copperMaterial))
//			.group(getItemName(copperBulb))
//			.save(this.output);
//	}
//

//	public void waxedChiseled(final Block result, final Block material) {
//		this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
//			.define('M', material)
//			.pattern(" M ")
//			.pattern(" M ")
//			.group(getItemName(result))
//			.unlockedBy(getHasName(material), this.has(material))
//			.save(this.output);
//	}
//

//	public void suspiciousStew(final Item item, final SuspiciousEffectHolder effectHolder) {
//		ItemStackTemplate stew = new ItemStackTemplate(
//			Items.SUSPICIOUS_STEW, DataComponentPatch.builder().set(DataComponents.SUSPICIOUS_STEW_EFFECTS, effectHolder.getSuspiciousEffects()).build()
//		);
//		this.shapeless(RecipeCategory.FOOD, stew)
//			.requires(Items.BOWL)
//			.requires(ItemTags.MUSHROOMS)
//			.requires(ItemTags.MUSHROOMS)
//			.requires(item)
//			.group("suspicious_stew")
//			.unlockedBy(getHasName(item), this.has(item))
//			.save(this.output, getItemName(stew.item().value()) + "_from_" + getItemName(item));
//	}
//

//	public void dyedItem(final Item target, final String group) {
//		CustomCraftingRecipeBuilder.customCrafting(
//				RecipeCategory.MISC,
//				(commonInfo, bookInfo) -> new DyeRecipe(commonInfo, bookInfo, Ingredient.of(target), this.tag(ItemTags.DYES), new ItemStackTemplate(target))
//			)
//			.unlockedBy(getHasName(target), this.has(target))
//			.group(group)
//			.save(this.output, getItemName(target) + "_dyed");
//	}
//

//	public void dyedShulkerBoxRecipe(final Item dye, final Item dyedResult) {
//		TransmuteRecipeBuilder.transmute(RecipeCategory.DECORATIONS, this.tag(ItemTags.SHULKER_BOXES), Ingredient.of(dye), dyedResult)
//			.group("shulker_box_dye")
//			.unlockedBy("has_shulker_box", this.has(ItemTags.SHULKER_BOXES))
//			.save(this.output);
//	}
//

//	public void dyedBundleRecipe(final Item dye, final Item dyedResult) {
//		TransmuteRecipeBuilder.transmute(RecipeCategory.TOOLS, this.tag(ItemTags.BUNDLES), Ingredient.of(dye), dyedResult)
//			.group("bundle_dye")
//			.unlockedBy(getHasName(dye), this.has(dye))
//			.save(this.output);
//	}
//

//	public void cushionRecipe(final Item woolSlab, final Item result) {
//		this.shaped(RecipeCategory.DECORATIONS, result, 1)
//			.define('#', woolSlab)
//			.group("cushion")
//			.unlockedBy(getHasName(woolSlab), this.has(woolSlab))
//			.pattern("###")
//			.save(this.output);
//	}
//

//	public void generateRecipes(final BlockFamily family, final FeatureFlagSet flagSet) {
//		family.getVariants().forEach((variant, result) -> {
//			if (result.requiredFeatures().isSubsetOf(flagSet)) {
//				if (family.shouldGenerateCraftingRecipe()) {
//					this.generateCraftingRecipe(family, variant, result, this.getBaseBlockForCrafting(family, variant));
//				}
//
//				if (family.shouldGenerateSmeltingRecipe()) {
//					this.generateSmeltingRecipe(variant, result, this.getBaseBlockForCrafting(family, variant));
//				}
//
//				if (family.shouldGenerateStonecutterRecipe()) {
//					this.generateStonecutterRecipe(family, variant, family.getBaseBlock());
//				}
//			}
//		});
//	}
//

//	public final void generateCraftingRecipe(final BlockFamily family, final BlockFamily.Variant variant, final Block result, final ItemLike base) {
//		RecipeProvider.FamilyCraftingRecipeProvider recipeFunction = SHAPE_BUILDERS.get(variant);
//		if (recipeFunction != null) {
//			RecipeBuilder builder = recipeFunction.create(this, result, base);
//			family.getRecipeGroupPrefix().ifPresent(prefix -> builder.group(variant.getPrefixedRecipeGroup(prefix)));
//			builder.unlockedBy(this.getCraftingCriterionName(family, variant, base), this.has(base));
//			builder.save(this.output);
//		}
//	}
//

//	public final void generateSmeltingRecipe(final BlockFamily.Variant variant, final Block result, final ItemLike base) {
//		if (variant == BlockFamily.Variant.CRACKED) {
//			this.smeltingResultFromBase(result, base);
//		}
//
//		if (variant == BlockFamily.Variant.COBBLED) {
//			this.smeltingResultFromBase(base, result);
//		}
//	}
//

//	public final void generateStonecutterRecipe(final BlockFamily family, final BlockFamily.Variant variant, final Block base) {
//		RecipeProvider.FamilyStonecutterRecipeProvider recipeFunction = STONECUTTER_RECIPE_BUILDERS.get(variant);
//		if (recipeFunction != null) {
//			recipeFunction.create(this, family.get(variant), base);
//		}
//
//		if (variant == BlockFamily.Variant.POLISHED
//			|| variant == BlockFamily.Variant.CUT
//			|| variant == BlockFamily.Variant.BRICKS
//			|| variant == BlockFamily.Variant.TILES
//			|| variant == BlockFamily.Variant.PILLAR
//			|| variant == BlockFamily.Variant.COBBLED) {
//			BlockFamily childVariantFamily = BlockFamilies.getFamily(family.get(variant));
//			if (childVariantFamily != null) {
//				childVariantFamily.getVariants().forEach((childVariant, r) -> this.generateStonecutterRecipe(childVariantFamily, childVariant, base));
//			}
//		}
//	}
//

//	public final Block getBaseBlockForCrafting(final BlockFamily family, final BlockFamily.Variant variant) {
//		BlockFamily.Variant baseVariant = variant.getBaseVariantForCrafting();
//		if (baseVariant != null) {
//			if (!family.getVariants().containsKey(baseVariant)) {
//				throw new IllegalStateException(baseVariant.name() + " is not defined for the family.");
//			} else {
//				return family.get(baseVariant);
//			}
//		} else {
//			return family.getBaseBlock();
//		}
//	}
//

//	public final String getCraftingCriterionName(final BlockFamily family, final BlockFamily.Variant variant, final ItemLike base) {
//		return base != family.getBaseBlock()
//			? "has_" + variant.getBaseVariantForCrafting().getRecipeGroup()
//			: family.getRecipeUnlockedBy().orElseGet(() -> getHasName(base));
//	}
//

//	public static Criterion<EnterBlockTrigger.TriggerInstance> insideOf(final Block block) {
//		return CriteriaTriggers.ENTER_BLOCK
//			.createCriterion(new EnterBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(HolderSet.direct(block.builtInRegistryHolder())), Optional.empty()));
//	}
//

//	public Criterion<BredAnimalsTrigger.TriggerInstance> bredAnimal() {
//		return CriteriaTriggers.BRED_ANIMALS
//			.createCriterion(new BredAnimalsTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
//	}
//

//	public final Criterion<InventoryChangeTrigger.TriggerInstance> has(final MinMaxBounds.Ints count, final ItemLike item) {
//		return inventoryTrigger(ItemPredicate.Builder.item().of(this.items, item).withCount(count));
//	}
//

//	public Criterion<InventoryChangeTrigger.TriggerInstance> has(final ItemLike item) {
//		return inventoryTrigger(ItemPredicate.Builder.item().of(this.items, item));
//	}
//

//	public Criterion<InventoryChangeTrigger.TriggerInstance> has(final TagKey<Item> tag) {
//		return inventoryTrigger(ItemPredicate.Builder.item().of(this.items, tag));
//	}
//

//	public static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(final ItemPredicate.Builder... predicates) {
//		return inventoryTrigger(Arrays.stream(predicates).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
//	}
//

//	public static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(final ItemPredicate... predicates) {
//		return CriteriaTriggers.INVENTORY_CHANGED
//			.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(predicates)));
//	}
//

    public static String getHasName(final ItemLike baseBlock) {
        return "has_" + getItemName(baseBlock);
    }


    public static String getItemName(final ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
    }


    public static String getSimpleRecipeName(final ItemLike itemLike) {
        return getItemName(itemLike);
    }


    public static String getConversionRecipeName(final ItemLike product, final ItemLike material) {
        return getItemName(product) + "_from_" + getItemName(material);
    }


    public static String getSmeltingRecipeName(final ItemLike product) {
        return getItemName(product) + "_from_smelting";
    }


    public static String getBlastingRecipeName(final ItemLike product) {
        return getItemName(product) + "_from_blasting";
    }


    public Ingredient tag(final TagKey<Item> id) {
        return Ingredient.of(this.items.getOrThrow(id));
    }

    @NotVanilla
    public ShapedRecipeBuilder shaped(final RecipeCategory category, final Object item) {
        return ShapedRecipeBuilder.shaped(this.items, category, ItemLike.cast(item));
    }

    @NotVanilla
    public ShapedRecipeBuilder shaped(final RecipeCategory category, final Object item, final int count) {
        return ShapedRecipeBuilder.shaped(this.items, category, ItemLike.cast(item), count);
    }

    public ShapelessRecipeBuilder shapeless(final RecipeCategory category, final ItemStackTemplate result) {
        return ShapelessRecipeBuilder.shapeless(this.items, category, result);
    }

    @NotVanilla
    public ShapelessRecipeBuilder shapeless(final RecipeCategory category, final Object item) {
        return ShapelessRecipeBuilder.shapeless(this.items, category, ItemLike.cast(item));
    }

    @NotVanilla
    public ShapelessRecipeBuilder shapeless(final RecipeCategory category, final Object item, final int count) {
        return ShapelessRecipeBuilder.shapeless(this.items, category, ItemLike.cast(item), count);
    }
//
//	@FunctionalInterface
//	private interface FamilyCraftingRecipeProvider {
//		RecipeBuilder create(RecipeProvider context, ItemLike result, ItemLike base);
//	}
//
//	@FunctionalInterface
//	private interface FamilyStonecutterRecipeProvider {
//		void create(RecipeProvider context, ItemLike result, ItemLike base);
//	}
}
