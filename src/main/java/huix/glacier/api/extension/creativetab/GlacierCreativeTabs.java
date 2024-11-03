package huix.glacier.api.extension.creativetab;

import huix.glacier.util.GlacierHelper;
import net.minecraft.CreativeTabs;

import java.util.ArrayList;

public class GlacierCreativeTabs extends CreativeTabs {
    public static final ArrayList<CreativeTabs> newCreativeTabArray = new ArrayList<>();

    public GlacierCreativeTabs(String par2Str) {
        super(GlacierHelper.getNextCreativeID(), par2Str);
        newCreativeTabArray.add(this);
    }

    static {
        newCreativeTabArray.add(CreativeTabs.tabBlock);
        newCreativeTabArray.add(CreativeTabs.tabDecorations);
        newCreativeTabArray.add(CreativeTabs.tabRedstone);
        newCreativeTabArray.add(CreativeTabs.tabTransport);
        newCreativeTabArray.add(CreativeTabs.tabMisc);
        newCreativeTabArray.add(CreativeTabs.tabAllSearch);
        newCreativeTabArray.add(CreativeTabs.tabFood);
        newCreativeTabArray.add(CreativeTabs.tabTools);
        newCreativeTabArray.add(CreativeTabs.tabCombat);
        newCreativeTabArray.add(CreativeTabs.tabBrewing);
        newCreativeTabArray.add(CreativeTabs.tabMaterials);
        newCreativeTabArray.add(CreativeTabs.tabInventory);
    }
}
