package moddedmite.rustedironcore.internal.unsafe;

import net.minecraft.CraftingResult;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ExperimentalReleaseUtil {
    private static int miteRelease;
    public static final int[] normal_releases = new int[] {153, 155, 156, 157, 162, 163, 167, 168, 171, 173, 174, 179, 180, 181, 183, 186, 187, 190, 191, 194, 195};

    public static boolean isExperimental() {
        return Arrays.stream(normal_releases).noneMatch(i -> i == miteRelease);
    }

    public static boolean isRepairResult(CraftingResult result) {
        if (result == null) return false;
        try {
            Method method = result.getClass().getMethod("isRepair");
            return (Boolean) method.invoke(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    static {
        try {
            byte[] data = IOUtils.toByteArray(ExperimentalReleaseUtil.class.getResourceAsStream("/net/minecraft/Minecraft.class"));
            final int[] version = new int[1];
            ClassReader cr = new ClassReader(data);
            cr.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    if ("MITE_release_number".equals(name))
                        version[0] = (int) value;
                    return super.visitField(access, name, descriptor, signature, value);
                }
            }, ClassReader.EXPAND_FRAMES);
            miteRelease = version[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
