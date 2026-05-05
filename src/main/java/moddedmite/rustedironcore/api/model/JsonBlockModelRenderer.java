package moddedmite.rustedironcore.api.model;

import huix.glacier.util.Identifier;
import net.minecraft.Block;
import net.minecraft.Icon;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;
import net.minecraft.RenderBlocks;
import net.minecraft.ResourceLocation;
import net.minecraft.Tessellator;
import net.minecraft.TextureMap;
import net.minecraft.TextureObject;
import org.lwjgl.opengl.GL11;

import java.util.Locale;

public final class JsonBlockModelRenderer {

    private static final int BLOCK_TEXTURE_MAP = 0;
    private static final int ITEM_TEXTURE_MAP = 1;
    private static final int FULL_BRIGHTNESS = 240;
    private static final double EPSILON = 1.0E-5D;

    public static void renderBlock(JsonBlockModel model, JsonBlockState.Variant variant, RenderBlocks renderBlocks, Block block, int x, int y, int z, int metadata, int brightness, float red, float green, float blue) {
        Tessellator tessellator = Tessellator.instance;
        for (JsonBlockModel.Element element : model.getElements()) {
            renderElement(tessellator, model, variant, renderBlocks, block, null, element, x, y, z, metadata, brightness, red, green, blue, BLOCK_TEXTURE_MAP, false);
        }
    }

    public static boolean renderBlockAsItem(JsonBlockModel model, JsonBlockState.Variant variant, Block block, int metadata, float brightness) {
        return renderBlockAsItem(model, variant, block, metadata, brightness, JsonBlockModel.RenderContext.BLOCK_ITEM);
    }

    public static boolean renderBlockAsItem(JsonBlockModel model, JsonBlockState.Variant variant, Block block, int metadata, float brightness, JsonBlockModel.RenderContext context) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        applyDisplayTransform(model, context, 0.0625F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        Tessellator tessellator = Tessellator.instance;
        int packedBrightness = Math.max(0, Math.min(FULL_BRIGHTNESS, (int) (brightness * FULL_BRIGHTNESS)));
        for (JsonBlockModel.Element element : model.getElements()) {
            renderElement(tessellator, model, variant, null, block, null, element, 0.0F, 0.0F, 0.0F, metadata, packedBrightness, 1.0F, 1.0F, 1.0F, BLOCK_TEXTURE_MAP, true);
        }

        GL11.glPopMatrix();
        return true;
    }

    public static void renderBuiltInEntity(JsonBlockModel model, ItemStack stack, Icon fallbackIcon, JsonBlockModel.RenderContext context, BuiltInEntityRenderer renderer) {
        GL11.glPushMatrix();
        applyDisplayTransform(model, context, 0.0625F);
        renderer.render(stack, context);
        GL11.glPopMatrix();
    }

    public static void renderBuiltInEntityIntoGui(JsonBlockModel model, ItemStack stack, int x, int y, float zLevel, BuiltInEntityRenderer renderer) {
        GL11.glPushMatrix();
        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glTranslatef(x + 8.0F, y + 8.0F, zLevel);
        GL11.glScalef(16.0F, -16.0F, 16.0F);
        applyDisplayTransform(model, JsonBlockModel.RenderContext.GUI, 0.0625F);
        renderer.render(stack, JsonBlockModel.RenderContext.GUI);
        if (!lightingEnabled) {
            GL11.glDisable(GL11.GL_LIGHTING);
        }
        GL11.glPopMatrix();
    }

    public static void renderBuiltInEntityAsBlockItem(JsonBlockModel model, Block block, int metadata, BuiltInEntityRenderer renderer) {
        renderBuiltInEntityAsBlockItem(model, block, metadata, renderer, JsonBlockModel.RenderContext.BLOCK_ITEM);
    }

