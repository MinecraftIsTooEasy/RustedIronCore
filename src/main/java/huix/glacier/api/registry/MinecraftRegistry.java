package huix.glacier.api.registry;


import net.minecraft.*;
import net.xiaoyu233.fml.api.block.IBlock;
import net.xiaoyu233.fml.api.item.IItem;

import java.util.HashMap;

public class MinecraftRegistry implements IRegistryInstance {

    public static MinecraftRegistry instance;

    private final String mod_name;


    public MinecraftRegistry(String modName) {
        instance = this;
        this.mod_name = modName;
    }

    public HashMap<Integer, Integer> itemHeatLevelMap = new HashMap<>();

    /**
     * Use {@link moddedmite.rustedironcore.property.ItemProperties} instead.
     */
    @Deprecated(since = "1.3.9")
    public void registerItemHeatLevel(int itemId, int heatLevel) {
        this.itemHeatLevelMap.put(itemId, heatLevel);
    }


    public void registerBlockAnvil(BlockAnvil block, String resourceLocation) {
        this.registerBlockAnvil(block, resourceLocation, resourceLocation);
    }

    public void registerBlockAnvil(BlockAnvil block, String resource, String name) {
        block.setUnlocalizedName(name);
        if (!((IBlock) block).hasNamespaceSet()) {
            ((IBlock) block).setNamespace(this.mod_name);
        }

        ((IBlock) block).setBlockTextureName(resource);
        Item item = (new ItemAnvilBlock(block)).setUnlocalizedName(name);
        item.setMaxStackSize(block.getItemStackLimit());
        ((IItem) item).setNamespace(this.mod_name);
    }

    public void registerBlock(Block block, String resourceLocation, String name) {
        block.setUnlocalizedName(name);
        if (!((IBlock) block).hasNamespaceSet()) {
            ((IBlock) block).setNamespace(this.mod_name);
        }

        ((IBlock) block).setBlockTextureName(resourceLocation);
        Item item = (new ItemBlock(block)).setUnlocalizedName(name);
        item.setMaxStackSize(block.getItemStackLimit());
        ((IItem) item).setNamespace(this.mod_name);
    }

    public void registerBlock(Block block, String resourceLocation) {
        this.registerBlock(block, resourceLocation, resourceLocation);
    }

    public Item registerItem(String resource, String name, Item item) {
        String prefix = ((IItem) item).getTexturePrefix();
        ((IItem) item).setItemTextureName(prefix + resource);
        item.setUnlocalizedName(name);
        ((IItem) item).setNamespace(this.mod_name);
        return item;
    }

    public Item registerItem(String resource, Item item) {
        return this.registerItem(resource, resource, item);
    }

    @Override
    public String getNameSpace() {
        return this.mod_name;
    }

    public boolean autoItemRegister;

    public MinecraftRegistry initAutoItemRegister() {
        this.autoItemRegister = true;
        return this;
    }
}
