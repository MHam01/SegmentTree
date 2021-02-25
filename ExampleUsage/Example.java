public class Example {
    public static void main(String[] args) {
        /*  0  0  0  0  0 0 0 0 0 0 0
           -5 -4 -3 -2 -1 0 1 2 3 4 5 */
        final SegmentTree<Integer> rangeSum = new SegmentTree<>(-5, 5, 0, (l, r, old, val) -> old + val);
        rangeSum.setPropagateOp((l, r, old, lazy) -> old + (r - l + 1) * lazy);

        /*  0  0  5  0  0 0 0 0 0 0 0
           -5 -4 -3 -2 -1 0 1 2 3 4 5 */
        rangeSum.singleChg(-3, 5);
        System.out.println("Sum in range [-4; -1]: " + rangeSum.query(-4, -1).get());

        /*  0  3  8  3  3 3 3 0 0 0 0
           -5 -4 -3 -2 -1 0 1 2 3 4 5 */
        rangeSum.rangeChg(-4, 1, 3);
        System.out.println("Sum in range [-3; 1]: " + rangeSum.query(-3, 1).get());

        System.out.println();
        System.out.println(rangeSum);
        System.out.println('\n');


        final Double[] init = new Double[]{0D, -4.64, 5.634, 375.213, Double.POSITIVE_INFINITY, 1D};
        final SegmentTree<Double> rangeMaximum = new SegmentTree<Double>(0, 5, init, (l, r, old, val) -> Math.max(old, val));
        rangeMaximum.setPropagateOp((l, r, old, lazy) -> Math.max(old, lazy));

        System.out.println("Maximum in range [0; 5]: " + rangeMaximum.query(0, 4).get());
        System.out.println("Maximum in range [0; 3]: " + rangeMaximum.query(0, 3).get());

        System.out.println();
        System.out.println(rangeMaximum);
    }
}
