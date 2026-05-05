package moddedmite.rustedironcore.api.event.handler;

import huix.glacier.util.Identifier;
import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IJsonModelListener;
import moddedmite.rustedironcore.api.model.BuiltInEntityRenderer;
import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import moddedmite.rustedironcore.api.model.JsonModelRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Block;
import net.minecraft.Item;

@Environment(EnvType.CLIENT)
public class JsonModelHandler extends AbstractHandler<IJsonModelListener> implements JsonModelRegistry {

    private final JsonModelRegistry registry = JsonBlockModelManager.INSTANCE;

    public void onJsonModelRegister() {
        this.listeners.forEach(listener -> listener.onJsonModelRegister(this));
    }

    @Override
    public void registerBlockModel(Block block, String modelId) {
        this.registry.registerBlockModel(block, modelId);
    }

    @Override
    public void registerBlockModel(Block block, Identifier modelId) {
        this.registry.registerBlockModel(block, modelId);
    }

    @Override
    public void registerBlockModel(Block block, int metadata, String modelId) {
        this.registry.registerBlockModel(block, metadata, modelId);
    }

    @Override
    public void registerBlockModel(Block block, int metadata, Identifier modelId) {
        this.registry.registerBlockModel(block, metadata, modelId);
    }

    @Override
    public void registerBlockState(Block block, String blockStateId) {
        this.registry.registerBlockState(block, blockStateId);
    }

    @Override
    public void registerBlockState(Block block, Identifier blockStateId) {
        this.registry.registerBlockState(block, blockStateId);
    }

    @Override
    public void registerItemModel(Item item, String modelId) {
        this.registry.registerItemModel(item, modelId);
    }

    @Override
    public void registerItemModel(Item item, Identifier modelId) {
        this.registry.registerItemModel(item, modelId);
    }

    @Override
    public void registerItemModel(Item item, int metadata, String modelId) {
        this.registry.registerItemModel(item, metadata, modelId);
    }

    @Override
    public void registerItemModel(Item item, int metadata, Identifier modelId) {
        this.registry.registerItemModel(item, metadata, modelId);
    }

    @Override
    public void registerBuiltInRenderer(Item item, BuiltInEntityRenderer renderer) {
        this.registry.registerBuiltInRenderer(item, renderer);
    }

    @Override
    public void registerBuiltInRenderer(Item item, int metadata, BuiltInEntityRenderer renderer) {
        this.registry.registerBuiltInRenderer(item, metadata, renderer);
    }

    @Override
    public void registerBuiltInRenderer(Block block, BuiltInEntityRenderer renderer) {
        this.registry.registerBuiltInRenderer(block, renderer);
    }
}
