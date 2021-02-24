public interface TreeOperation<T> {
    /**
     * @param val1 Value in the current segment
     * @param val2 Changed value inherited by children
     * @return New value to be set in current segment
     */
    T accept(T val1, T val2);
}
