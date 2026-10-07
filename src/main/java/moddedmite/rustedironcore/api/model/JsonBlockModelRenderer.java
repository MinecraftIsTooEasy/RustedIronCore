package moddedmite.rustedironcore.api.model;

import huix.glacier.util.Identifier;
import moddedmite.rustedironcore.mixin.accessor.RenderBlocksAccessor;
import net.minecraft.Block;
import net.minecraft.EntityLivingBase;
import net.minecraft.Icon;
import net.minecraft.ItemRenderer;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;
import net.minecraft.RenderBlocks;
import net.minecraft.RenderHelper;
import net.minecraft.ResourceLocation;
import net.minecraft.Tessellator;
import net.minecraft.TextureMap;
import net.minecraft.TextureObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Locale;

public final class JsonBlockModelRenderer {

    private static final int BLOCK_TEXTURE_MAP = 0;
    private static final int ITEM_TEXTURE_MAP = 1;
    private static final int MODEL_UV_SIZE = 16;
    private static final double EPSILON = 1.0E-5D;

    public static void renderBlock(JsonBlockModel model, JsonBlockState.Variant variant, RenderBlocks renderBlocks, Block block, int x, int y, int z, int metadata, float red, float green, float blue) {
        Tessellator tessellator = Tessellator.instance;
        WorldLightSampler lightSampler = new WorldLightSampler(renderBlocks.blockAccess, block, x, y, z);
        for (JsonModelElement element : model.getElements()) {
            renderElement(tessellator, model, variant, renderBlocks, block, null, element, x, y, z, metadata, lightSampler, red, green, blue, BLOCK_TEXTURE_MAP, false, true);
        }
    }

    public static boolean renderBlockAsItem(JsonBlockModel model, JsonBlockState.Variant variant, Block block, int metadata, float brightness) {
        return renderBlockAsItem(model, variant, block, metadata, brightness, JsonModelDisplayContext.BLOCK_ITEM);
    }

    public static boolean renderBlockAsItem(JsonBlockModel model, JsonBlockState.Variant variant, Block block, int metadata, float brightness, JsonModelDisplayContext context) {
        GL11.glPushMatrix();
        JsonModelTransforms.apply(model, context);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        renderBlockAsItemGeometry(model, variant, block, metadata, brightness);
        GL11.glPopMatrix();
        return true;
    }