    public static void renderBuiltInEntityAsBlockItem(JsonBlockModel model, Block block, int metadata, BuiltInEntityRenderer renderer, JsonBlockModel.RenderContext context) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        applyDisplayTransform(model, context, 0.0625F);
        renderer.render(new ItemStack(block, 1, metadata), context);
        GL11.glPopMatrix();
    }

    public static boolean renderItem(JsonBlockModel model, ItemStack stack, Icon fallbackIcon, JsonBlockModel.RenderContext context) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
        GL11.glPushMatrix();
        applyDisplayTransform(model, context, 0.0625F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        Tessellator tessellator = Tessellator.instance;
        for (JsonBlockModel.Element element : model.getElements()) {
            renderElement(tessellator, model, JsonBlockState.EMPTY_VARIANT, null, null, fallbackIcon, element, 0.0F, 0.0F, 0.0F, stack.getItemSubtype(), FULL_BRIGHTNESS, 1.0F, 1.0F, 1.0F, ITEM_TEXTURE_MAP, true);
        }

        GL11.glPopMatrix();
        return true;
    }

    public static boolean renderItemIntoGui(JsonBlockModel model, ItemStack stack, int x, int y, float zLevel) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
        GL11.glPushMatrix();
        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glTranslatef(x + 8.0F, y + 8.0F, zLevel);
        GL11.glScalef(16.0F, -16.0F, 16.0F);
        applyDisplayTransform(model, JsonBlockModel.RenderContext.GUI, 0.0625F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        Tessellator tessellator = Tessellator.instance;
        Icon fallbackIcon = stack.getIconIndex();
        for (JsonBlockModel.Element element : model.getElements()) {
            renderElement(tessellator, model, JsonBlockState.EMPTY_VARIANT, null, null, fallbackIcon, element, 0.0F, 0.0F, 0.0F, stack.getItemSubtype(), FULL_BRIGHTNESS, 1.0F, 1.0F, 1.0F, ITEM_TEXTURE_MAP, true);
        }

        if (!lightingEnabled) {
            GL11.glDisable(GL11.GL_LIGHTING);
        }
        GL11.glPopMatrix();
        return true;
    }

    public static boolean renderBlockAsItemInGui(JsonBlockModel model, JsonBlockState.Variant variant, Block block, int metadata, int x, int y, float zLevel) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glTranslatef(x + 8.0F, y + 8.0F, zLevel);
        GL11.glScalef(16.0F, -16.0F, 16.0F);
        applyDisplayTransform(model, JsonBlockModel.RenderContext.GUI, 0.0625F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        Tessellator tessellator = Tessellator.instance;
        for (JsonBlockModel.Element element : model.getElements()) {
            renderElement(tessellator, model, variant, null, block, null, element, 0.0F, 0.0F, 0.0F, metadata, FULL_BRIGHTNESS, 1.0F, 1.0F, 1.0F, BLOCK_TEXTURE_MAP, true);
        }

        if (!lightingEnabled) {
            GL11.glDisable(GL11.GL_LIGHTING);
        }
        GL11.glPopMatrix();
        return true;
    }

    private static void renderElement(Tessellator tessellator, JsonBlockModel model, JsonBlockState.Variant variant, RenderBlocks renderBlocks, Block block, Icon fallbackIcon, JsonBlockModel.Element element, float offsetX, float offsetY, float offsetZ, int metadata, int brightness, float red, float green, float blue, int textureType, boolean manageDrawing) {
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element, JsonBlockModel.Direction.DOWN, offsetX, offsetY, offsetZ, metadata, brightness, red * 0.5F, green * 0.5F, blue * 0.5F, textureType, manageDrawing);
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element, JsonBlockModel.Direction.UP, offsetX, offsetY, offsetZ, metadata, brightness, red, green, blue, textureType, manageDrawing);
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element, JsonBlockModel.Direction.NORTH, offsetX, offsetY, offsetZ, metadata, brightness, red * 0.8F, green * 0.8F, blue * 0.8F, textureType, manageDrawing);
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element, JsonBlockModel.Direction.SOUTH, offsetX, offsetY, offsetZ, metadata, brightness, red * 0.8F, green * 0.8F, blue * 0.8F, textureType, manageDrawing);
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element, JsonBlockModel.Direction.WEST, offsetX, offsetY, offsetZ, metadata, brightness, red * 0.6F, green * 0.6F, blue * 0.6F, textureType, manageDrawing);
        emitFace(tessellator, model, variant, renderBlocks, block, fallbackIcon, element, JsonBlockModel.Direction.EAST, offsetX, offsetY, offsetZ, metadata, brightness, red * 0.6F, green * 0.6F, blue * 0.6F, textureType, manageDrawing);
    }

    private static void emitFace(Tessellator tessellator, JsonBlockModel model, JsonBlockState.Variant variant, RenderBlocks renderBlocks, Block block, Icon fallbackIcon, JsonBlockModel.Element element, JsonBlockModel.Direction direction, float offsetX, float offsetY, float offsetZ, int metadata, int brightness, float red, float green, float blue, int textureType, boolean manageDrawing) {
        JsonBlockModel.Face face = element.getFace(direction);
        if (face == null) {
            return;
        }
        if (isDegenerateFace(element, direction)) {
            return;
        }

        Icon icon = getIcon(model, face, direction, renderBlocks, block, fallbackIcon, metadata, textureType);
        if (icon == null) {
            return;
        }

        float[] uv = face.getUv(element.getFromX(), element.getFromY(), element.getFromZ(), element.getToX(), element.getToY(), element.getToZ(), direction, model.getTextureWidth(), model.getTextureHeight());
        double[][] vertices = createFaceVertices(element, direction);
        double[][] textureUv = createTextureUv(icon, uv, face.getRotation(), direction, model.getTextureWidth(), model.getTextureHeight());

        boolean startedDrawing = manageDrawing && !tessellator.isDrawing;
        if (startedDrawing) {
            tessellator.startDrawingQuads();
        }

        tessellator.setBrightness(brightness);
        tessellator.setColorOpaque_F(red, green, blue);
        setNormal(tessellator, direction);
        for (int i = 0; i < 4; i++) {
            double[] transformed = transformVertex(vertices[i], element.getRotation(), variant, offsetX, offsetY, offsetZ);
            tessellator.addVertexWithUV(transformed[0], transformed[1], transformed[2], textureUv[i][0], textureUv[i][1]);
        }

        if (startedDrawing) {
            tessellator.draw();
        }
    }

    private static boolean isDegenerateFace(JsonBlockModel.Element element, JsonBlockModel.Direction direction) {
        double width = element.getToX() - element.getFromX();
        double height = element.getToY() - element.getFromY();
        double depth = element.getToZ() - element.getFromZ();
        return switch (direction) {
            case DOWN, UP -> width <= EPSILON || depth <= EPSILON;
            case NORTH, SOUTH -> width <= EPSILON || height <= EPSILON;
            case WEST, EAST -> depth <= EPSILON || height <= EPSILON;
        };
    }

    private static Icon getIcon(JsonBlockModel model, JsonBlockModel.Face face, JsonBlockModel.Direction direction, RenderBlocks renderBlocks, Block block, Icon fallbackIcon, int metadata, int textureType) {
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

    private static double[][] createFaceVertices(JsonBlockModel.Element element, JsonBlockModel.Direction direction) {
        double fromX = element.getFromX();
        double fromY = element.getFromY();
        double fromZ = element.getFromZ();
        double toX = element.getToX();
        double toY = element.getToY();
        double toZ = element.getToZ();

        // Vertex order matches high-version Minecraft / dpyq face winding (CCW when viewed outward)
        return switch (direction) {
            case DOWN -> new double[][]{
                    new double[]{fromX, fromY, toZ},     // V0: minX, minY, maxZ
                    new double[]{fromX, fromY, fromZ},   // V1: minX, minY, minZ
                    new double[]{toX, fromY, fromZ},     // V2: maxX, minY, minZ
                    new double[]{toX, fromY, toZ}};      // V3: maxX, minY, maxZ
            case UP -> new double[][]{
                    new double[]{toX, toY, toZ},         // V0: maxX, maxY, maxZ
                    new double[]{toX, toY, fromZ},       // V1: maxX, maxY, minZ
                    new double[]{fromX, toY, fromZ},     // V2: minX, maxY, minZ
                    new double[]{fromX, toY, toZ}};      // V3: minX, maxY, maxZ
            case NORTH -> new double[][]{
                    new double[]{fromX, toY, fromZ},     // V0: minX, maxY, minZ
                    new double[]{toX, toY, fromZ},       // V1: maxX, maxY, minZ
                    new double[]{toX, fromY, fromZ},     // V2: maxX, minY, minZ
                    new double[]{fromX, fromY, fromZ}};  // V3: minX, minY, minZ
            case SOUTH -> new double[][]{
                    new double[]{fromX, toY, toZ},       // V0: minX, maxY, maxZ
                    new double[]{fromX, fromY, toZ},     // V1: minX, minY, maxZ
                    new double[]{toX, fromY, toZ},       // V2: maxX, minY, maxZ
                    new double[]{toX, toY, toZ}};        // V3: maxX, maxY, maxZ
            case WEST -> new double[][]{
                    new double[]{fromX, toY, toZ},       // V0: minX, maxY, maxZ
                    new double[]{fromX, toY, fromZ},     // V1: minX, maxY, minZ
                    new double[]{fromX, fromY, fromZ},   // V2: minX, minY, minZ
                    new double[]{fromX, fromY, toZ}};    // V3: minX, minY, maxZ
            case EAST -> new double[][]{
                    new double[]{toX, fromY, toZ},       // V0: maxX, minY, maxZ
                    new double[]{toX, fromY, fromZ},     // V1: maxX, minY, minZ
                    new double[]{toX, toY, fromZ},       // V2: maxX, maxY, minZ
                    new double[]{toX, toY, toZ}};        // V3: maxX, maxY, maxZ
        };
    }

    private static double[][] createTextureUv(Icon icon, float[] uv, int rotation, JsonBlockModel.Direction direction, int textureWidth, int textureHeight) {
        double minU = interpolateU(icon, uv[0], 16);
        double minV = interpolateV(icon, uv[1], 16);
        double maxU = interpolateU(icon, uv[2], 16);
        double maxV = interpolateV(icon, uv[3], 16);

        // UV-to-vertex assignments match high-version Minecraft convention
        double[][] textureUv = switch (direction) {
            case DOWN -> new double[][]{{minU, maxV}, {minU, minV}, {maxU, minV}, {maxU, maxV}};
            case UP -> new double[][]{{maxU, maxV}, {maxU, minV}, {minU, minV}, {minU, maxV}};
            case NORTH, WEST -> new double[][]{{maxU, minV}, {minU, minV}, {minU, maxV}, {maxU, maxV}};
            case SOUTH -> new double[][]{{minU, minV}, {minU, maxV}, {maxU, maxV}, {maxU, minV}};
            case EAST -> new double[][]{{minU, maxV}, {maxU, maxV}, {maxU, minV}, {minU, minV}};
        };

        int turns = Math.floorMod(rotation / 90, 4);
        if (turns == 0) {
            return textureUv;
        }

        double[][] rotated = new double[4][2];
        for (int i = 0; i < 4; i++) {
            rotated[i] = textureUv[Math.floorMod(i + turns, 4)];
        }
        return rotated;
    }

    private static double interpolateU(Icon icon, float value, int textureWidth) {
        return icon.getMinU() + (icon.getMaxU() - icon.getMinU()) * (value / textureWidth);
    }

    private static double interpolateV(Icon icon, float value, int textureHeight) {
        return icon.getMinV() + (icon.getMaxV() - icon.getMinV()) * (value / textureHeight);
    }

    private static double[] transformVertex(double[] vertex, JsonBlockModel.Rotation rotation, JsonBlockState.Variant variant, float offsetX, float offsetY, float offsetZ) {
        double[] transformed = new double[]{vertex[0], vertex[1], vertex[2]};
        // Element yaw uses clockwise degrees in source models.
        transformed = rotateModelVertex(transformed, rotation, true);
        if (variant.getX() != 0) {
            transformed = rotateModelVertex(transformed, JsonBlockModel.Rotation.of("x", variant.getX(), 8.0F, 8.0F, 8.0F), false);
        }
        if (variant.getY() != 0) {
            transformed = rotateModelVertex(transformed, JsonBlockModel.Rotation.of("y", variant.getY(), 8.0F, 8.0F, 8.0F), false);
        }
        return new double[]{transformed[0] / 16.0D + offsetX, transformed[1] / 16.0D + offsetY, transformed[2] / 16.0D + offsetZ};
    }

    private static double[] rotateModelVertex(double[] vertex, JsonBlockModel.Rotation rotation, boolean invertY) {
        if (rotation == null || !rotation.isPresent()) {
            return vertex;
        }

        float[] origin = rotation.getOrigin();
        double x = vertex[0] - origin[0];
        double y = vertex[1] - origin[1];
        double z = vertex[2] - origin[2];
        float angle = rotation.getAngle();
        if (invertY && "y".equals(rotation.getAxis())) {
            angle = -angle;
        }
        double radians = Math.toRadians(angle);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        return switch (rotation.getAxis()) {
            case "x" -> new double[]{x + origin[0], (y * cos - z * sin) + origin[1], (y * sin + z * cos) + origin[2]};
            case "y" -> new double[]{(x * cos - z * sin) + origin[0], y + origin[1], (x * sin + z * cos) + origin[2]};
            case "z" -> new double[]{(x * cos - y * sin) + origin[0], (x * sin + y * cos) + origin[1], z + origin[2]};
            default -> vertex;
        };
    }

    private static void setNormal(Tessellator tessellator, JsonBlockModel.Direction direction) {
        switch (direction) {
            case DOWN -> tessellator.setNormal(0.0F, -1.0F, 0.0F);
            case UP -> tessellator.setNormal(0.0F, 1.0F, 0.0F);
            case NORTH -> tessellator.setNormal(0.0F, 0.0F, -1.0F);
            case SOUTH -> tessellator.setNormal(0.0F, 0.0F, 1.0F);
            case WEST -> tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            case EAST -> tessellator.setNormal(1.0F, 0.0F, 0.0F);
        }
    }

    private static void applyDisplayTransform(JsonBlockModel model, JsonBlockModel.RenderContext context, float translationScale) {
        JsonBlockModel.DisplayTransform transform = model.getDisplayTransform(context);
        if (transform == null) {
            if (context == JsonBlockModel.RenderContext.GUI) {
                GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(0.625F, 0.625F, 0.625F);
            }
            return;
        }

        float[] translation = transform.getTranslation();
        GL11.glTranslatef(translation[0] * translationScale, translation[1] * translationScale, translation[2] * translationScale);

        float[] rotation = transform.getRotation();
        GL11.glRotatef(rotation[0], 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(rotation[1], 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotation[2], 0.0F, 0.0F, 1.0F);

        float[] scale = transform.getScale();
        GL11.glScalef(scale[0], scale[1], scale[2]);
    }
}
