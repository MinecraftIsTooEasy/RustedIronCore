package moddedmite.rustedironcore.api.item;

import net.minecraft.ItemBow;
import net.minecraft.Material;

@Deprecated(since = "1.3.5")
public class BowItem extends ItemBow {
    private final int velocityBonusPercentage;

    public BowItem(int id, Material reinforcement_material, int maxDamage, int velocityBonusPercentage) {
        super(id, reinforcement_material);
        this.velocityBonusPercentage = velocityBonusPercentage;
        this.setMaxDamage(maxDamage);
    }

    public int velocityBonus() {
        return this.velocityBonusPercentage;
    }
}