    /** Draws block-model vertices in the current matrix without applying the model's item display transform. */
    public static void renderBlockAsItemGeometry(JsonBlockModel model, JsonBlockState.Variant variant,
                                                 Block block, int metadata, float brightness) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.instance;
        for (JsonModelElement element : model.getElements()) {
            renderElement(tessellator, model, variant, null, block, null, element,
                    0.0F, 0.0F, 0.0F, metadata, null,
                    brightness, brightness, brightness, BLOCK_TEXTURE_MAP, true, false);
        }
    }

    public static void renderBuiltInEntity(JsonBlockModel model, ItemStack stack, Icon fallbackIcon, JsonModelDisplayContext context, BuiltInEntityRenderer renderer) {
        renderBuiltInEntity(model, null, stack, fallbackIcon, context, renderer);
    }

    public static void renderBuiltInEntity(JsonBlockModel model, EntityLivingBase entity, ItemStack stack,
                                           Icon fallbackIcon, JsonModelDisplayContext context,
                                           BuiltInEntityRenderer renderer) {
        GL11.glPushMatrix();
        JsonModelTransforms.apply(model, context);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        renderer.render(entity, stack, context);
        GL11.glPopMatrix();
    }

    public static void renderBuiltInEntityIntoGui(JsonBlockModel model, ItemStack stack, int x, int y, float zLevel, BuiltInEntityRenderer renderer) {
        GL11.glPushMatrix();
        beginGuiRenderState();
        try {
            GL11.glTranslatef(x + 8.0F, y + 8.0F, zLevel);
            GL11.glScalef(16.0F, -16.0F, 16.0F);
            JsonModelTransforms.apply(model, JsonModelDisplayContext.GUI);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            renderer.render(null, stack, JsonModelDisplayContext.GUI);
        } finally {
            endGuiRenderState();
            GL11.glPopMatrix();
        }
    }

    public static void renderBuiltInEntityAsBlockItem(JsonBlockModel model, Block block, int metadata, BuiltInEntityRenderer renderer) {
        renderBuiltInEntityAsBlockItem(model, block, metadata, renderer, JsonModelDisplayContext.BLOCK_ITEM);
    }

    public static void renderBuiltInEntityAsBlockItem(JsonBlockModel model, Block block, int metadata, BuiltInEntityRenderer renderer, JsonModelDisplayContext context) {
        renderBuiltInEntityAsBlockItem(model, null, new ItemStack(block, 1, metadata), renderer, context);
    }

    public static void renderBuiltInEntityAsBlockItem(JsonBlockModel model, ItemStack stack,
                                                      BuiltInEntityRenderer renderer,
                                                      JsonModelDisplayContext context) {
        renderBuiltInEntityAsBlockItem(model, null, stack, renderer, context);
    }

    public static void renderBuiltInEntityAsBlockItem(JsonBlockModel model, EntityLivingBase entity,
                                                      ItemStack stack, BuiltInEntityRenderer renderer,
                                                      JsonModelDisplayContext context) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        JsonModelTransforms.apply(model, context);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        renderer.render(entity, stack, context);
        GL11.glPopMatrix();
    }

    public static boolean renderItem(JsonBlockModel model, ItemStack stack, Icon fallbackIcon, JsonModelDisplayContext context) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
        GL11.glPushMatrix();
        JsonModelTransforms.apply(model, context);
        if (model.isGeneratedItem()) {
            renderGeneratedItemLayers(model, stack, fallbackIcon, 0.0625F);
            GL11.glPopMatrix();
            return true;
        }
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        Tessellator tessellator = Tessellator.instance;
        for (JsonModelElement element : model.getElements()) {
            renderElement(tessellator, model, JsonBlockState.EMPTY_VARIANT, null, null, fallbackIcon, element, 0.0F, 0.0F, 0.0F, stack.getItemSubtype(), null, 1.0F, 1.0F, 1.0F, ITEM_TEXTURE_MAP, true, false);
        }

        GL11.glPopMatrix();
        return true;
    }

    public static boolean renderItemIntoGui(JsonBlockModel model, ItemStack stack, int x, int y, float zLevel) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
        GL11.glPushMatrix();
        beginGuiRenderState();
        try {
            GL11.glTranslatef(x + 8.0F, y + 8.0F, zLevel);
            GL11.glScalef(16.0F, -16.0F, 16.0F);
            JsonModelTransforms.apply(model, JsonModelDisplayContext.GUI);
            if (model.isGeneratedItem()) {
                renderGeneratedItemLayers(model, stack, stack.getIconIndex(), 0.0625F);
                return true;
            }
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

            Tessellator tessellator = Tessellator.instance;
            Icon fallbackIcon = stack.getIconIndex();
            for (JsonModelElement element : model.getElements()) {
                renderElement(tessellator, model, JsonBlockState.EMPTY_VARIANT, null, null, fallbackIcon, element, 0.0F, 0.0F, 0.0F, stack.getItemSubtype(), null, 1.0F, 1.0F, 1.0F, ITEM_TEXTURE_MAP, true, false);
            }
            return true;
        } finally {
            endGuiRenderState();
            GL11.glPopMatrix();
        }
    }

    private static void renderGeneratedItemLayers(JsonBlockModel model, ItemStack stack, Icon fallbackIcon, float thickness) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
        boolean rendered = false;
        for (int layer = 0; layer < 5; layer++) {
            String texture = model.getGeneratedItemLayerTexture(layer);
            int textureType = getAtlasTextureType(texture, ITEM_TEXTURE_MAP);
            Icon icon = getAtlasIcon(texture, textureType);
            if (icon == null) {
                if (layer == 0) {
                    icon = fallbackIcon;
                    textureType = stack.getItemSpriteNumber() == BLOCK_TEXTURE_MAP
                            ? BLOCK_TEXTURE_MAP
                            : ITEM_TEXTURE_MAP;
                } else {
                    break;
                }
            }
            if (icon == null) {
                continue;
            }
            if (layer > 0) {
                GL11.glTranslatef(0.0F, 0.0F, -thickness);
            }
            int color = stack.getItem().getColorFromItemStack(stack, layer);
            float red = ((color >> 16) & 255) / 255.0F;
            float green = ((color >> 8) & 255) / 255.0F;
            float blue = (color & 255) / 255.0F;
            GL11.glColor4f(red, green, blue, 1.0F);
            bindTextureAtlas(textureType);
            // Modern item/generated geometry uses x=u. MITE renderItemIn2D's
            // historical maxU/minU argument order mirrors the front face, so
            // pass the atlas bounds in modern left-to-right order here.
            ItemRenderer.renderItemIn2D(tessellator, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), thickness);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            rendered = true;
        }
        if (!rendered && fallbackIcon != null) {
            int textureType = stack.getItemSpriteNumber() == BLOCK_TEXTURE_MAP
                    ? BLOCK_TEXTURE_MAP
                    : ITEM_TEXTURE_MAP;
            bindTextureAtlas(textureType);
            ItemRenderer.renderItemIn2D(tessellator, fallbackIcon.getMinU(), fallbackIcon.getMinV(), fallbackIcon.getMaxU(), fallbackIcon.getMaxV(), fallbackIcon.getIconWidth(), fallbackIcon.getIconHeight(), thickness);
        }
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public static boolean renderBlockAsItemInGui(JsonBlockModel model, JsonBlockState.Variant variant, Block block, int metadata, int x, int y, float zLevel) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        beginGuiRenderState();
        try {
            GL11.glTranslatef(x + 8.0F, y + 8.0F, zLevel);
            GL11.glScalef(16.0F, -16.0F, 16.0F);
            JsonModelTransforms.apply(model, JsonModelDisplayContext.GUI);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

            Tessellator tessellator = Tessellator.instance;
            for (JsonModelElement element : model.getElements()) {
                renderElement(tessellator, model, variant, null, block, null, element,
                        0.0F, 0.0F, 0.0F, metadata, null,
                        1.0F, 1.0F, 1.0F, BLOCK_TEXTURE_MAP, true, false);
            }
            return true;
        } finally {
            endGuiRenderState();
            GL11.glPopMatrix();
        }
    }

    private static void beginGuiRenderState() {
        // GUI JSON models must not depend on whichever world or GUI renderer
        // happened to configure LIGHT0/LIGHT1 first. Preserve every state that
        // standard item lighting changes, then establish the vanilla GUI lights.
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT | GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void endGuiRenderState() {
        GL11.glPopAttrib();
    }

    private static void renderElement(Tessellator tessellator, JsonBlockModel model,
                                      JsonBlockState.Variant variant, RenderBlocks renderBlocks,
                                      Block block, Icon fallbackIcon, JsonModelElement element,
                                      float offsetX, float offsetY, float offsetZ, int metadata,
                                      WorldLightSampler lightSampler, float red, float green, float blue,
                                      int textureType, boolean manageDrawing, boolean applyFaceShade) {
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element,
                JsonModelDirection.DOWN, offsetX, offsetY, offsetZ, metadata, lightSampler,
                red, green, blue, textureType, manageDrawing, applyFaceShade);
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element,
                JsonModelDirection.UP, offsetX, offsetY, offsetZ, metadata, lightSampler,
                red, green, blue, textureType, manageDrawing, applyFaceShade);
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element,
                JsonModelDirection.NORTH, offsetX, offsetY, offsetZ, metadata, lightSampler,
                red, green, blue, textureType, manageDrawing, applyFaceShade);
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element,
                JsonModelDirection.SOUTH, offsetX, offsetY, offsetZ, metadata, lightSampler,
                red, green, blue, textureType, manageDrawing, applyFaceShade);
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element,
                JsonModelDirection.WEST, offsetX, offsetY, offsetZ, metadata, lightSampler,
                red, green, blue, textureType, manageDrawing, applyFaceShade);
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element,
                JsonModelDirection.EAST, offsetX, offsetY, offsetZ, metadata, lightSampler,
                red, green, blue, textureType, manageDrawing, applyFaceShade);
    }

    private static void emitFace(Tessellator tessellator, JsonBlockModel model,
                                 JsonBlockState.Variant variant, RenderBlocks renderBlocks,
                                 Block block, Icon fallbackIcon, JsonModelElement element,
                                 JsonModelDirection direction, float offsetX, float offsetY, float offsetZ,
                                 int metadata, WorldLightSampler lightSampler,
                                 float red, float green, float blue, int textureType,
                                 boolean manageDrawing, boolean applyFaceShade) {
        JsonModelFace face = element.getFace(direction);
        if (face == null) {
            return;
        }
        if (isDegenerateFace(element, direction)) {
            return;
        }
        if (shouldCullFace(face, variant, renderBlocks, block, offsetX, offsetY, offsetZ)) {
            return;
        }

        Icon icon = getIcon(model, face, direction, renderBlocks, block, fallbackIcon, metadata, textureType);
        if (icon == null) {
            return;
        }

        float[] uv = face.getUv(element.getFromX(), element.getFromY(), element.getFromZ(), element.getToX(), element.getToY(), element.getToZ(), direction, model.getTextureWidth(), model.getTextureHeight());
        int faceRotation = face.getRotation();
        if (variant.isUvLock()) {
            UvLockResult uvLockResult = recomputeUvLock(uv, faceRotation, direction, variant);
            uv = uvLockResult.uv();
            faceRotation = uvLockResult.rotation();
        }
        double[][] vertices = createFaceVertices(element, direction);
        double[][] textureUv = createTextureUv(icon, uv, faceRotation, direction, model.getTextureWidth(), model.getTextureHeight());

        boolean startedDrawing = manageDrawing && !tessellator.isDrawing;
        if (startedDrawing) {
            tessellator.startDrawingQuads();
        }

        double[][] transformedVertices = new double[4][3];
        for (int i = 0; i < 4; i++) {
            transformedVertices[i] = transformVertex(vertices[i], element.getRotation(), variant, offsetX, offsetY, offsetZ);
        }

        JsonModelDirection facing = calculateFacing(transformedVertices, direction);
        if (lightSampler != null) {
            tessellator.setBrightness(lightSampler.sampleFace(transformedVertices, facing));
        }
        float shade = applyFaceShade ? getShade(facing, element.isShade()) : 1.0F;
        if (face.getTintIndex() >= 0) {
            tessellator.setColorOpaque_F(red * shade, green * shade, blue * shade);
        } else {
            tessellator.setColorOpaque_F(shade, shade, shade);
        }
        setNormal(tessellator, facing);
        for (int i = 0; i < 4; i++) {
            double[] transformed = transformedVertices[i];
            tessellator.addVertexWithUV(transformed[0], transformed[1], transformed[2], textureUv[i][0], textureUv[i][1]);
        }

        if (startedDrawing) {
            tessellator.draw();
        }
    }

    private static boolean isDegenerateFace(JsonModelElement element, JsonModelDirection direction) {
        double width = Math.abs(element.getToX() - element.getFromX());
        double height = Math.abs(element.getToY() - element.getFromY());
        double depth = Math.abs(element.getToZ() - element.getFromZ());
        // A modern cross model intentionally has zero thickness on its plane.
        // Only reject faces whose two in-plane dimensions have zero area.
        return switch (direction) {
            case DOWN, UP -> width <= EPSILON || depth <= EPSILON;
            case NORTH, SOUTH -> width <= EPSILON || height <= EPSILON;
            case WEST, EAST -> depth <= EPSILON || height <= EPSILON;
        };
    }

    private static Icon getIcon(JsonBlockModel model, JsonModelFace face, JsonModelDirection direction, RenderBlocks renderBlocks, Block block, Icon fallbackIcon, int metadata, int textureType) {
        String textureReference = model.resolveTexture(face.getTexture());
        Icon icon = getAtlasIcon(textureReference, textureType);
        if (icon != null) {
            return icon;
        }

        if (renderBlocks != null && block != null) {
            return renderBlocks.getBlockIconFromSideAndMetadata(block, direction.getSide(), metadata);
        }

        if (block != null) {
            return block.getIcon(direction.getSide(), metadata);
        }

        return fallbackIcon;
    }

    private static Icon getAtlasIcon(String textureReference, int textureType) {
        String iconName = toAtlasIconName(textureReference, textureType);
        if (iconName == null) {
            return null;
        }

        ResourceLocation atlasLocation = textureType == ITEM_TEXTURE_MAP ? TextureMap.locationItemsTexture : TextureMap.locationBlocksTexture;
        TextureObject textureObject = Minecraft.getMinecraft().getTextureManager().getTexture(atlasLocation);
        if (!(textureObject instanceof TextureMap textureMap)) {
            return null;
        }

        Icon icon = textureMap.getAtlasSprite(iconName);
        if (icon == null || "missingno".equals(icon.getIconName())) {
            return null;
        }
        return icon;
    }

    static int getAtlasTextureType(String textureReference, int defaultTextureType) {
        if (textureReference == null || textureReference.isEmpty()) {
            return defaultTextureType;
        }

        Identifier textureId = new Identifier(textureReference.replace("#", ""));
        String path = textureId.getPath().toLowerCase(Locale.ROOT);
        if (path.startsWith("textures/")) {
            path = path.substring("textures/".length());
        }
        if (path.startsWith("blocks/") || path.startsWith("block/")) {
            return BLOCK_TEXTURE_MAP;
        }
        if (path.startsWith("items/") || path.startsWith("item/")) {
            return ITEM_TEXTURE_MAP;
        }
        return defaultTextureType;
    }

    private static void bindTextureAtlas(int textureType) {
        ResourceLocation atlasLocation = textureType == ITEM_TEXTURE_MAP
                ? TextureMap.locationItemsTexture
                : TextureMap.locationBlocksTexture;
        Minecraft.getMinecraft().getTextureManager().bindTexture(atlasLocation);
    }

    public static String toAtlasIconName(String textureReference, int textureType) {
        if (textureReference == null || textureReference.isEmpty()) {
            return null;
        }

        Identifier textureId = new Identifier(textureReference.replace("#", ""));
        String path = textureId.getPath().toLowerCase(Locale.ROOT);
        if (path.endsWith(".png")) {
            path = path.substring(0, path.length() - 4);
        }

        if (path.startsWith("textures/")) {
            path = path.substring("textures/".length());
        }

        if (textureType == BLOCK_TEXTURE_MAP) {
            if (path.startsWith("blocks/")) {
                path = path.substring("blocks/".length());
            } else if (path.startsWith("block/")) {
                path = path.substring("block/".length());
            } else if (path.startsWith("items/") || path.startsWith("item/")) {
                return null;
            }
        } else if (textureType == ITEM_TEXTURE_MAP) {
            if (path.startsWith("items/")) {
                path = path.substring("items/".length());
            } else if (path.startsWith("item/")) {
                path = path.substring("item/".length());
            } else if (path.startsWith("blocks/") || path.startsWith("block/")) {
                return null;
            }
        }

        if (path.isEmpty()) {
            return null;
        }

        return "minecraft".equals(textureId.getNamespace()) ? path : textureId.getNamespace() + ":" + path;
    }

    private static double[][] createFaceVertices(JsonModelElement element, JsonModelDirection direction) {
        double fromX = element.getFromX();
        double fromY = element.getFromY();
        double fromZ = element.getFromZ();
        double toX = element.getToX();
        double toY = element.getToY();
        double toZ = element.getToZ();

        return switch (direction) {
            case DOWN -> new double[][]{
                    new double[]{fromX, fromY, toZ},
                    new double[]{fromX, fromY, fromZ},
                    new double[]{toX, fromY, fromZ},
                    new double[]{toX, fromY, toZ}};
            case UP -> new double[][]{
                    new double[]{fromX, toY, fromZ},
                    new double[]{fromX, toY, toZ},
                    new double[]{toX, toY, toZ},
                    new double[]{toX, toY, fromZ}};
            case NORTH -> new double[][]{
                    new double[]{toX, toY, fromZ},
                    new double[]{toX, fromY, fromZ},
                    new double[]{fromX, fromY, fromZ},
                    new double[]{fromX, toY, fromZ}};
            case SOUTH -> new double[][]{
                    new double[]{fromX, toY, toZ},
                    new double[]{fromX, fromY, toZ},
                    new double[]{toX, fromY, toZ},
                    new double[]{toX, toY, toZ}};
            case WEST -> new double[][]{
                    new double[]{fromX, toY, fromZ},
                    new double[]{fromX, fromY, fromZ},
                    new double[]{fromX, fromY, toZ},
                    new double[]{fromX, toY, toZ}};
            case EAST -> new double[][]{
                    new double[]{toX, toY, toZ},
                    new double[]{toX, fromY, toZ},
                    new double[]{toX, fromY, fromZ},
                    new double[]{toX, toY, fromZ}};
        };
    }

    private static boolean shouldCullFace(JsonModelFace face, JsonBlockState.Variant variant, RenderBlocks renderBlocks, Block block, float offsetX, float offsetY, float offsetZ) {
        if (face.getCullFace() == null || renderBlocks == null || block == null || renderBlocks.blockAccess == null || ((RenderBlocksAccessor) (Object) renderBlocks).rustedIronCore$getRenderAllFaces()) {
            return false;
        }

        JsonModelDirection cullDirection = rotateCullDirection(face.getCullFace(), variant);
        int x = (int) Math.floor(offsetX);
        int y = (int) Math.floor(offsetY);
        int z = (int) Math.floor(offsetZ);
        return switch (cullDirection) {
            case DOWN -> !block.shouldSideBeRendered(renderBlocks.blockAccess, x, y - 1, z, cullDirection.getSide());
            case UP -> !block.shouldSideBeRendered(renderBlocks.blockAccess, x, y + 1, z, cullDirection.getSide());
            case NORTH -> !block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z - 1, cullDirection.getSide());
            case SOUTH -> !block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z + 1, cullDirection.getSide());
            case WEST -> !block.shouldSideBeRendered(renderBlocks.blockAccess, x - 1, y, z, cullDirection.getSide());
            case EAST -> !block.shouldSideBeRendered(renderBlocks.blockAccess, x + 1, y, z, cullDirection.getSide());
        };
    }

    private static JsonModelDirection rotateCullDirection(JsonModelDirection direction, JsonBlockState.Variant variant) {
        JsonModelDirection rotated = direction;
        int xTurns = Math.floorMod(variant.getX() / 90, 4);
        for (int i = 0; i < xTurns; i++) {
            rotated = rotateDirectionX(rotated);
        }
        int yTurns = Math.floorMod(variant.getY() / 90, 4);
        for (int i = 0; i < yTurns; i++) {
            rotated = rotateDirectionY(rotated);
        }
        return rotated;
    }

    private static JsonModelDirection rotateDirectionX(JsonModelDirection direction) {
        return switch (direction) {
            case NORTH -> JsonModelDirection.DOWN;
            case DOWN -> JsonModelDirection.SOUTH;
            case SOUTH -> JsonModelDirection.UP;
            case UP -> JsonModelDirection.NORTH;
            default -> direction;
        };
    }

    private static JsonModelDirection rotateDirectionY(JsonModelDirection direction) {
        return switch (direction) {
            case NORTH -> JsonModelDirection.EAST;
            case EAST -> JsonModelDirection.SOUTH;
            case SOUTH -> JsonModelDirection.WEST;
            case WEST -> JsonModelDirection.NORTH;
            default -> direction;
        };
    }

    private static UvLockResult recomputeUvLock(float[] uv, int rotation, JsonModelDirection direction, JsonBlockState.Variant variant) {
        int reverse0 = getReverseUvIndex(0, rotation);
        double u0 = getUvU(uv, rotation, reverse0);
        double v0 = getUvV(uv, rotation, reverse0);
        double[] transformed0 = applyUvLockTransform(u0 / 16.0D, v0 / 16.0D, 0.0D, direction, variant);
        float newU0 = (float) (16.0D * transformed0[0]);
        float newV0 = (float) (16.0D * transformed0[1]);

        int reverse2 = getReverseUvIndex(2, rotation);
        double u2 = getUvU(uv, rotation, reverse2);
        double v2 = getUvV(uv, rotation, reverse2);
        double[] transformed2 = applyUvLockTransform(u2 / 16.0D, v2 / 16.0D, 0.0D, direction, variant);
        float newU2 = (float) (16.0D * transformed2[0]);
        float newV2 = (float) (16.0D * transformed2[1]);

        float minU = Math.signum(u2 - u0) == Math.signum(newU2 - newU0) ? newU0 : newU2;
        float maxU = Math.signum(u2 - u0) == Math.signum(newU2 - newU0) ? newU2 : newU0;
        float minV = Math.signum(v2 - v0) == Math.signum(newV2 - newV0) ? newV0 : newV2;
        float maxV = Math.signum(v2 - v0) == Math.signum(newV2 - newV0) ? newV2 : newV0;

        double radians = Math.toRadians(rotation);
        double[] rotationVector = applyUvLockRotationVector(Math.cos(radians), Math.sin(radians), 0.0D, direction, variant);
        int lockedRotation = Math.floorMod(-((int) Math.round(Math.toDegrees(Math.atan2(rotationVector[1], rotationVector[0])) / 90.0D)) * 90, 360);
        return new UvLockResult(new float[]{minU, minV, maxU, maxV}, lockedRotation);
    }

    private static int getReverseUvIndex(int index, int rotation) {
        return Math.floorMod(index + 4 - Math.floorMod(rotation / 90, 4), 4);
    }

    private static double getUvU(float[] uv, int rotation, int index) {
        int shifted = Math.floorMod(index + Math.floorMod(rotation / 90, 4), 4);
        return (shifted != 0 && shifted != 1) ? uv[2] : uv[0];
    }

    private static double getUvV(float[] uv, int rotation, int index) {
        int shifted = Math.floorMod(index + Math.floorMod(rotation / 90, 4), 4);
        return (shifted != 0 && shifted != 3) ? uv[3] : uv[1];
    }

    private static double[] applyUvLockTransform(double x, double y, double z, JsonModelDirection direction, JsonBlockState.Variant variant) {
        double[] vector = new double[]{x - 0.5D, y - 0.5D, z - 0.5D};
        JsonModelDirection rotatedDirection = rotateCullDirection(direction, variant);
        vector = applyVanillaUvLocalToGlobal(rotatedDirection, vector);
        vector = applyModelRotationInverse(vector, variant);
        vector = applyVanillaUvGlobalToLocal(direction, vector);
        return new double[]{vector[0] + 0.5D, vector[1] + 0.5D, vector[2] + 0.5D};
    }

    private static double[] applyUvLockRotationVector(double x, double y, double z, JsonModelDirection direction, JsonBlockState.Variant variant) {
        double[] vector = applyVanillaUvLocalToGlobal(rotateCullDirection(direction, variant), new double[]{x, y, z});
        vector = applyModelRotationInverse(vector, variant);
        return applyVanillaUvGlobalToLocal(direction, vector);
    }

    private static double[] applyVanillaUvLocalToGlobal(JsonModelDirection direction, double[] vector) {
        return switch (direction) {
            case EAST -> rotateVectorY(vector, 90.0D);
            case WEST -> rotateVectorY(vector, -90.0D);
            case NORTH -> rotateVectorY(vector, 180.0D);
            case UP -> rotateVectorX(vector, -90.0D);
            case DOWN -> rotateVectorX(vector, 90.0D);
            case SOUTH -> vector;
        };
    }

    private static double[] applyVanillaUvGlobalToLocal(JsonModelDirection direction, double[] vector) {
        return switch (direction) {
            case EAST -> rotateVectorY(vector, -90.0D);
            case WEST -> rotateVectorY(vector, 90.0D);
            case NORTH -> rotateVectorY(vector, -180.0D);
            case UP -> rotateVectorX(vector, 90.0D);
            case DOWN -> rotateVectorX(vector, -90.0D);
            case SOUTH -> vector;
        };
    }

    private static double[] applyModelRotationInverse(double[] vector, JsonBlockState.Variant variant) {
        vector = rotateVectorY(vector, -variant.getY());
        return rotateVectorX(vector, variant.getX());
    }

    private static double[] rotateVectorX(double[] vector, double degrees) {
        if (degrees == 0.0D) {
            return vector;
        }
        double radians = Math.toRadians(degrees);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return new double[]{vector[0], vector[1] * cos - vector[2] * sin, vector[1] * sin + vector[2] * cos};
    }

    private static double[] rotateVectorY(double[] vector, double degrees) {
        if (degrees == 0.0D) {
            return vector;
        }
        double radians = Math.toRadians(degrees);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return new double[]{vector[0] * cos - vector[2] * sin, vector[1], vector[0] * sin + vector[2] * cos};
    }

    private record UvLockResult(float[] uv, int rotation) {
    }

    private static double[][] createTextureUv(Icon icon, float[] uv, int rotation, JsonModelDirection direction, int textureWidth, int textureHeight) {
        double minU = interpolateU(icon, uv[0], MODEL_UV_SIZE);
        double minV = interpolateV(icon, uv[1], MODEL_UV_SIZE);
        double maxU = interpolateU(icon, uv[2], MODEL_UV_SIZE);
        double maxV = interpolateV(icon, uv[3], MODEL_UV_SIZE);

        double[][] textureUv = new double[4][2];
        int turns = Math.floorMod(rotation / 90, 4);
        for (int i = 0; i < 4; i++) {
            int shifted = Math.floorMod(i + turns, 4);
            textureUv[i][0] = (shifted != 0 && shifted != 1) ? maxU : minU;
            textureUv[i][1] = (shifted != 0 && shifted != 3) ? maxV : minV;
        }
        return textureUv;
    }

    private static double interpolateU(Icon icon, float value, int textureWidth) {
        return icon.getMinU() + (icon.getMaxU() - icon.getMinU()) * (value / textureWidth);
    }

    private static double interpolateV(Icon icon, float value, int textureHeight) {
        return icon.getMinV() + (icon.getMaxV() - icon.getMinV()) * (value / textureHeight);
    }

    private static float getShade(JsonModelDirection direction, boolean shade) {
        if (!shade) {
            return 1.0F;
        }
        return switch (direction) {
            case DOWN -> 0.5F;
            case UP -> 1.0F;
            case NORTH, SOUTH -> 0.8F;
            case WEST, EAST -> 0.6F;
        };
    }

    private static JsonModelDirection calculateFacing(double[][] vertices, JsonModelDirection fallbackDirection) {
        if (vertices == null || vertices.length < 3) {
            return fallbackDirection;
        }

        double[] v0 = vertices[0];
        double[] v1 = vertices[1];
        double[] v2 = vertices[2];
        double[] a = new double[]{v0[0] - v1[0], v0[1] - v1[1], v0[2] - v1[2]};
        double[] b = new double[]{v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]};
        double[] normal = new double[]{
                b[1] * a[2] - b[2] * a[1],
                b[2] * a[0] - b[0] * a[2],
                b[0] * a[1] - b[1] * a[0]
        };

        double lengthSquared = normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2];
        if (lengthSquared <= EPSILON) {
            return fallbackDirection;
        }

        double length = Math.sqrt(lengthSquared);
        double nx = normal[0] / length;
        double ny = normal[1] / length;
        double nz = normal[2] / length;

        JsonModelDirection bestDirection = null;
        double bestDot = 0.0D;
        for (JsonModelDirection candidate : JsonModelDirection.values()) {
            double dot = nx * candidateNormalX(candidate) + ny * candidateNormalY(candidate) + nz * candidateNormalZ(candidate);
            if (dot >= 0.0D && dot > bestDot) {
                bestDot = dot;
                bestDirection = candidate;
            }
        }

        return bestDirection != null ? bestDirection : fallbackDirection;
    }

    private static double candidateNormalX(JsonModelDirection direction) {
        return switch (direction) {
            case WEST -> -1.0D;
            case EAST -> 1.0D;
            default -> 0.0D;
        };
    }

    private static double candidateNormalY(JsonModelDirection direction) {
        return switch (direction) {
            case DOWN -> -1.0D;
            case UP -> 1.0D;
            default -> 0.0D;
        };
    }

    private static double candidateNormalZ(JsonModelDirection direction) {
        return switch (direction) {
            case NORTH -> -1.0D;
            case SOUTH -> 1.0D;
            default -> 0.0D;
        };
    }

    private static double[] transformVertex(double[] vertex, JsonModelRotation rotation, JsonBlockState.Variant variant, float offsetX, float offsetY, float offsetZ) {
        double[] transformed = new double[]{vertex[0], vertex[1], vertex[2]};
        transformed = rotateModelVertex(transformed, rotation, true);
        if (variant.getX() != 0) {
            transformed = rotateModelVertex(transformed, JsonModelRotation.of("x", -variant.getX(), 8.0F, 8.0F, 8.0F), false);
        }
        if (variant.getY() != 0) {
            transformed = rotateModelVertex(transformed, JsonModelRotation.of("y", variant.getY(), 8.0F, 8.0F, 8.0F), false);
        }
        return new double[]{transformed[0] / 16.0D + offsetX, transformed[1] / 16.0D + offsetY, transformed[2] / 16.0D + offsetZ};
    }

    private static double[] rotateModelVertex(double[] vertex, JsonModelRotation rotation, boolean invertElementY) {
        if (rotation == null || !rotation.isPresent()) {
            return vertex;
        }

        float[] origin = rotation.getOrigin();
        double x = vertex[0] - origin[0];
        double y = vertex[1] - origin[1];
        double z = vertex[2] - origin[2];
        float angle = rotation.getAngle();
        if (invertElementY && "y".equals(rotation.getAxis())) {
            angle = -angle;
        }
        double radians = Math.toRadians(angle);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        double[] rotated = switch (rotation.getAxis()) {
            case "x" -> new double[]{x + origin[0], (y * cos - z * sin) + origin[1], (y * sin + z * cos) + origin[2]};
            case "y" -> new double[]{(x * cos - z * sin) + origin[0], y + origin[1], (x * sin + z * cos) + origin[2]};
            case "z" -> new double[]{(x * cos - y * sin) + origin[0], (x * sin + y * cos) + origin[1], z + origin[2]};
            default -> vertex;
        };
        if (!rotation.isRescale()) {
            return rotated;
        }

        double[] scale = getElementRotationScale(rotation);
        return new double[]{
                (rotated[0] - origin[0]) * scale[0] + origin[0],
                (rotated[1] - origin[1]) * scale[1] + origin[1],
                (rotated[2] - origin[2]) * scale[2] + origin[2]
        };
    }

    private static double[] getElementRotationScale(JsonModelRotation rotation) {
        double rescale = Math.abs(rotation.getAngle()) == 22.5F
                ? 1.0D / Math.cos(Math.PI / 8.0D)
                : 1.0D / Math.cos(Math.PI / 4.0D);
        return switch (rotation.getAxis()) {
            case "x" -> new double[]{1.0D, rescale, rescale};
            case "y" -> new double[]{rescale, 1.0D, rescale};
            case "z" -> new double[]{rescale, rescale, 1.0D};
            default -> new double[]{1.0D, 1.0D, 1.0D};
        };
    }

    private static void setNormal(Tessellator tessellator, JsonModelDirection direction) {
        switch (direction) {
            case DOWN -> tessellator.setNormal(0.0F, -1.0F, 0.0F);
            case UP -> tessellator.setNormal(0.0F, 1.0F, 0.0F);
            case NORTH -> tessellator.setNormal(0.0F, 0.0F, -1.0F);
            case SOUTH -> tessellator.setNormal(0.0F, 0.0F, 1.0F);
            case WEST -> tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            case EAST -> tessellator.setNormal(1.0F, 0.0F, 0.0F);
        }
    }

}
