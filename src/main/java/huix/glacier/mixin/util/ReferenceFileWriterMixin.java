package huix.glacier.mixin.util;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.FileWriter;

@Mixin( ReferenceFileWriter.class )
public class ReferenceFileWriterMixin {

    @Shadow
    private static String newline;

    @Overwrite
    private static void writeItemReachFile(File dir) throws Exception {
        FileWriter fw = new FileWriter(dir.getPath() + "/item_reach.txt");
        StringBuilder sb = new StringBuilder();
        sb.append("The player has a base reach of " + StringHelper.formatFloat(2.75F, 1, 2) + " vs blocks and " + StringHelper.formatFloat(1.5F, 1, 2) + " vs entities." + newline + newline);
        sb.append("Only items that have a reach bonus are listed." + newline + newline);
        sb.append("Reach Bonus" + newline);
        sb.append("-----------" + newline);

        for(int i = 0; i < Item.itemsList.length; ++i) {
            Item item = Item.getItem(i);
            if (item != null) {
                String name = item.getNameForReferenceFile();
                if (item instanceof ItemTool reach_bonus) {
                    if (reach_bonus.getToolMaterial() != Material.iron && !(reach_bonus instanceof ItemCudgel))
                        continue;
                    name = (reach_bonus.getToolMaterial()).name;
                }
                float var7 = item.getReachBonus();
                if (var7 > 0.0F) {
                    sb.append("Item[").append(i).append("] ");
                    sb.append(name).append(": +").append(StringHelper.formatFloat(var7, 1, 3));
                    sb.append(newline);
                }
            }
        }

        fw.write(sb.toString());
        fw.close();
    }
}
