package huix.glacier.api.extension.material;

import net.minecraft.EnumQuality;

public interface IEquipmentMaterial {

    String getName();
    float getDurability();
    int getEnchantability();
    EnumQuality getMaxQuality();
    float getDamageVsEntity();
}
