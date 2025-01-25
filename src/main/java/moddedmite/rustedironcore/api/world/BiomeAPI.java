package moddedmite.rustedironcore.api.world;

public interface BiomeAPI {
    default String getBiomeUnlocalizedName(){return null;}
    default String setBiomeUnlocalizedName(String unlocalizedName){return null;}
}
