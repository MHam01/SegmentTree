package exceptions;

public class IllegalArraySizeException extends RuntimeException {
    private static final String template = "Initialization array is of size %d, but should be of size %d!";

    public IllegalArraySizeException(int actual, int expected) {
        super(String.format(template, actual, expected));
    }
}
