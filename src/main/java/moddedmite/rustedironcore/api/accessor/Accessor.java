package moddedmite.rustedironcore.api.accessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.*;

public class Accessor {
    private static final Logger logger = LogManager.getLogger("Accessor");

    private Accessor() {
        try {
            throw new Throwable("你怎么回事，为什么要创建Accessor的实例？");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, Y> T modify(@Nonnull Field field, @Nonnull T t, @Nonnull Y y) {
        try {
            if (Modifier.isFinal(field.getModifiers())) {
                Field declaredField = Field.class.getDeclaredField("modifiers");
                declaredField.setAccessible(true);
                declaredField.setInt(field, field.getModifiers() & (-17));
            }
            field.setAccessible(true);
            field.set(y, t);
            return (T) field.get(t);
        } catch (ClassCastException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e2) {
            throw new RuntimeException("Accessor：输入的值" + t + "无效", e2);
        } catch (NoSuchFieldException e3) {
            throw new RuntimeException("Accessor：找不到" + field.getName(), e3);
        }
    }

    public static <T> T modifyStatic(@Nonnull Field field, @Nonnull T t) {
        try {
            Field declaredField = Field.class.getDeclaredField("modifiers");
            declaredField.setAccessible(true);
            declaredField.setInt(field, field.getModifiers() & (-17));
            field.setAccessible(true);
            field.set(null, t);
            return (T) field.get(t);
        } catch (ClassCastException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e2) {
            throw new RuntimeException("Accessor：输入的值" + t + "无效", e2);
        }
    }

    public static <T, Y> Y access(@Nonnull Field field, @Nonnull T t) throws TargetIsStaticException {
        if (!Modifier.isStatic(field.getModifiers())) {
            try {
                field.setAccessible(true);
                return (Y) field.get(t);
            } catch (ClassCastException | IllegalAccessException | SecurityException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e2) {
                throw new RuntimeException("Accessor：你最好不是乱输的" + t, e2);
            }
        }
        throw new TargetIsStaticException("Accessor：成员" + field.getName() + "是非静态的，不能以静态形式访问！");
    }

    public static <T> T accessStatic(@Nonnull Field field) throws TargetIsNotStaticException {
        if (Modifier.isStatic(field.getModifiers())) {
            try {
                field.setAccessible(true);
                return (T) field.get(null);
            } catch (ClassCastException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
        throw new TargetIsNotStaticException("Accessor：成员" + field.getName() + "不是静态的！");
    }

    public static <T> T createInstance(@Nonnull Class<T> clazz, @Nullable Object... args) {
        try {
            Class[] parameterTypes = new Class[args.length];
            int i = 0;
            for (Object object : args) {
                parameterTypes[i] = object.getClass();
                i++;
            }
            Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e2) {
            throw new RuntimeException("Accessor：你要不检查检查你传入的参数？或者使用另一个createInstant()？", e2);
        }
    }

    public static <T> T createInstance(@Nonnull Class<T> clazz, @Nonnull Class[] types, @Nonnull Object[] args) {
        try {
            if (types.length != args.length) {
                throw new IllegalArgumentException("Accessor：参数类型数量和参数数量不一致！");
            }
            Constructor<T> constructor = clazz.getDeclaredConstructor(types);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Accessor：快去告诉锈铁锭他Accessor有问题", e);
        } catch (InstantiationException | InvocationTargetException e2) {
            throw new RuntimeException(e2);
        } catch (NoSuchMethodException e3) {
            throw new RuntimeException("Accessor：你要不检查检查你传入的参数类型？", e3);
        }
    }

    public static <T, Y> Y invoke(@Nonnull Method method, @Nonnull T t, @Nullable Object... objArr) throws TargetIsStaticException {
        if (!Modifier.isStatic(method.getModifiers()) || t == null) {
            try {
                method.setAccessible(true);
                return (Y) method.invoke(t, objArr);
            } catch (IllegalAccessException | NullPointerException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        throw new TargetIsStaticException("Accessor：不能以静态方式访问非静态方法" + method.getName());
    }

    public static <T> T invokeStatic(@Nonnull Method method, @Nullable Object... objArr) throws TargetIsNotStaticException {
        if (Modifier.isStatic(method.getModifiers())) {
            try {
                method.setAccessible(true);
                return (T) method.invoke(null, objArr);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e2) {
                throw new RuntimeException("Accessor：需要实例！", e2);
            }
        }
        throw new TargetIsNotStaticException("Accessor：方法" + method.getName() + "不是静态的！");
    }

    public static <T> Class<T> accessClass(@Nonnull String str) {
        try {
            return (Class<T>) Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Accessor：要不再考虑清楚？", e);
        }
    }

    public static <T, Y> Class<Y> accessInnerClass(@Nonnull Class<T> cls, @Nonnull String str) {
        try {
            return (Class<Y>) Class.forName(cls.getName() + "$" + str);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
