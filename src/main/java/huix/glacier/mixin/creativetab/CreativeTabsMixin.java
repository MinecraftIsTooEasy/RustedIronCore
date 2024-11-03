package huix.glacier.mixin.creativetab;

import com.llamalad7.mixinextras.sugar.Local;
import huix.glacier.api.extension.creativetab.ICreativeTabs;
import net.minecraft.CreativeTabs;
import net.minecraft.Item;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.util.List;

@Mixin( CreativeTabs.class )
public class CreativeTabsMixin implements ICreativeTabs {
    @Shadow
    @Final
    @Mutable
    public static CreativeTabs[] creativeTabArray;
    @Shadow
    @Final
    private int tabIndex;
    @Shadow
    @Final
    private String tabLabel;

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/CreativeTabs;creativeTabArray:[Lnet/minecraft/CreativeTabs;"))
    private CreativeTabs[] redirectInit(@Local(argsOnly = true) int par1) {
        return new CreativeTabs[1024];
    }
//
//    @Inject(method = "<init>", at = @At(value = "RETURN"))
//    private void injectInit(int par1, String par2Str, CallbackInfo ci) {
//        if (par1 >= creativeTabArray.length)
//        {
//            CreativeTabs[] tmp = new CreativeTabs[par1 + 1];
//            for (int x = 0; x < creativeTabArray.length; x++) {
//                tmp[x] = creativeTabArray[x];
//            }
//            creativeTabArray = tmp;
//        }
//
//        creativeTabArray[par1] = ReflectHelper.dyCast(this);
//    }


    @Overwrite
    public int getTabColumn() {
        if (tabIndex > 11) {
            return ((tabIndex - 12) % 10) % 5;
        }
        return this.tabIndex % 6;
    }

    @Overwrite
    public boolean isTabInFirstRow() {
        if (tabIndex > 11) {
            return ((tabIndex - 12) % 10) < 5;
        }
        return this.tabIndex < 6;
    }

    @Overwrite
    public void displayAllReleventItems(List par1List) {
        Item[] itemsList = Item.itemsList;
        for (Item item : itemsList) {
            if (item == null) {
                continue;
            }

            if (item.getCreativeTab() == ReflectHelper.dyCast(this)) {
                item.getSubItems(item.itemID, ReflectHelper.dyCast(this), par1List);
            }
        }

        this.addEnchantmentBooksToList(par1List);
    }

    @Unique
    @Override
    public int getTabPage() {
        if (tabIndex > 11) {
            return ((tabIndex - 12) / 10) + 1;
        }
        return 0;
    }

    @Unique
    @Override
    public boolean hasSearchBar() {
        return tabIndex == CreativeTabs.tabAllSearch.getTabIndex();
    }


    @Shadow
    public Item getTabIconItem() {
        return null;
    }
    @Shadow
    public void addEnchantmentBooksToList(List par1List) {
    }

}
