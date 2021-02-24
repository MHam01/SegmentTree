public class Segment<T> {
    private final int left, right;

    private T value, lazy;

    public Segment(int left, int right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Integrates lazy value when it's needed.
     *
     * @param operation Operation performed in the tree
     */
    protected void integrate(TreeOperation<T> operation) {
        if(this.lazy == null) return;

        this.value = operation.accept(this.value, this.lazy);
    }

    /**
     * Updates the lazy value in this segment such that operation still holds.
     *
     * @param newLazy New lazy value to integrate
     * @param operation Operation performed in the tree
     */
    protected void updateLazy(T newLazy, TreeOperation<T> operation) {
        if(newLazy == null) return;
        else if(this.lazy == null) {
            this.lazy = newLazy;
            return;
        }

        this.lazy = operation.accept(lazy, newLazy);
    }

    @Override
    public String toString() {
        return String.format("{[%d; %d] -> %s * %s", this.left, this.right, this.value.toString(), this.lazy.toString());
    }

    public int getLowerBound() {
        return left;
    }

    public int getUpperBound() {
        return right;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getLazy() {
        return lazy;
    }

    public void setLazy(T lazy) {
        this.lazy = lazy;
    }
}
