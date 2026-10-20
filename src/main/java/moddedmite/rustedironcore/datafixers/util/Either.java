package moddedmite.rustedironcore.datafixers.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Either<L, R> /*implements App<Mu<R>, L>*/ {
//    public static <L, R> Either<L, R> unbox(App<Mu<R>, L> box) {
//        return (Either)box;
//    }

    private Either() {
    }

    public abstract <C, D> Either<C, D> mapBoth(Function<? super L, ? extends C> var1, Function<? super R, ? extends D> var2);

    public abstract <T> T map(Function<? super L, ? extends T> var1, Function<? super R, ? extends T> var2);

    public abstract Either<L, R> ifLeft(Consumer<? super L> var1);

    public abstract Either<L, R> ifRight(Consumer<? super R> var1);

    public abstract Optional<L> left();

    public abstract Optional<R> right();

    public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> l) {
        return (Either) this.map((t) -> left(l.apply(t)), Either::right);
    }

    public <T> Either<L, T> mapRight(Function<? super R, ? extends T> l) {
        return (Either) this.map(Either::left, (t) -> right(l.apply(t)));
    }

    public static <L, R> Either<L, R> left(L value) {
        return new Left<L, R>(value);
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Right<L, R>(value);
    }

    public L orThrow() {
        return (L) this.map((l) -> l, (r) -> {
            if (r instanceof Throwable) {
                throw new RuntimeException((Throwable) r);
            } else {
                throw new RuntimeException(r.toString());
            }
        });
    }

    public Either<R, L> swap() {
        return (Either) this.map(Either::right, Either::left);
    }

    public <L2> Either<L2, R> flatMap(Function<L, Either<L2, R>> function) {
        return (Either) this.map(function, Either::right);
    }

    public static <U> U unwrap(Either<? extends U, ? extends U> either) {
        return (U) either.map(Function.identity(), Function.identity());
    }

//    public static final class Mu<R> implements K1 {
//    }

    private static final class Left<L, R> extends Either<L, R> {
        private final L value;

        public Left(L value) {
            this.value = value;
        }

        public <C, D> Either<C, D> mapBoth(Function<? super L, ? extends C> f1, Function<? super R, ? extends D> f2) {
            return new Left<C, D>(f1.apply(this.value));
        }

        public <T> T map(Function<? super L, ? extends T> l, Function<? super R, ? extends T> r) {
            return (T) l.apply(this.value);
        }

        public Either<L, R> ifLeft(Consumer<? super L> consumer) {
            consumer.accept(this.value);
            return this;
        }

        public Either<L, R> ifRight(Consumer<? super R> consumer) {
            return this;
        }

        public Optional<L> left() {
            return Optional.of(this.value);
        }

        public Optional<R> right() {
            return Optional.empty();
        }

        public String toString() {
            return "Left[" + String.valueOf(this.value) + "]";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                Left<?, ?> left = (Left) o;
                return Objects.equals(this.value, left.value);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.value.hashCode();
        }
    }

    private static final class Right<L, R> extends Either<L, R> {
        private final R value;

        public Right(R value) {
            this.value = value;
        }

        public <C, D> Either<C, D> mapBoth(Function<? super L, ? extends C> f1, Function<? super R, ? extends D> f2) {
            return new Right<C, D>(f2.apply(this.value));
        }

        public <T> T map(Function<? super L, ? extends T> l, Function<? super R, ? extends T> r) {
            return (T) r.apply(this.value);
        }

        public Either<L, R> ifLeft(Consumer<? super L> consumer) {
            return this;
        }

        public Either<L, R> ifRight(Consumer<? super R> consumer) {
            consumer.accept(this.value);
            return this;
        }

        public Optional<L> left() {
            return Optional.empty();
        }

        public Optional<R> right() {
            return Optional.of(this.value);
        }

        public String toString() {
            return "Right[" + String.valueOf(this.value) + "]";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                Right<?, ?> right = (Right) o;
                return Objects.equals(this.value, right.value);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.value.hashCode();
        }
    }

//    public static final class Instance<R2> implements Applicative<Mu<R2>, Mu<R2>>, Traversable<Mu<R2>, Mu<R2>>, CocartesianLike<Mu<R2>, R2, Mu<R2>> {
//        public <T, R> App<Mu<R2>, R> map(Function<? super T, ? extends R> func, App<Mu<R2>, T> ts) {
//            return Either.<Mu<R2>, R>unbox(ts).mapLeft(func);
//        }
//
//        public <A> App<Mu<R2>, A> point(A a) {
//            return Either.<Mu<R2>, A>left(a);
//        }
//
//        public <A, R> Function<App<Mu<R2>, A>, App<Mu<R2>, R>> lift1(App<Mu<R2>, Function<A, R>> function) {
//            return (a) -> Either.unbox(function).flatMap((f) -> Either.unbox(a).mapLeft(f));
//        }
//
//        public <A, B, R> BiFunction<App<Mu<R2>, A>, App<Mu<R2>, B>, App<Mu<R2>, R>> lift2(App<Mu<R2>, BiFunction<A, B, R>> function) {
//            return (a, b) -> Either.unbox(function).flatMap((f) -> Either.unbox(a).flatMap((av) -> Either.unbox(b).mapLeft((bv) -> f.apply(av, bv))));
//        }
//
//        public <F extends K1, A, B> App<F, App<Mu<R2>, B>> traverse(Applicative<F, ?> applicative, Function<A, App<F, B>> function, App<Mu<R2>, A> input) {
//            return (App)Either.<F, App<Mu<R2>, B>>unbox(input).map((l) -> {
//                App<F, B> b = (App)function.apply(l);
//                return applicative.ap(Either::left, b);
//            }, (r) -> applicative.point(Either.right(r)));
//        }
//
//        public <A> App<Mu<R2>, A> to(App<Mu<R2>, A> input) {
//            return input;
//        }
//
//        public <A> App<Mu<R2>, A> from(App<Mu<R2>, A> input) {
//            return input;
//        }
//
//        public static final class Mu<R2> implements Applicative.Mu, Traversable.Mu, CocartesianLike.Mu {
//        }
//    }
}
