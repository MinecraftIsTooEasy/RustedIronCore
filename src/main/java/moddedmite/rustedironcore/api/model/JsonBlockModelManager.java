package moddedmite.rustedironcore.api.model;

import huix.glacier.util.Identifier;
import moddedmite.rustedironcore.api.util.LogUtil;
import moddedmite.rustedironcore.mixin.accessor.BlockAccessor;
import moddedmite.rustedironcore.mixin.accessor.ItemAccessor;
import net.minecraft.Block;
import net.minecraft.Icon;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
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
    private final Map<Item, Identifier> itemModelOverrides = new IdentityHashMap<>();
    private final Map<Long, Identifier> itemMetadataModelOverrides = new HashMap<>();
    private final Set<Integer> jsonRenderTypes = new HashSet<>();
    private final Map<Item, BuiltInEntityRenderer> builtInRenderers = new IdentityHashMap<>();
    private final Map<Long, BuiltInEntityRenderer> builtInRenderersByMetadata = new HashMap<>();
    private final Map<Block, BuiltInEntityRenderer> builtInRenderersByBlock = new IdentityHashMap<>();

    private JsonBlockModelManager() {
    }

    public boolean hasJsonBlockInRenderType(int renderType) {
        return this.jsonRenderTypes.contains(renderType);
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
            this.jsonRenderTypes.add(block.getRenderType());
        }
    }

    public void registerBlockModel(Block block, int metadata, String modelId) {
        this.registerBlockModel(block, metadata, new Identifier(modelId));
    }

    public void registerBlockModel(Block block, int metadata, Identifier modelId) {
        if (block != null && modelId != null) {
            this.blockMetadataModelOverrides.put(blockMetadataKey(block, metadata), normalizeModelId(modelId, "block"));
            this.jsonRenderTypes.add(block.getRenderType());
        }
    }

    public void registerBlockState(Block block, String blockStateId) {
        this.registerBlockState(block, new Identifier(blockStateId));
    }

    public void registerBlockState(Block block, Identifier blockStateId) {
        if (block != null && blockStateId != null) {
            this.blockStateOverrides.put(block, normalizeResourceId(blockStateId, "blockstates"));
            this.jsonRenderTypes.add(block.getRenderType());
        }
    }

    public void registerItemModel(Item item, String modelId) {
        this.registerItemModel(item, new Identifier(modelId));
    }

    public void registerItemModel(Item item, Identifier modelId) {
        if (item != null && modelId != null) {
            this.itemModelOverrides.put(item, normalizeModelId(modelId, "item"));
        }
    }

    public void registerItemModel(Item item, int metadata, String modelId) {
        this.registerItemModel(item, metadata, new Identifier(modelId));
    }

    public void registerItemModel(Item item, int metadata, Identifier modelId) {
        if (item != null && modelId != null) {
            this.itemMetadataModelOverrides.put(itemMetadataKey(item, metadata), normalizeModelId(modelId, "item"));
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
        Optional<BlockModelSelection> selection = this.getBlockModelSelection(block, metadata);
        if (selection.isEmpty() || selection.get().model.getElements().isEmpty()) {
            return false;
        }

        int brightness = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z);
        int color = block.colorMultiplier(renderBlocks.blockAccess, x, y, z);
        float red = ((color >> 16) & 255) / 255.0F;
        float green = ((color >> 8) & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        JsonBlockModelRenderer.renderBlock(selection.get().model, selection.get().variant, renderBlocks, block, x, y, z, metadata, brightness, red, green, blue);
        return true;
    }

    public boolean renderBlockAsItem(Block block, int metadata, float brightness) {
        return renderBlockAsItem(block, metadata, brightness, JsonBlockModel.RenderContext.BLOCK_ITEM);
    }

    public boolean renderBlockAsItem(Block block, int metadata, float brightness, JsonBlockModel.RenderContext context) {
        ItemStack stack = new ItemStack(block, 1, metadata);
        BuiltInEntityRenderer builtInRenderer = this.getBuiltInRenderer(stack);
        if (builtInRenderer != null) {
            JsonBlockModel model = this.getItemModel(stack).orElse(null);
            if (model == null) {
                Optional<BlockModelSelection> selection = this.getBlockModelSelection(block, metadata);
                if (selection.isEmpty()) {
                    return false;
                }
                model = selection.get().model;
            }
            JsonBlockModelRenderer.renderBuiltInEntityAsBlockItem(model, block, metadata, builtInRenderer, context);
            return true;
        }

        if (!this.jsonRenderTypes.contains(block.getRenderType())) {
            return false;
        }
        Optional<BlockModelSelection> selection = this.getBlockModelSelection(block, metadata);
        if (selection.isEmpty()) {
            return false;
        }

        if (selection.get().model.getElements().isEmpty()) {
            return false;
        }

        JsonBlockModelRenderer.renderBlockAsItem(selection.get().model, JsonBlockState.EMPTY_VARIANT, block, metadata, brightness, context);
        return true;
    }

    public boolean renderItem(ItemStack stack, Icon fallbackIcon, JsonBlockModel.RenderContext context) {
        Optional<JsonBlockModel> model = this.getItemModel(stack);
        if (model.isEmpty()) {
            return false;
        }

        BuiltInEntityRenderer renderer = this.getBuiltInRenderer(stack);
        if (renderer != null) {
            JsonBlockModelRenderer.renderBuiltInEntity(model.get(), stack, fallbackIcon, context, renderer);
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

        BuiltInEntityRenderer builtInRenderer = this.getBuiltInRenderer(stack);

        if (stack.isBlock()) {
            Block block = Block.blocksList[stack.itemID];
            if (block == null) {
                return false;
            }
            if (builtInRenderer != null) {
                JsonBlockModel model = this.getItemModel(stack).orElse(null);
                if (model == null) {
                    Optional<BlockModelSelection> selection = this.getBlockModelSelection(block, stack.getItemSubtype());
                    if (selection.isEmpty()) {
                        return false;
                    }
                    model = selection.get().model;
                }
                JsonBlockModelRenderer.renderBuiltInEntityIntoGui(model, stack, x, y, zLevel, builtInRenderer);
                return true;
            }
            if (!this.jsonRenderTypes.contains(block.getRenderType())) {
                return false;
            }
            Optional<BlockModelSelection> selection = this.getBlockModelSelection(block, stack.getItemSubtype());
            if (selection.isEmpty() || selection.get().model.getElements().isEmpty()) {
                return false;
            }
            JsonBlockModelRenderer.renderBlockAsItemInGui(selection.get().model, JsonBlockState.EMPTY_VARIANT, block, stack.getItemSubtype(), x, y, zLevel);
            return true;
        }

        Optional<JsonBlockModel> model = this.getItemModel(stack);
        if (model.isEmpty()) {
            return false;
        }
        if (builtInRenderer != null) {
            JsonBlockModelRenderer.renderBuiltInEntityIntoGui(model.get(), stack, x, y, zLevel, builtInRenderer);
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
        return selection.map(blockModelSelection -> blockModelSelection.model);
    }

    public Optional<JsonBlockModel> getItemModel(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return Optional.empty();
        }

        Item item = stack.getItem();
        Identifier metadataModelId = this.itemMetadataModelOverrides.get(itemMetadataKey(item, stack.getItemSubtype()));
        if (metadataModelId != null) {
            return this.loadModel(metadataModelId);
        }

        Identifier modelId = this.itemModelOverrides.get(item);
        if (modelId != null) {
            return this.loadModel(modelId);
        }

        String iconString = ((ItemAccessor) item).invokeGetIconString();
        if (iconString == null || iconString.isEmpty()) {
            return Optional.empty();
        }

        Identifier itemTextureId = new Identifier(iconString);
        return this.loadModel(new Identifier(itemTextureId.getNamespace(), "item/" + itemTextureId.getPath()));
    }

    Optional<JsonBlockModel> loadModelById(Identifier modelId) {
        return this.loadModel(modelId);
    }

    public void registerTextureAtlasSprites(TextureMap textureMap) {
        int textureType = textureMap.getTextureType();
        Set<String> iconNames = new HashSet<>();
        if (textureType == BLOCK_TEXTURE_MAP) {
            this.collectBlockTextureIcons(iconNames);
        } else if (textureType == ITEM_TEXTURE_MAP) {
            this.collectItemTextureIcons(iconNames);
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

            for (int metadata = 0; metadata < 16; metadata++) {
                Optional<BlockModelSelection> selection = this.getBlockModelSelection(block, metadata);
                if (selection.isPresent()) {
                    collectTextureIcons(selection.get().model, BLOCK_TEXTURE_MAP, iconNames);
                }
            }
        }

        for (Identifier modelId : this.blockModelOverrides.values()) {
            this.loadModel(modelId).ifPresent(model -> collectTextureIcons(model, BLOCK_TEXTURE_MAP, iconNames));
        }
        for (Identifier modelId : this.blockMetadataModelOverrides.values()) {
            this.loadModel(modelId).ifPresent(model -> collectTextureIcons(model, BLOCK_TEXTURE_MAP, iconNames));
        }
    }

    private void collectItemTextureIcons(Set<String> iconNames) {
        for (Item item : Item.itemsList) {
            if (item == null) {
                continue;
            }

            Optional<JsonBlockModel> model = this.getItemModel(new ItemStack(item));
            model.ifPresent(jsonBlockModel -> collectTextureIcons(jsonBlockModel, ITEM_TEXTURE_MAP, iconNames));
        }

        for (Identifier modelId : this.itemModelOverrides.values()) {
            this.loadModel(modelId).ifPresent(model -> collectTextureIcons(model, ITEM_TEXTURE_MAP, iconNames));
        }
        for (Identifier modelId : this.itemMetadataModelOverrides.values()) {
            this.loadModel(modelId).ifPresent(model -> collectTextureIcons(model, ITEM_TEXTURE_MAP, iconNames));
        }
    }

    private static void collectTextureIcons(JsonBlockModel model, int textureType, Set<String> iconNames) {
        for (String texture : model.collectResolvedTextures()) {
            String iconName = JsonBlockModelRenderer.toAtlasIconName(texture, textureType);
            if (iconName != null) {
                iconNames.add(iconName);
            }
        }
    }

    private Optional<BlockModelSelection> getBlockModelSelection(Block block, int metadata) {
        if (block == null) {
            return Optional.empty();
        }

        Identifier metadataModelId = this.blockMetadataModelOverrides.get(blockMetadataKey(block, metadata));
        if (metadataModelId != null) {
            return this.loadModel(metadataModelId).map(model -> new BlockModelSelection(model, JsonBlockState.EMPTY_VARIANT));
        }

        Identifier blockModelId = this.blockModelOverrides.get(block);
        if (blockModelId != null) {
            Optional<JsonBlockModel> model = this.loadModel(blockModelId);
            if (model.isPresent()) {
                return Optional.of(new BlockModelSelection(model.get(), JsonBlockState.EMPTY_VARIANT));
            }
        }

        Identifier blockTextureId = getBlockTextureId(block);
        if (blockTextureId == null && !this.blockStateOverrides.containsKey(block)) {
            return Optional.empty();
        }

        Optional<JsonBlockState> blockState = this.loadBlockState(block, blockTextureId);
        if (blockState.isPresent()) {
            JsonBlockState.Variant variant = blockState.get().getVariant(metadata);
            if (variant.getModelId() != null) {
                Optional<JsonBlockModel> model = this.loadModel(normalizeModelId(variant.getModelId(), "block"));
                if (model.isPresent()) {
                    return Optional.of(new BlockModelSelection(model.get(), variant));
                }
            }
        }

        if (blockTextureId != null) {
            Identifier fallbackModelId = new Identifier(blockTextureId.getNamespace(), "block/" + blockTextureId.getPath());
            return this.loadModel(fallbackModelId).map(model -> new BlockModelSelection(model, JsonBlockState.EMPTY_VARIANT));
        }
        return Optional.empty();
    }

    private Optional<JsonBlockState> loadBlockState(Block block, Identifier blockTextureId) {
        Identifier blockStateId = this.blockStateOverrides.get(block);
        if (blockStateId == null) {
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

    private static final class BlockModelSelection {
        private final JsonBlockModel model;
        private final JsonBlockState.Variant variant;

        private BlockModelSelection(JsonBlockModel model, JsonBlockState.Variant variant) {
            this.model = model;
            this.variant = variant;
        }
    }
}
