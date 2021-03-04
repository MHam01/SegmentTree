import exceptions.FixedOperationException;
import exceptions.IllegalArraySizeException;
import exceptions.IllegalIntervalException;
import operation.TreeOperation;

import java.util.Arrays;
import java.util.Optional;

public class SegmentTree<T> {
    private final int offset, height;
    private final Segment<T>[] tree, leaves;
    private final TreeOperation<T> operation;

    //Can be used to speed up lazy propagation, should represent len(segment) * operation
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
        if(left > right) throw new IllegalIntervalException(left, right);

        final int segmLen = right - left + 1;

        if(leaves.length == 1 && left != right) {
            final T initVal = leaves[0];
            leaves = (T[]) new Object[segmLen];
            Arrays.fill(leaves, initVal);
        } else if(leaves.length != segmLen)
            throw new IllegalArraySizeException(leaves.length, segmLen);

        this.offset = left;
        this.operation = operation;

        this.height = (int) Math.ceil(Math.log10(segmLen) / Math.log10(2)) + 1;

        this.tree = new Segment[(int) Math.pow(2, this.height)];
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

        int mid = (left + right) / 2;

        if(mid < 0 || (left < 0 && mid == 0)) mid--;

        final T leftSubTr = build(leaves, 2 * ind, left, mid);
        final T rightSubTr = build(leaves, 2 * ind + 1, mid + 1, right);

        curr.setValue(operation.accept(curr.getLowerBound(), curr.getUpperBound(), leftSubTr, rightSubTr));
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
        singleChg(leaf, value, 1, this.operation);
    }

    /**
     * Performs changes to a single leaf in the tree given the specified operation.
     * Operates in O(log n) time.
     *
     * @param leaf Leaf to be changed
     * @param value Value to be passed down to the specified leaf node
     * @param operation Operation to be performed on change
     */
    public void singleChg(int leaf, T value, TreeOperation<T> operation) {
        singleChg(leaf, value, 1, operation);
    }

    /**
     * Auxiliary function to traverse the tree.
     *
     * @param leaf Leaf to be changed
     * @param value Value to be passed down to the specified leaf node
     * @param ind Position of the current segment in the tree
     * @param operation Operation to be performed on change
     */
    private void singleChg(int leaf, T value, int ind, TreeOperation<T> operation) {
        final Segment<T> curr = this.tree[ind];

        if(leaf < curr.getLowerBound() || leaf > curr.getUpperBound())
            return;

        curr.setValue(operation.accept(curr.getLowerBound(), curr.getUpperBound(), curr.getValue(), value));

        if(curr.getLowerBound() != curr.getUpperBound()) {
            singleChg(leaf, value, 2 * ind, operation);
            singleChg(leaf, value, 2 * ind + 1, operation);
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
        rangeChg(left, right, value, 1, this.operation);
    }

    /**
     * Perform changes to a given range given the specified operation.
     * Operates in O(log n) time.
     *
     * @param left Lower bound for the change
     * @param right Upper bound for the change
     * @param value Value to be passed down the tree to all leaf nodes in the range
     * @param operation Operation to be performed on change
     */
    public void rangeChg(int left, int right, T value, TreeOperation<T> operation) {
        rangeChg(left, right, value, 1, operation);
    }

    /**
     * Auxiliary method to traverse the tree.
     *
     * @param left Lower bound for the change
     * @param right Upper bound for the change
     * @param value Value to be passed down the tree to all leaf nodes in the range
     * @param ind Position of the current segment in the tree
     * @param operation Operation to be performed on change
     */
    private void rangeChg(int left, int right, T value, int ind, TreeOperation<T> operation) {
        final Segment<T> curr = this.tree[ind];

        propagate(ind);
        if(left > curr.getUpperBound() || curr.getLowerBound() > right)
            return;

        if(left <= curr.getLowerBound() && right >= curr.getUpperBound()) {
            curr.updateLazy(value, operation);
            propagate(ind);
        } else if(curr.getLowerBound() != curr.getUpperBound()) {
            rangeChg(left, right, value, 2 * ind, operation);
            rangeChg(left, right, value, 2 * ind + 1, operation);

            curr.setValue(operation.accept(curr.getLowerBound(), curr.getUpperBound(), this.tree[2 * ind].getValue(), this.tree[2 * ind + 1].getValue()));
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
        return query(left, right, 1);
    }

    /**
     * Auxiliary method to traverse the tree.
     *
     * @param left Lower bound for the lookup
     * @param right Upper bound for the lookup
     * @param ind Position of the current segment in the tree
     * @return Empty if bounds are out of range, computed value otherwise
     */
    private Optional<T> query(int left, int right, int ind) {
        if(ind >= this.tree.length) return Optional.empty();

        final Segment<T> curr = this.tree[ind];

        if(curr == null || left > curr.getUpperBound() || right < curr.getLowerBound())
            return Optional.empty();

        propagate(ind);

        if(left <= curr.getLowerBound() && right >= curr.getUpperBound())
            return Optional.of(curr.getValue());

        final Optional<T> res1 = query(left, right, 2 * ind);
        final Optional<T> res2 = query(left, right, 2 * ind + 1);

        if(res1.isEmpty()) return res2;
        if(res2.isEmpty()) return res1;

        return Optional.of(this.operation.accept(curr.getLowerBound(), curr.getUpperBound(), res1.get(), res2.get()));
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
        toString(-1, 1, segTree);
        return segTree.toString();
    }

    private void toString(int level, int ind, StringBuilder str) {
        final Segment<T> curr = this.tree[ind];

        if(level >= 0) str.append("  ".repeat(level)).append('|').append("-").append(' ');

        str.append(curr.toString()).append('\n');

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
        if(this.propagateOp != null) throw new FixedOperationException();

        this.propagateOp = propagateOp;
    }
}
