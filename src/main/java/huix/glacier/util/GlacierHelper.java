package huix.glacier.util;

import net.minecraft.ItemArrow;
import net.minecraft.Material;

import java.util.ArrayList;
import java.util.Arrays;

public class GlacierHelper {
    private static int nextCreativeID = 12;
    public static ArrayList<Material> arrow_material_types = new ArrayList<>();


    static {
        arrow_material_types.addAll(Arrays.asList(ItemArrow.material_types));
    }


    public static int getNextCreativeID() {
        return nextCreativeID++;
    }
}
