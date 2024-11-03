package huix.glacier.api.extension.item;

import huix.glacier.api.registry.MinecraftRegistry;
import huix.glacier.util.GlacierLog;
import net.minecraft.Item;
import net.minecraft.Material;
import net.xiaoyu233.fml.api.item.IItem;

public class GlacierItem extends Item {

    public GlacierItem(MinecraftRegistry registryInstance, int par1, String texture, int num_subtypes) {
        super(par1, texture, num_subtypes);
        if (registryInstance.autoItemRegister) {
            ((IItem) this).setNamespace(registryInstance.getNameSpace());
        } else {
            GlacierLog.error("Please initAutoItemRegister!");
        }
    }

    public GlacierItem(MinecraftRegistry registryInstance, int id, String texture) {
        this(registryInstance, id, texture, -1);
    }

    public GlacierItem(MinecraftRegistry registryInstance, int id, Material[] material_array, String texture) {
        this(registryInstance, id, texture);
        this.setMaterial(material_array);
    }

    public GlacierItem(MinecraftRegistry registryInstance, int id, Material material, String texture) {
        this(registryInstance, id, texture);
        this.setMaterial(material);
    }
}
