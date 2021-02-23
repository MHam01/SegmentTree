public class Segment<T> {
    private final int LEFT, RIGHT;

    private T value;

    public Segment(int left, int right) {
        this.LEFT = left;
        this.RIGHT = right;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
