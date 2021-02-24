public class Segment<T> {
    private final int left, right;

    private T value;

    public Segment(int left, int right) {
        this.left = left;
        this.right = right;
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
}
