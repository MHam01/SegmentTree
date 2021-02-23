package exceptions;

public class IllegalIntervalException extends RuntimeException {
    private static final String template = "Starting value %d greater than ending value %d!";

    public IllegalIntervalException(int from, int to) {
        super(String.format(template, from, to));
    }
}
