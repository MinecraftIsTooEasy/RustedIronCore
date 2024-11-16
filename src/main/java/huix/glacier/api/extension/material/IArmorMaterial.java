package huix.glacier.api.extension.material;

public interface IArmorMaterial {
    int getProtection();

    default int getLossOfChainMail() {
        return 2;
    }
}
