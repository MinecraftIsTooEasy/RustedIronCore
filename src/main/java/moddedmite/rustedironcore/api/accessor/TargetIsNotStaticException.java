package moddedmite.rustedironcore.api.accessor;

public class TargetIsNotStaticException extends ReflectiveOperationException {
    public TargetIsNotStaticException(String message) {
        super(message);
    }

    public TargetIsNotStaticException() {
    }

    public TargetIsNotStaticException(String message, Throwable cause) {
        super(message, cause);
    }

    public TargetIsNotStaticException(Throwable cause) {
        super(cause);
    }
}
