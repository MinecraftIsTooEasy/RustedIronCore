package huix.glacier.api.extension.material;

import net.minecraft.Item;
import net.minecraft.ItemCoin;
import net.minecraft.ItemNugget;

public interface ICoinMaterial {
    int getExperienceValue();
    ItemCoin getForInstance();
    Item getNuggetPeer();
}
