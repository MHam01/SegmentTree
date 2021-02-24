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

    protected void updateLazy(T old, TreeOperation<T> operation) {
        if(old == null) return;
        else if(this.lazy == null) {
            this.lazy = old;
            return;
        }

        this.lazy = operation.accept(lazy, old);
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
