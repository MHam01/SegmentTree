import exceptions.IllegalArraySizeException;
import exceptions.IllegalIntervalException;

import java.util.Arrays;
import java.util.Optional;

public class SegmentTree<T> {
    private final int offset, height;
    private final Segment<T>[] tree, leaves;
    private final TreeOperation<T> operation;

    //Can be used to speed up lazy propagation, should represent n * operation
    private TreeOperation<T> propagateOp;

    /**
     * Creates a new Segment Tree which performs changes using the given operation.
     * Builds in O(n) time.
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
     * Builds in O(n) time.
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
     * @param ind Position of the current segment in the tree
     * @param left Lower bound of current segment
     * @param right Upper bound of current segment
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

        final T leftSubTr = build(leaves, 2 * ind, left, mid);
        final T rightSubTr = build(leaves, 2 * ind + 1, mid + 1, right);

        curr.setValue(operation.accept(leftSubTr, rightSubTr));
        return curr.getValue();
    }

    /**
     * Performs changes to a single leaf in the tree given the specified operation.
     * Operates in O(log n) time.
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
     * @param ind Position of the current segment in the tree
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

    /**
     * Perform changes to a given range given the specified operation.
     * Operates in O(log n) time.
     *
     * @param left Lower bound for the change
     * @param right Upper bound for the change
     * @param value Value to be passed down the tree to all leaf nodes in the range
     */
    public void rangeChg(int left, int right, T value) {
        rangeChg(left, right, value, 1);
    }

    /**
     * Auxiliary method to traverse the tree.
     *
     * @param left Lower bound for the change
     * @param right Upper bound for the change
     * @param value Value to be passed down the tree to all leaf nodes in the range
     * @param ind Position of the current segment in the tree
     */
    private void rangeChg(int left, int right, T value, int ind) {
        final Segment<T> curr = this.tree[ind];

        if(left > curr.getUpperBound() || curr.getLowerBound() > right)
            return;

        if(left <= curr.getLowerBound() && right >= curr.getUpperBound()) {
            curr.updateLazy(value, this.operation);
            propagate(ind);
        } else if(curr.getLowerBound() != curr.getUpperBound()) {
            rangeChg(left, right, value, 2 * ind);
            rangeChg(left, right, value, 2 * ind + 1);

            curr.setValue(this.operation.accept(this.tree[2 * ind].getValue(), this.tree[2 * ind + 1].getValue()));
        }
    }

    /**
     * Queries values in a given range using default operation of the tree.
     * Operates in O(log n) time.
     *
     * @param left Lower bound for the lookup
     * @param right Upper bound for the lookup
     * @return Empty if bounds are out of range, computed value otherwise
     */
    public Optional<T> query(int left, int right) {
        return query(left, right, 1, this.operation);
    }

    /**
     * Queries values in a given range.
     * Operates in O(log n) time.
     *
     * @param left Lower bound for the lookup
     * @param right Upper bound for the lookup
     * @param queryOp Operation used when combining values from different segments
     * @return Empty if bounds are out of range, computed value otherwise
     */
    public Optional<T> query(int left, int right, TreeOperation<T> queryOp) {
        return query(left, right, 1, queryOp);
    }

    /**
     * Auxiliary method to traverse the tree.
     *
     * @param left Lower bound for the lookup
     * @param right Upper bound for the lookup
     * @param ind Position of the current segment in the tree
     * @param operation Operation used when combining values from different segments
     * @return Empty if bounds are out of range, computed value otherwise
     */
    private Optional<T> query(int left, int right, int ind, TreeOperation<T> operation) {
        final Segment<T> curr = this.tree[ind];

        if(left > curr.getUpperBound() || right < curr.getLowerBound())
            return Optional.empty();

        propagate(ind);

        if(left <= curr.getLowerBound() && curr.getUpperBound() >= right)
            return Optional.of(curr.getValue());

        final Optional<T> res1 = query(left, right, 2 * ind, operation);
        final Optional<T> res2 = query(left, right, 2 * ind + 1, operation);

        if(res1.isEmpty()) return res2;
        if(res2.isEmpty()) return res1;

        return Optional.of(operation.accept(res1.get(), res2.get()));
    }

    /**
     * Propagates the lazy values of the current segment and its children.
     *
     * @param ind Position of the current segment in the tree
     */
    private void propagate(int ind) {
        final Segment<T> seg = this.tree[ind];
        final int len = seg.getUpperBound() - seg.getLowerBound() + 1;

        if(this.propagateOp == null) {
            for (int i = 0; i < len; i++)
                seg.integrate(this.operation);
        } else seg.integrate(this.propagateOp);

        if(seg.getLowerBound() != seg.getUpperBound()) {
            this.tree[2 * ind].updateLazy(seg.getLazy(), this.operation);
            this.tree[2 * ind +1].updateLazy(seg.getLazy(), this.operation);
        }

        seg.setLazy(null);
    }

    @Override
    public String toString() {
        final StringBuilder segTree = new StringBuilder();
        toString(0, 1, segTree);
        return segTree.toString();
    }

    private void toString(int level, int ind, StringBuilder str) {
        final Segment<T> curr = this.tree[ind];

        if(level > 0) str.append('|').append("-".repeat(level));

        str.append(' ').append(curr.toString()).append('\n');

        if(curr.getLowerBound() != curr.getUpperBound()) {
            toString(level + 1, 2 * ind, str);
            toString(level + 1, 2 * ind + 1, str);
        }
    }

    public Segment<T>[] getLeaves() {
        return leaves;
    }

    public int getHeight() {
        return height;
    }

    public void setPropagateOp(TreeOperation<T> propagateOp) {
        if(this.propagateOp == null) return;

        this.propagateOp = propagateOp;
    }
}
