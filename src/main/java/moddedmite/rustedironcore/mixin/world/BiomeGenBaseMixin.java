package moddedmite.rustedironcore.mixin.world;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.BiomeDecorationRegisterEvent;
import moddedmite.rustedironcore.api.event.events.OreGenerationRegisterEvent;
import moddedmite.rustedironcore.api.world.BiomeAPI;
import net.minecraft.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeGenBase.class)
public class BiomeGenBaseMixin implements BiomeAPI {
    @Unique
    public String biomeUnlocalizedName;

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void onStaticHead(CallbackInfo ci) {
        BiomeDecorationRegisterEvent biomeDecorationRegisterEvent = new BiomeDecorationRegisterEvent();
        Handlers.BiomeDecoration.publish(biomeDecorationRegisterEvent);
        Handlers.BiomeDecoration.buildMap(biomeDecorationRegisterEvent.getMap());
        Handlers.OreGeneration.publish(new OreGenerationRegisterEvent(Handlers.OreGeneration));
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onStaticTail(CallbackInfo ci) {
        ((BiomeAPI) BiomeGenBase.ocean).setBiomeUnlocalizedName("ocean");
        ((BiomeAPI) BiomeGenBase.plains).setBiomeUnlocalizedName("plains");
        ((BiomeAPI) BiomeGenBase.desert).setBiomeUnlocalizedName("desert");
        ((BiomeAPI) BiomeGenBase.extremeHills).setBiomeUnlocalizedName("extremeHills");
        ((BiomeAPI) BiomeGenBase.forest).setBiomeUnlocalizedName("forest");
        ((BiomeAPI) BiomeGenBase.taiga).setBiomeUnlocalizedName("taiga");
        ((BiomeAPI) BiomeGenBase.swampland).setBiomeUnlocalizedName("swampland");
        ((BiomeAPI) BiomeGenBase.river).setBiomeUnlocalizedName("river");
        ((BiomeAPI) BiomeGenBase.hell).setBiomeUnlocalizedName("hell");
        ((BiomeAPI) BiomeGenBase.underworld).setBiomeUnlocalizedName("underworld");
        ((BiomeAPI) BiomeGenBase.sky).setBiomeUnlocalizedName("sky");
        ((BiomeAPI) BiomeGenBase.frozenOcean).setBiomeUnlocalizedName("frozenOcean");
        ((BiomeAPI) BiomeGenBase.frozenRiver).setBiomeUnlocalizedName("frozenRiver");
        ((BiomeAPI) BiomeGenBase.icePlains).setBiomeUnlocalizedName("icePlains");
        ((BiomeAPI) BiomeGenBase.iceMountains).setBiomeUnlocalizedName("iceMountains");
        ((BiomeAPI) BiomeGenBase.beach).setBiomeUnlocalizedName("beach");
        ((BiomeAPI) BiomeGenBase.desertHills).setBiomeUnlocalizedName("desertHills");
        ((BiomeAPI) BiomeGenBase.forestHills).setBiomeUnlocalizedName("forestHills");
        ((BiomeAPI) BiomeGenBase.taigaHills).setBiomeUnlocalizedName("taigaHills");
        ((BiomeAPI) BiomeGenBase.extremeHillsEdge).setBiomeUnlocalizedName("extremeHillsEdge");
        ((BiomeAPI) BiomeGenBase.jungle).setBiomeUnlocalizedName("jungle");
        ((BiomeAPI) BiomeGenBase.jungleHills).setBiomeUnlocalizedName("jungleHills");
        ((BiomeAPI) BiomeGenBase.desertRiver).setBiomeUnlocalizedName("desertRiver");
        ((BiomeAPI) BiomeGenBase.jungleRiver).setBiomeUnlocalizedName("jungleRiver");
        ((BiomeAPI) BiomeGenBase.swampRiver).setBiomeUnlocalizedName("swampRiver");
    }

    @Override
    public String setBiomeUnlocalizedName(String unlocalizedName) {
        return this.biomeUnlocalizedName = unlocalizedName;
    }

    @Override
    public String getBiomeUnlocalizedName() {
        return "biome." + this.biomeUnlocalizedName + ".name";
    }
}
