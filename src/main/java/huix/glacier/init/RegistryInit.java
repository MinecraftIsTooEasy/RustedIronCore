package huix.glacier.init;

import huix.glacier.api.extension.creativetab.GlacierCreativeTabs;
import huix.glacier.api.registry.MinecraftRegistry;
import huix.glacier.api.entrypoint.IGameRegistry;
import huix.glacier.api.registry.RegistryIds;
import huix.glacier.api.registry.sync.RegistryHelper;
import huix.glacier.util.Identifier;
import net.minecraft.*;
import net.xiaoyu233.fml.reload.utils.IdUtil;

public class RegistryInit implements IGameRegistry {
    public static final MinecraftRegistry registry = new MinecraftRegistry("glacier").initAutoItemRegister();

//    CreativeTabs test = new GlacierCreativeTabs("glacier");
//    Item coin = new Item(5001, "test").setCreativeTab(test);
//
    @Override
    public void onGameRegistry() {
//        RegistryHelper.registerItem(coin, new Identifier("glacier", "coin"));
    }
}
