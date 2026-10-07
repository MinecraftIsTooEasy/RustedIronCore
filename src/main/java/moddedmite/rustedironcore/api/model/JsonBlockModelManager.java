package moddedmite.rustedironcore.api.model;

import huix.glacier.util.Identifier;
import moddedmite.rustedironcore.api.util.LogUtil;
import moddedmite.rustedironcore.mixin.accessor.BlockAccessor;
import moddedmite.rustedironcore.mixin.accessor.ItemAccessor;
import net.minecraft.Block;
import net.minecraft.EntityLivingBase;
import net.minecraft.Icon;
import net.minecraft.IBlockAccess;
import net.minecraft.Item;
import net.minecraft.ItemBlock;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;
import net.minecraft.RenderBlocks;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.ResourceManagerReloadListener;
import net.minecraft.TextureMap;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class JsonBlockModelManager implements ResourceManagerReloadListener, JsonModelRegistry {

    public static final JsonBlockModelManager INSTANCE = new JsonBlockModelManager();

    private static final Logger LOGGER = LogUtil.getLogger();
    private static final int BLOCK_TEXTURE_MAP = 0;
    private static final int ITEM_TEXTURE_MAP = 1;

    private final Map<Identifier, Optional<JsonBlockState>> blockStateCache = new HashMap<>();
    private final Map<Identifier, Optional<JsonBlockModel>> modelCache = new HashMap<>();
    private final Map<Block, Identifier> blockModelOverrides = new IdentityHashMap<>();
    private final Map<Block, Identifier> blockStateOverrides = new IdentityHashMap<>();
    private final Map<Integer, Identifier> blockMetadataModelOverrides = new HashMap<>();
    private final Map<Integer, Identifier> blockMetadataStateOverrides = new HashMap<>();
    private final Map<Block, JsonBlockStatePropertyResolver> blockStatePropertyResolvers = new IdentityHashMap<>();
    private final Map<Item, Identifier> itemModelOverrides = new IdentityHashMap<>();
    private final Map<Long, Identifier> itemMetadataModelOverrides = new HashMap<>();
    private final Map<Block, Identifier> blockItemModelOverrides = new IdentityHashMap<>();
    private final Map<Integer, Identifier> blockItemMetadataModelOverrides = new HashMap<>();
    private final Map<Item, Map<Identifier, JsonItemModelPredicate>> itemModelPredicates = new IdentityHashMap<>();
    private final Set<Block> jsonBlocks = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<Item, BuiltInEntityRenderer> builtInRenderers = new IdentityHashMap<>();
    private final Map<Long, BuiltInEntityRenderer> builtInRenderersByMetadata = new HashMap<>();
    private final Map<Block, BuiltInEntityRenderer> builtInRenderersByBlock = new IdentityHashMap<>();

    private JsonBlockModelManager() {
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.blockStateCache.clear();
        this.modelCache.clear();
    }

    public void registerBlockModel(Block block, String modelId) {
        this.registerBlockModel(block, new Identifier(modelId));
    }

    public void registerBlockModel(Block block, Identifier modelId) {
        if (block != null && modelId != null) {
            this.blockModelOverrides.put(block, normalizeModelId(modelId, "block"));
            this.jsonBlocks.add(block);
        }
    }

    public void registerBlockModel(Block block, int metadata, String modelId) {
        this.registerBlockModel(block, metadata, new Identifier(modelId));
    }

    public void registerBlockModel(Block block, int metadata, Identifier modelId) {
        if (block != null && modelId != null) {
            this.blockMetadataModelOverrides.put(blockMetadataKey(block, metadata), normalizeModelId(modelId, "block"));
            this.jsonBlocks.add(block);
        }
    }

    public void registerBlockState(Block block, String blockStateId) {
        this.registerBlockState(block, new Identifier(blockStateId));
    }

    public void registerBlockState(Block block, Identifier blockStateId) {
        if (block != null && blockStateId != null) {
            this.blockStateOverrides.put(block, normalizeResourceId(blockStateId, "blockstates"));
            this.jsonBlocks.add(block);
        }
    }

    public void registerBlockState(Block block, int metadata, String blockStateId) {
        this.registerBlockState(block, metadata, new Identifier(blockStateId));
    }

    public void registerBlockState(Block block, int metadata, Identifier blockStateId) {
        if (block != null && blockStateId != null) {
            this.blockMetadataStateOverrides.put(blockMetadataKey(block, metadata), normalizeResourceId(blockStateId, "blockstates"));
            this.jsonBlocks.add(block);
        }
    }

    public void registerBlockStateProperties(Block block, JsonBlockStatePropertyResolver resolver) {
        if (block != null && resolver != null) {
            this.blockStatePropertyResolvers.put(block, resolver);
            this.jsonBlocks.add(block);
        }
    }

    public void registerItemModel(Item item, String modelId) {
        this.registerItemModel(item, new Identifier(modelId));
    }

    public void registerItemModel(Item item, Identifier modelId) {
        if (item != null && modelId != null) {
            this.itemModelOverrides.put(item, normalizeModelId(modelId, "item"));
            if (item instanceof ItemBlock) {
                this.blockItemModelOverrides.put(((ItemBlock) item).getBlock(), normalizeModelId(modelId, "item"));
            }
        }
    }

    public void registerItemModel(Block block, String modelId) {
        this.registerItemModel(block, new Identifier(modelId));
    }

    public void registerItemModel(Block block, Identifier modelId) {
        if (block != null && modelId != null) {
            this.blockItemModelOverrides.put(block, normalizeModelId(modelId, "item"));
            Item item = Item.itemsList[block.blockID];
            if (item != null) {
                this.itemModelOverrides.put(item, normalizeModelId(modelId, "item"));
            }
        }
    }

    public void registerItemModel(Item item, int metadata, String modelId) {
        this.registerItemModel(item, metadata, new Identifier(modelId));
    }

    public void registerItemModel(Item item, int metadata, Identifier modelId) {
        if (item != null && modelId != null) {
            this.itemMetadataModelOverrides.put(itemMetadataKey(item, metadata), normalizeModelId(modelId, "item"));
            if (item instanceof ItemBlock) {
                this.blockItemMetadataModelOverrides.put(blockMetadataKey(((ItemBlock) item).getBlock(), metadata), normalizeModelId(modelId, "item"));
            }
        }
    }

    public void registerItemModel(Block block, int metadata, String modelId) {
        this.registerItemModel(block, metadata, new Identifier(modelId));
    }

    public void registerItemModel(Block block, int metadata, Identifier modelId) {
        if (block != null && modelId != null) {
            this.blockItemMetadataModelOverrides.put(blockMetadataKey(block, metadata), normalizeModelId(modelId, "item"));
            Item item = Item.itemsList[block.blockID];
            if (item != null) {
                this.itemMetadataModelOverrides.put(itemMetadataKey(item, metadata), normalizeModelId(modelId, "item"));
            }
        }
    }

    @Override
    public void registerItemModelPredicate(Item item, String property, JsonItemModelPredicate predicate) {
        this.registerItemModelPredicate(item, new Identifier(property), predicate);
    }

    @Override
    public void registerItemModelPredicate(Item item, Identifier property, JsonItemModelPredicate predicate) {
        if (item != null && property != null && predicate != null) {
            this.itemModelPredicates.computeIfAbsent(item, ignored -> new HashMap<>()).put(property, predicate);
        }
    }

    @Override
    public void registerItemModelPredicate(Block block, String property, JsonItemModelPredicate predicate) {
        this.registerItemModelPredicate(block, new Identifier(property), predicate);
    }

    @Override
    public void registerItemModelPredicate(Block block, Identifier property, JsonItemModelPredicate predicate) {
        if (block != null) {
            this.registerItemModelPredicate(Item.itemsList[block.blockID], property, predicate);
        }
    }

    public void registerBuiltInRenderer(Item item, BuiltInEntityRenderer renderer) {
        if (item != null && renderer != null) {
            this.builtInRenderers.put(item, renderer);
            if (item instanceof ItemBlock) {
                this.builtInRenderersByBlock.put(((ItemBlock) item).getBlock(), renderer);
            }
        }
    }

    public void registerBuiltInRenderer(Item item, int metadata, BuiltInEntityRenderer renderer) {
        if (item != null && renderer != null) {
            this.builtInRenderersByMetadata.put(itemMetadataKey(item, metadata), renderer);
        }
    }

    public void registerBuiltInRenderer(Block block, BuiltInEntityRenderer renderer) {
        if (block != null && renderer != null) {
            this.builtInRenderersByBlock.put(block, renderer);
        }
    }

    private BuiltInEntityRenderer getBuiltInRenderer(ItemStack stack) {
        if (stack == null) return null;
        Item item = stack.getItem();
        BuiltInEntityRenderer renderer = this.builtInRenderersByMetadata.get(itemMetadataKey(item, stack.getItemSubtype()));
        if (renderer != null) return renderer;
        renderer = this.builtInRenderers.get(item);
        if (renderer != null) return renderer;
        if (stack.isBlock()) {
            Block block = Block.blocksList[stack.itemID];
            if (block != null) {
                return this.builtInRenderersByBlock.get(block);
            }
        }
        return null;
    }

    public boolean renderBlock(RenderBlocks renderBlocks, Block block, int x, int y, int z, int metadata) {
        List<BlockModelSelection> selections = this.getBlockModelSelections(block, renderBlocks.blockAccess, x, y, z, metadata);
        if (selections.isEmpty()) {
            return false;
        }

        int color = block.colorMultiplier(renderBlocks.blockAccess, x, y, z);
        float red = ((color >> 16) & 255) / 255.0F;
        float green = ((color >> 8) & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        boolean rendered = false;
        for (BlockModelSelection selection : selections) {
            if (!selection.model().getElements().isEmpty()) {
                JsonBlockModelRenderer.renderBlock(selection.model(), selection.variant(), renderBlocks, block, x, y, z, metadata, red, green, blue);
                rendered = true;
            }
        }
        if (!rendered) {
            return false;
        }
        return true;
    }

    public boolean renderBlockAsItem(Block block, int metadata, float brightness) {
        return renderBlockAsItem(block, metadata, brightness, JsonModelDisplayContext.BLOCK_ITEM);
    }

    public boolean renderBlockAsItem(Block block, int metadata, float brightness, JsonModelDisplayContext context) {
        return this.renderBlockAsItem(new ItemStack(block, 1, metadata), block, brightness, context, null);
    }

    public boolean renderBlockAsItem(ItemStack stack, Block block, float brightness,
                                     JsonModelDisplayContext context, EntityLivingBase entity) {
        if (stack == null || block == null) {
            return false;
        }
        int metadata = stack.getItemSubtype();

        Optional<JsonBlockModel> itemModel = this.getExplicitItemModel(stack, block)
                .map(model -> this.resolveItemModel(model, stack, entity));
        if (itemModel.isPresent()) {
            JsonBlockModel model = itemModel.get();
            if (model.isBuiltInEntity()) {
                BuiltInEntityRenderer renderer = this.getBuiltInRenderer(stack);
                if (renderer == null) {
                    return false;
                }
                JsonBlockModelRenderer.renderBuiltInEntityAsBlockItem(model, entity, stack, renderer, context);
                return true;
            }
            return renderBlockItemModel(model, stack, block, stack.getItemSubtype(), brightness, context);
        }

        if (!this.jsonBlocks.contains(block)) {
            return false;
        }
        List<BlockModelSelection> selections = this.getBlockModelSelections(block, null, 0, 0, 0, metadata);
        if (selections.isEmpty()) {
            return false;
        }

        boolean rendered = false;
        for (BlockModelSelection selection : selections) {
            JsonBlockModel model = selection.model();
            if (model.isBuiltInEntity()) {
                BuiltInEntityRenderer renderer = this.getBuiltInRenderer(stack);
                if (renderer != null) {
                    JsonBlockModelRenderer.renderBuiltInEntityAsBlockItem(model, entity, stack, renderer, context);
                    rendered = true;
                }
            } else if (!model.getElements().isEmpty()) {
                JsonBlockModelRenderer.renderBlockAsItem(selection.model(), selection.variant(), block, metadata, brightness, context);
                rendered = true;
            }
        }
        return rendered;
    }

    /** Draws the registered block-state model in the current item-space matrix without applying JSON display transforms. */
    public boolean renderBlockAsItemGeometry(Block block, int metadata, float brightness) {
        if (block == null || !this.jsonBlocks.contains(block)) {
            return false;
        }
        List<BlockModelSelection> selections = this.getBlockModelSelections(block, null, 0, 0, 0, metadata);
        boolean rendered = false;
        for (BlockModelSelection selection : selections) {
            if (!selection.model().getElements().isEmpty()) {
                JsonBlockModelRenderer.renderBlockAsItemGeometry(
                        selection.model(), selection.variant(), block, metadata, brightness);
                rendered = true;
            }
        }
        return rendered;
    }

    public boolean renderItem(ItemStack stack, Icon fallbackIcon, JsonModelDisplayContext context) {
        return this.renderItem(stack, fallbackIcon, context, null);
    }

    public boolean renderItem(ItemStack stack, Icon fallbackIcon, JsonModelDisplayContext context,
                              EntityLivingBase entity) {
        Optional<JsonBlockModel> model = this.getItemModel(stack, entity);
        if (model.isEmpty()) {
            return false;
        }

        if (model.get().isBuiltInEntity()) {
            BuiltInEntityRenderer renderer = this.getBuiltInRenderer(stack);
            if (renderer == null) {
                return false;
            }
            JsonBlockModelRenderer.renderBuiltInEntity(model.get(), entity, stack, fallbackIcon, context, renderer);
            return true;
        }

        if (model.get().getElements().isEmpty()) {
            return false;
        }

        JsonBlockModelRenderer.renderItem(model.get(), stack, fallbackIcon, context);
        return true;
    }

    public boolean renderItemIntoGui(ItemStack stack, int x, int y, float zLevel) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }

        if (stack.isBlock()) {
            Block block = Block.blocksList[stack.itemID];
            if (block == null) {
                return false;
            }
            Optional<JsonBlockModel> itemModel = this.getExplicitItemModel(stack, block)
                    .map(model -> this.resolveItemModel(model, stack, null));
            if (itemModel.isPresent()) {
                JsonBlockModel model = itemModel.get();
                if (model.isBuiltInEntity()) {
                    BuiltInEntityRenderer renderer = this.getBuiltInRenderer(stack);
                    if (renderer == null) {
                        return false;
                    }
                    JsonBlockModelRenderer.renderBuiltInEntityIntoGui(model, stack, x, y, zLevel, renderer);
                    return true;
                }
                return renderBlockItemModelIntoGui(model, stack, block, x, y, zLevel);
            }
            if (!this.jsonBlocks.contains(block)) {
                return false;
            }
            List<BlockModelSelection> selections = this.getBlockModelSelections(block, null, 0, 0, 0, stack.getItemSubtype());
            if (selections.isEmpty()) {
                return false;
            }
            boolean rendered = false;
            for (BlockModelSelection selection : selections) {
                JsonBlockModel model = selection.model();
                if (model.isBuiltInEntity()) {
                    BuiltInEntityRenderer renderer = this.getBuiltInRenderer(stack);
                    if (renderer != null) {
                        JsonBlockModelRenderer.renderBuiltInEntityIntoGui(model, stack, x, y, zLevel, renderer);
                        rendered = true;
                    }
                } else if (!model.getElements().isEmpty()) {
                    JsonBlockModelRenderer.renderBlockAsItemInGui(selection.model(), selection.variant(), block, stack.getItemSubtype(), x, y, zLevel);
                    rendered = true;
                }
            }
            return rendered;
        }

        Optional<JsonBlockModel> model = this.getItemModel(stack);
        if (model.isEmpty()) {
            return false;
        }
        if (model.get().isBuiltInEntity()) {
            BuiltInEntityRenderer renderer = this.getBuiltInRenderer(stack);
            if (renderer == null) {
                return false;
            }
            JsonBlockModelRenderer.renderBuiltInEntityIntoGui(model.get(), stack, x, y, zLevel, renderer);
            return true;
        }
        if (model.get().getElements().isEmpty()) {
            return false;
        }

        JsonBlockModelRenderer.renderItemIntoGui(model.get(), stack, x, y, zLevel);
        return true;
    }

    public Optional<JsonBlockModel> getModel(Block block, int metadata) {
        Optional<BlockModelSelection> selection = this.getBlockModelSelection(block, metadata);
        return selection.map(BlockModelSelection::model);
    }

    /** Loads a model by its full resource path, such as {@code modid:item/model_name}. */
    public Optional<JsonBlockModel> getModel(Identifier modelId) {
        if (modelId == null) {
            return Optional.empty();
        }
        return this.loadModel(modelId);
    }

    public Optional<JsonBlockModel> getItemModel(ItemStack stack) {
        return this.getItemModel(stack, null);
    }

    public Optional<JsonBlockModel> getItemModel(ItemStack stack, EntityLivingBase entity) {
        return this.getBaseItemModel(stack).map(model -> this.resolveItemModel(model, stack, entity));
    }

    private Optional<JsonBlockModel> getBaseItemModel(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return Optional.empty();
        }

        Item item = stack.getItem();
        Identifier metadataModelId = this.itemMetadataModelOverrides.get(itemMetadataKey(item, stack.getItemSubtype()));
        if (metadataModelId != null) {
            return this.loadModel(metadataModelId);
        }
        Block stackBlock = stack.isBlock() ? Block.blocksList[stack.itemID] : null;
        if (stackBlock != null) {
            metadataModelId = this.blockItemMetadataModelOverrides.get(blockMetadataKey(stackBlock, stack.getItemSubtype()));
            if (metadataModelId != null) {
                return this.loadModel(metadataModelId);
            }
        }

        Identifier modelId = this.itemModelOverrides.get(item);
        if (modelId != null) {
            return this.loadModel(modelId);
        }
        if (stackBlock != null) {
            modelId = this.blockItemModelOverrides.get(stackBlock);
            if (modelId != null) {
                return this.loadModel(modelId);
            }
        }

        String iconString = ((ItemAccessor) item).invokeGetIconString();
        if (iconString == null || iconString.isEmpty()) {
            return Optional.empty();
        }

        Identifier itemTextureId = new Identifier(iconString);
        return this.loadModel(new Identifier(itemTextureId.getNamespace(), "item/" + itemTextureId.getPath()));
    }

    private JsonBlockModel resolveItemModel(JsonBlockModel model, ItemStack stack, EntityLivingBase entity) {
        List<JsonItemModelOverride> overrides = model.getOverrides();
        for (int index = overrides.size() - 1; index >= 0; index--) {
            JsonItemModelOverride override = overrides.get(index);
            if (!this.matchesItemOverride(stack, entity, override)) {
                continue;
            }
            Identifier modelId = normalizeModelId(override.getModelId(), "item");
            if (modelId.equals(model.getModelId())) {
                return model;
            }
            return this.loadModel(modelId).orElse(model);
        }
        return model;
    }

    private boolean matchesItemOverride(ItemStack stack, EntityLivingBase entity,
                                        JsonItemModelOverride override) {
        Map<Identifier, JsonItemModelPredicate> predicates = this.itemModelPredicates.get(stack.getItem());
        if (predicates == null) {
            return false;
        }
        for (Map.Entry<Identifier, Float> entry : override.getPredicates().entrySet()) {
            JsonItemModelPredicate predicate = predicates.get(entry.getKey());
            if (predicate == null || predicate.getValue(stack, entity) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private Optional<JsonBlockModel> getExplicitItemModel(ItemStack stack) {
        return this.getExplicitItemModel(stack, stack != null && stack.isBlock() ? Block.blocksList[stack.itemID] : null);
    }

    private Optional<JsonBlockModel> getExplicitItemModel(ItemStack stack, Block block) {
        if (stack == null || stack.getItem() == null) {
            return Optional.empty();
        }

        Item item = stack.getItem();
        Identifier metadataModelId = this.itemMetadataModelOverrides.get(itemMetadataKey(item, stack.getItemSubtype()));
        if (metadataModelId != null) {
            return this.loadModel(metadataModelId);
        }
        if (block != null) {
            metadataModelId = this.blockItemMetadataModelOverrides.get(blockMetadataKey(block, stack.getItemSubtype()));
            if (metadataModelId != null) {
                return this.loadModel(metadataModelId);
            }
        }

        Identifier modelId = this.itemModelOverrides.get(item);
        if (modelId != null) {
            return this.loadModel(modelId);
        }
        if (block != null) {
            modelId = this.blockItemModelOverrides.get(block);
            if (modelId != null) {
                return this.loadModel(modelId);
            }
        }

        return Optional.empty();
    }

    private static boolean renderBlockItemModel(JsonBlockModel model, ItemStack stack, Block block, int metadata, float brightness, JsonModelDisplayContext context) {
        if (model.isGeneratedItem()) {
            return JsonBlockModelRenderer.renderItem(model, stack, stack.getIconIndex(), context);
        }
        if (model.getElements().isEmpty()) {
            return false;
        }
        return JsonBlockModelRenderer.renderBlockAsItem(model, JsonBlockState.EMPTY_VARIANT, block, metadata, brightness, context);
    }

    private static boolean renderBlockItemModelIntoGui(JsonBlockModel model, ItemStack stack, Block block, int x, int y, float zLevel) {
        if (model.isGeneratedItem()) {
            return JsonBlockModelRenderer.renderItemIntoGui(model, stack, x, y, zLevel);
        }
        if (model.getElements().isEmpty()) {
            return false;
        }
        return JsonBlockModelRenderer.renderBlockAsItemInGui(model, JsonBlockState.EMPTY_VARIANT, block, stack.getItemSubtype(), x, y, zLevel);
    }

    Optional<JsonBlockModel> loadModelById(Identifier modelId) {
        return this.loadModel(modelId);
    }

    public void registerTextureAtlasSprites(TextureMap textureMap) {
        int textureType = textureMap.getTextureType();
        Set<String> iconNames = new HashSet<>();
        if (textureType == BLOCK_TEXTURE_MAP) {
            this.collectBlockTextureIcons(iconNames);
            this.collectItemTextureIcons(iconNames, BLOCK_TEXTURE_MAP);
        } else if (textureType == ITEM_TEXTURE_MAP) {
            this.collectItemTextureIcons(iconNames, ITEM_TEXTURE_MAP);
        }

        for (String iconName : iconNames) {
            textureMap.registerIcon(iconName);
        }
    }

    private void collectBlockTextureIcons(Set<String> iconNames) {
        for (Block block : Block.blocksList) {
            if (block == null) {
                continue;
            }

            Identifier blockTextureId = getBlockTextureId(block);
            for (int metadata = 0; metadata < 16; metadata++) {
                Optional<JsonBlockState> blockState = this.loadBlockState(block, metadata, blockTextureId);
                if (blockState.isPresent()) {
                    collectBlockStateTextureIcons(blockState.get(), iconNames);
                    continue;
                }

                for (BlockModelSelection selection : this.getBlockModelSelections(block, null, 0, 0, 0, metadata)) {
                    collectTextureIcons(selection.model(), BLOCK_TEXTURE_MAP, iconNames);
                }
            }
        }

        for (Identifier modelId : this.blockModelOverrides.values()) {
            this.loadModel(modelId).ifPresent(model -> collectTextureIcons(model, BLOCK_TEXTURE_MAP, iconNames));
        }
        for (Identifier modelId : this.blockMetadataModelOverrides.values()) {
            this.loadModel(modelId).ifPresent(model -> collectTextureIcons(model, BLOCK_TEXTURE_MAP, iconNames));
        }
        for (Identifier blockStateId : this.blockStateOverrides.values()) {
            this.readBlockState(blockStateId).ifPresent(blockState -> collectBlockStateTextureIcons(blockState, iconNames));
        }
        for (Identifier blockStateId : this.blockMetadataStateOverrides.values()) {
            this.readBlockState(blockStateId).ifPresent(blockState -> collectBlockStateTextureIcons(blockState, iconNames));
        }
    }

    private void collectItemTextureIcons(Set<String> iconNames, int textureType) {
        for (Item item : Item.itemsList) {
            if (item == null) {
                continue;
            }

            Optional<JsonBlockModel> model = this.getItemModel(new ItemStack(item));
            model.ifPresent(jsonBlockModel ->
                    collectTextureIcons(jsonBlockModel, textureType, ITEM_TEXTURE_MAP, iconNames));
        }

        for (Identifier modelId : this.itemModelOverrides.values()) {
            this.loadModel(modelId).ifPresent(model ->
                    collectTextureIcons(model, textureType, ITEM_TEXTURE_MAP, iconNames));
        }
        for (Identifier modelId : this.itemMetadataModelOverrides.values()) {
            this.loadModel(modelId).ifPresent(model ->
                    collectTextureIcons(model, textureType, ITEM_TEXTURE_MAP, iconNames));
        }
    }

    private void collectTextureIcons(JsonBlockModel model, int textureType, Set<String> iconNames) {
        this.collectTextureIcons(model, textureType, textureType, iconNames, new HashSet<>());
    }

    private void collectTextureIcons(JsonBlockModel model, int textureType, int defaultTextureType,
                                     Set<String> iconNames) {
        this.collectTextureIcons(model, textureType, defaultTextureType, iconNames, new HashSet<>());
    }

    private void collectTextureIcons(JsonBlockModel model, int textureType, int defaultTextureType,
                                     Set<String> iconNames,
                                     Set<Identifier> visitedModels) {
        if (!visitedModels.add(model.getModelId())) {
            return;
        }
        for (String texture : model.collectResolvedTextures()) {
            if (JsonBlockModelRenderer.getAtlasTextureType(texture, defaultTextureType) != textureType) {
                continue;
            }
            String iconName = JsonBlockModelRenderer.toAtlasIconName(texture, textureType);
            if (iconName != null) {
                iconNames.add(iconName);
            }
        }
        for (JsonItemModelOverride override : model.getOverrides()) {
            Identifier modelId = normalizeModelId(override.getModelId(), "item");
            this.loadModel(modelId).ifPresent(overrideModel ->
                    this.collectTextureIcons(
                            overrideModel, textureType, defaultTextureType, iconNames, visitedModels));
        }
    }

    private void collectBlockStateTextureIcons(JsonBlockState blockState, Set<String> iconNames) {
        for (Identifier modelId : blockState.collectModelIds()) {
            this.loadModel(normalizeModelId(modelId, "block")).ifPresent(model -> collectTextureIcons(model, BLOCK_TEXTURE_MAP, iconNames));
        }
    }

    private Optional<BlockModelSelection> getBlockModelSelection(Block block, int metadata) {
        List<BlockModelSelection> selections = this.getBlockModelSelections(block, null, 0, 0, 0, metadata);
        return selections.isEmpty() ? Optional.empty() : Optional.of(selections.get(0));
    }

    private List<BlockModelSelection> getBlockModelSelections(Block block, IBlockAccess blockAccess, int x, int y, int z, int metadata) {
        List<BlockModelSelection> selections = new ArrayList<>();
        if (block == null) {
            return selections;
        }

        Identifier metadataModelId = this.blockMetadataModelOverrides.get(blockMetadataKey(block, metadata));
        if (metadataModelId != null) {
            this.loadModel(metadataModelId).ifPresent(model -> selections.add(new BlockModelSelection(model, JsonBlockState.EMPTY_VARIANT)));
            return selections;
        }

        Identifier blockModelId = this.blockModelOverrides.get(block);
        if (blockModelId != null) {
            this.loadModel(blockModelId).ifPresent(model -> selections.add(new BlockModelSelection(model, JsonBlockState.EMPTY_VARIANT)));
            return selections;
        }

        Identifier blockTextureId = getBlockTextureId(block);
        if (blockTextureId == null && !this.hasBlockStateOverride(block, metadata)) {
            return selections;
        }

        Optional<JsonBlockState> blockState = this.loadBlockState(block, metadata, blockTextureId);
        if (blockState.isPresent()) {
            Map<String, String> properties = this.createBlockStateProperties(block, blockAccess, x, y, z, metadata);
            for (JsonBlockState.Variant variant : blockState.get().getVariants(metadata, properties)) {
                if (variant.getModelId() == null) {
                    continue;
                }
                Optional<JsonBlockModel> model = this.loadModel(normalizeModelId(variant.getModelId(), "block"));
                if (model.isPresent()) {
                    selections.add(new BlockModelSelection(model.get(), variant));
                }
            }
            if (!selections.isEmpty()) {
                return selections;
            }
        }

        if (blockTextureId != null) {
            Identifier fallbackModelId = new Identifier(blockTextureId.getNamespace(), "block/" + blockTextureId.getPath());
            this.loadModel(fallbackModelId).ifPresent(model -> selections.add(new BlockModelSelection(model, JsonBlockState.EMPTY_VARIANT)));
        }
        return selections;
    }

    private boolean hasBlockStateOverride(Block block, int metadata) {
        return this.blockMetadataStateOverrides.containsKey(blockMetadataKey(block, metadata)) || this.blockStateOverrides.containsKey(block);
    }

    private Optional<JsonBlockState> loadBlockState(Block block, Identifier blockTextureId) {
        return this.loadBlockState(block, 0, blockTextureId);
    }

    private Optional<JsonBlockState> loadBlockState(Block block, int metadata, Identifier blockTextureId) {
        Identifier blockStateId = this.blockMetadataStateOverrides.get(blockMetadataKey(block, metadata));
        if (blockStateId == null) {
            blockStateId = this.blockStateOverrides.get(block);
        }
        if (blockStateId == null) {
            if (blockTextureId == null) {
                return Optional.empty();
            }
            blockStateId = new Identifier(blockTextureId.getNamespace(), blockTextureId.getPath());
        }

        Optional<JsonBlockState> cached = this.blockStateCache.get(blockStateId);
        if (cached != null) {
            return cached;
        }

        Optional<JsonBlockState> loaded = this.readBlockState(blockStateId);
        this.blockStateCache.put(blockStateId, loaded);
        return loaded;
    }

    private Map<String, String> createBlockStateProperties(Block block, IBlockAccess blockAccess, int x, int y, int z, int metadata) {
        Map<String, String> properties = new LinkedHashMap<>();
        JsonBlockState.addDefaultProperties(properties, metadata);
        JsonBlockStatePropertyResolver resolver = this.blockStatePropertyResolvers.get(block);
        if (resolver != null) {
            resolver.addProperties(block, blockAccess, x, y, z, metadata, properties);
        }
        return properties;
    }

    private Optional<JsonBlockState> readBlockState(Identifier blockStateId) {
        Identifier id = normalizeResourceId(blockStateId, "blockstates");
        ResourceLocation resourceLocation = new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath() + ".json");
        try (InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return Optional.of(JsonBlockState.fromJson(id, reader));
        } catch (IOException | RuntimeException exception) {
            return Optional.empty();
        }
    }

    private Optional<JsonBlockModel> loadModel(Identifier modelId) {
        Optional<JsonBlockModel> cached = this.modelCache.get(modelId);
        if (cached != null) {
            return cached;
        }

        Optional<JsonBlockModel> loaded = this.readModel(modelId);
        this.modelCache.put(modelId, loaded);
        return loaded;
    }

    private Optional<JsonBlockModel> readModel(Identifier modelId) {
        ResourceLocation resourceLocation = new ResourceLocation(modelId.getNamespace(), "models/" + modelId.getPath() + ".json");
        try (InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return Optional.of(JsonBlockModel.fromJson(modelId, reader, this));
        } catch (IOException exception) {
            return Optional.empty();
        } catch (RuntimeException exception) {
            LOGGER.warn("Failed to load JSON model {}", modelId, exception);
            return Optional.empty();
        }
    }

    private static Identifier getBlockTextureId(Block block) {
        String textureName = ((BlockAccessor) block).invokeGetTextureName();
        if (textureName == null || textureName.isEmpty()) {
            return null;
        }
        return new Identifier(textureName);
    }

    private static Identifier normalizeModelId(Identifier id, String defaultFolder) {
        String path = id.getPath();
        if (path.indexOf('/') < 0) {
            return new Identifier(id.getNamespace(), defaultFolder + "/" + path);
        }
        return id;
    }

    private static Identifier normalizeResourceId(Identifier id, String folder) {
        String path = id.getPath();
        String prefix = folder + "/";
        if (path.startsWith(prefix)) {
            return new Identifier(id.getNamespace(), path.substring(prefix.length()));
        }
        return id;
    }

    private static int blockMetadataKey(Block block, int metadata) {
        return (block.blockID << 4) | (metadata & 15);
    }

    private static long itemMetadataKey(Item item, int metadata) {
        return ((long) item.itemID << 32) | (metadata & 0xFFFFFFFFL);
    }

}
