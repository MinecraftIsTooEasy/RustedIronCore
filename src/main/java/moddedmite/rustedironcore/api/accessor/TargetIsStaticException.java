package moddedmite.rustedironcore.api.accessor;

public class TargetIsStaticException extends ReflectiveOperationException {
    public TargetIsStaticException(String message) {
        super(message);
    }

    public TargetIsStaticException() {
    }

    public TargetIsStaticException(String message, Throwable cause) {
        super(message, cause);
    }

    public TargetIsStaticException(Throwable cause) {
        super(cause);
    }
}
