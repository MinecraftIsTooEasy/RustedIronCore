package huix.glacier.api.extension.material;

import net.minecraft.MapColor;
import net.minecraft.Material;

public class GlacierMaterial extends Material {

    private final IEquipmentMaterial material;

    public GlacierMaterial(IEquipmentMaterial material) {
        super(material.getName(), null);
        this.material = material;
        this.setDurability(material.getDurability());
        this.setEnchantability(material.getEnchantability());
        this.setMaxQuality(material.getMaxQuality());
    }

    @Override
    public float getDamageVsEntity() {
        return this.material instanceof IEquipmentMaterial ? this.material.getDamageVsEntity() : super.getDamageVsEntity();
    }

}
