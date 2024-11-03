package huix.glacier.api.extension.creativetab;

public interface ICreativeTabs {

    default int getTabPage() {
        return 0;
    }

    default boolean hasSearchBar() {
        return false;
    }
}
