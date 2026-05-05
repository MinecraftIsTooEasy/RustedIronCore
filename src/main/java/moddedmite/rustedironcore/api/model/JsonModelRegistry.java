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

    void registerItemModel(Item item, String modelId);

    void registerItemModel(Item item, Identifier modelId);

    void registerItemModel(Item item, int metadata, String modelId);

    void registerItemModel(Item item, int metadata, Identifier modelId);

    void registerBuiltInRenderer(Item item, BuiltInEntityRenderer renderer);

    void registerBuiltInRenderer(Item item, int metadata, BuiltInEntityRenderer renderer);

    void registerBuiltInRenderer(Block block, BuiltInEntityRenderer renderer);
}
