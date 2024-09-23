package moddedmite.rustedironcore.api.util;

import moddedmite.rustedironcore.api.interfaces.IPotion;
import net.minecraft.Potion;
import net.minecraft.ResourceLocation;

public class PotionExtend extends Potion implements IPotion {
    private ResourceLocation texture;
    private boolean individualTexture = false;

    public PotionExtend(int id, boolean isBadEffect, int effectiveness) {
        super(id, isBadEffect, effectiveness);
    }

    public PotionExtend(int id, boolean isBadEffect, int effectiveness, ResourceLocation texturePath) {
        super(id, isBadEffect, effectiveness);
        this.texture = texturePath;
        this.individualTexture = true;
    }

    @Override
    public boolean ric$UsesIndividualTexture() {
        return this.individualTexture;
    }

    @Override
    public ResourceLocation ric$GetTexture() {
        return this.texture;
    }

}
