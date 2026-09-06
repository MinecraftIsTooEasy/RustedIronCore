package moddedmite.rustedironcore.api.model;

import huix.glacier.util.Identifier;
import net.minecraft.Block;
import net.minecraft.Item;

public interface JsonModelRegistry {

    void registerBlockModel(Block block, String modelId);

    void registerBlockModel(Block block, Identifier modelId);

    void registerBlockModel(Block block, int metadata, String modelId);

    void registerBlockModel(Block block, int metadata, Identifier modelId);

    void registerBlockState(Block block, String blockStateId);

    void registerBlockState(Block block, Identifier blockStateId);

    void registerBlockState(Block block, int metadata, String blockStateId);

    void registerBlockState(Block block, int metadata, Identifier blockStateId);

    void registerBlockStateProperties(Block block, JsonBlockStatePropertyResolver resolver);

    void registerItemModel(Item item, String modelId);

    void registerItemModel(Item item, Identifier modelId);

    void registerItemModel(Block block, String modelId);

    void registerItemModel(Block block, Identifier modelId);

    void registerItemModel(Item item, int metadata, String modelId);

    void registerItemModel(Item item, int metadata, Identifier modelId);

    void registerItemModel(Block block, int metadata, String modelId);

    void registerItemModel(Block block, int metadata, Identifier modelId);

    void registerItemModelPredicate(Item item, String property, JsonItemModelPredicate predicate);

    void registerItemModelPredicate(Item item, Identifier property, JsonItemModelPredicate predicate);

    void registerItemModelPredicate(Block block, String property, JsonItemModelPredicate predicate);

    void registerItemModelPredicate(Block block, Identifier property, JsonItemModelPredicate predicate);

    void registerBuiltInRenderer(Item item, BuiltInEntityRenderer renderer);

    void registerBuiltInRenderer(Item item, int metadata, BuiltInEntityRenderer renderer);

    void registerBuiltInRenderer(Block block, BuiltInEntityRenderer renderer);
}
