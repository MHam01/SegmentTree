import exceptions.IllegalArraySizeException;
import exceptions.IllegalIntervalException;

import java.util.Arrays;

public class SegmentTree<T> {
    private final int offset, height;
    private final Segment<T>[] tree, leaves;

    private TreeOperation<T> operation;

    /**
     * Creates a new Segment Tree which performs changes using the given operation.
     *
     * @param left Global lower bound
     * @param right Global upper bound
     * @param initValue Value with which every leaf is initialized
     * @param operation Operation performed when a value in a range changes
     */
    @SuppressWarnings("unchecked")
    public SegmentTree(int left, int right, T initValue, TreeOperation<T> operation) {
        this(left, right, (T[]) new Object[]{initValue}, operation);
    }

    /**
     * Creates a new Segment Tree which performs changes using the given operation.
     *
     * @param left Global lower bound
     * @param right Global upper bound
     * @param leaves Values with which the according leaf is initialized
     * @param operation Operation performed when a value in a range changes
     */
    @SuppressWarnings("unchecked")
    public SegmentTree(int left, int right, T[] leaves, TreeOperation<T> operation) {
        if(right > left) throw new IllegalIntervalException(left, right);

        final int segmLen = right - left + 1;

        if(leaves.length == 1 && left != right) {
            final T initVal = leaves[0];
            leaves = (T[]) new Object[segmLen];
            Arrays.fill(leaves, initVal);
        } else if(leaves.length != segmLen)
            throw new IllegalArraySizeException(leaves.length, segmLen);

        this.offset = left;
        this.operation = operation;

        this.height = (int) Math.ceil(Math.log10(segmLen) / Math.log10(2));

        this.tree = new Segment[(int) Math.pow(2, this.height + 1)];
        this.leaves = new Segment[segmLen];

        build(leaves, 1, left, right);
    }

    /**
     * Builds a new segment tree.
     *
     * @param leaves Values with which the according leaf is initialized
     * @param ind Position of the current interval in the tree
     * @param left Lower bound for current interval
     * @param right Upper bound for current interval
     * @return Value to be set in the parent node
     */
    private T build(final T[] leaves, int ind, int left, int right) {
        final Segment<T> curr = this.tree[ind] = new Segment<>(left, right);

        if(left == right) {
            curr.setValue(leaves[left - this.offset]);
            this.leaves[left - this.offset] = curr;
            return curr.getValue();
        }

        final int mid = (left + right) / 2;

        final T res1 = build(leaves, 2 * ind, left, mid);
        final T res2 = build(leaves, 2 * ind + 1, mid + 1, right);

        curr.setValue(operation.accept(res1, res2));
        return curr.getValue();
    }

    /**
     * Performs changes to a single leaf in the tree given the specified operation.
     *
     * @param leaf Leaf to be changed
     * @param value Value to be passed down to the specified leaf node
     */
    public void singleChg(int leaf, T value) {
        singleChg(leaf + this.offset, value, 1);
    }

    /**
     * Auxiliary function to traverse the tree.
     *
     * @param leaf Leaf to be changed
     * @param value Value to be passed down to the specified leaf node
     * @param ind Position of the current interval in the tree
     */
    private void singleChg(int leaf, T value, int ind) {
        final Segment<T> curr = this.tree[ind];

        if(leaf < curr.getLowerBound() || leaf > curr.getUpperBound())
            return;

        curr.setValue(this.operation.accept(curr.getValue(), value));

        if(curr.getLowerBound() != curr.getUpperBound()) {
            singleChg(leaf, value, 2 * ind);
            singleChg(leaf, value, 2 * ind + 1);
        }
    }

    public Segment<T>[] getLeaves() {
        return leaves;
    }

    public void setOperation(TreeOperation<T> operation) {
        this.operation = operation;
    }
}
