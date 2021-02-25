package exceptions;

public class FixedOperationException extends RuntimeException {
    public FixedOperationException() {
        super("Propagation operation may only be set once!");
    }
}
