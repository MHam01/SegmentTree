package operations;

public interface TreeOperation<T> {
    /**
     * @param lowerBound Lower bound of current segment
     * @param upperBound Upper bound of current segment
     * @param oldVal Old value (mostly value in current node)
     * @param newVal Value to update old value or lazy value to be integrated
     * @return Changed value to be set in current segment
     */
    T accept(int lowerBound, int upperBound, T oldVal, T newVal);
}
