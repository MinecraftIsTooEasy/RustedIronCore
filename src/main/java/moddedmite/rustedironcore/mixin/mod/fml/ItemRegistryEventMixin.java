package moddedmite.rustedironcore.mixin.mod.fml;

import net.minecraft.Block;
import net.minecraft.BlockAnvil;
import net.minecraft.Item;
import net.xiaoyu233.fml.reload.event.ItemRegistryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRegistryEvent.class)
public class ItemRegistryEventMixin {
    // TODO make them in fml; fml made Block.init create these
    // These may cause MappedRegistry.unregisteredIntrusiveHolders to be not empty when registry freezes
    @Inject(
            method = "registerItemBlock(Ljava/lang/String;Lnet/minecraft/Block;)V",
            at = @At(value = "NEW", target = "(Lnet/minecraft/Block;)Lnet/minecraft/ItemBlock;"),
            cancellable = true
    )
    private void removeDuplication(String namespace, Block block, CallbackInfo ci) {
        if (Item.getItem(block) != null) ci.cancel();
    }

    @Inject(
            method = "registerItemBlock(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/Block;)V",
            at = @At(value = "NEW", target = "(Lnet/minecraft/Block;)Lnet/minecraft/ItemBlock;"),
            cancellable = true
    )
    private void removeDuplication(String namespace, String resourceLocation, Block block, CallbackInfo ci) {
        if (Item.getItem(block) != null) ci.cancel();
    }

    @Inject(
            method = "registerItemBlock(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/Block;)V",
            at = @At(value = "NEW", target = "(Lnet/minecraft/Block;)Lnet/minecraft/ItemBlock;"),
            cancellable = true
    )
    private void removeDuplication(String namespace, String resourceLocation, String unlocalizedName, Block block, CallbackInfo ci) {
        if (Item.getItem(block) != null) ci.cancel();
    }

    @Inject(
            method = "registerAnvil(Ljava/lang/String;Lnet/minecraft/BlockAnvil;)V",
            at = @At(value = "NEW", target = "(Lnet/minecraft/BlockAnvil;)Lnet/minecraft/ItemAnvilBlock;"),
            cancellable = true
    )
    private void removeDuplication(String namespace, BlockAnvil blockAnvil, CallbackInfo ci) {
        if (Item.getItem(blockAnvil) != null) ci.cancel();
    }

    @Inject(
            method = "registerAnvil(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/BlockAnvil;)V",
            at = @At(value = "NEW", target = "(Lnet/minecraft/BlockAnvil;)Lnet/minecraft/ItemAnvilBlock;"),
            cancellable = true
    )
    private void removeDuplication(String namespace, String resourceLocation, BlockAnvil blockAnvil, CallbackInfo ci) {
        if (Item.getItem(blockAnvil) != null) ci.cancel();
    }

    @Inject(
            method = "registerAnvil(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/BlockAnvil;)V",
            at = @At(value = "NEW", target = "(Lnet/minecraft/BlockAnvil;)Lnet/minecraft/ItemAnvilBlock;"),
            cancellable = true
    )
    private void removeDuplication(String namespace, String resourceLocation, String unlocalizedName, BlockAnvil blockAnvil, CallbackInfo ci) {
        if (Item.getItem(blockAnvil) != null) ci.cancel();
    }

}
