# Segment Trees, a generic implementation
## Introduction
A segment tree is a data structure used to perform time efficient queries on an interval/array.  
Instead of traversing the array linearly (leading to a time complexity of *O(n)*), a segment tree builds a 
full binary tree enabling a time complexity of *O(log n)* for querying results in a given range.  
Using lazy evaluation (only taking changed values into account when they are needed) even the changing of values 
in a range can be done in *O(log n)* time.

## Usage
Consider the following interval, of which the sum in different ranges is needed:  

-5 | -4 | -3 | -2 | -1 | 0 | 1 | 2 | 3 | 4 | 5 
---|----|----|----|----|---|---|---|---|---|---
0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0
  
**1. Tree Initialization**  
The `TreeOperation` represents the action performed when a cell's value is changed, taking into account the bounds
of the current segment (not always a single cell) and, of course, the old aswell as the new value. In this case as
a sum query is needed, the values are added up.
```java
final SegmentTree<Integer> rangeSum = new SegmentTree<>(-5, 5, 0, (__, __, oldVal, newVal) -> oldVal + newVal);
```
  
**2. Adding the Propagation Operation**  
The propagation operation is a `TreeOperation` aswell which is always called when values are lazily evaluated. It is
used to perform changes in a given range. In order to achieve a time complexity of *O(log n)* for changing values the 
propagation operation should resemble *len(Segment)* times the execution of the general operation. In this case we
multiply the new value with the length of the current segment and add that to the old value, which is exactly the
same as performing a simpler + operation numerous times.
```java
rangeSum.setPropagateOp((lowerB, upperB, oldVal, newVal) -> oldVal + (upperB - lowerB + 1) * lazyVal);
```
  
**3. Changing Values**  
When only changing a single cell in the interval, e.g.  
-5 | -4 | -3 | -2 | -1 | 0 | 1 | 2 | 3 | 4 | 5 
---|----|----|----|----|---|---|---|---|---|---
0 | 0 | 5 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0

the method `singleChg(-3, 5)` should be used.  
When doing changes on a certain range, for example adding 3 in range [-4; 1]  
-5 | -4 | -3 | -2 | -1 | 0 | 1 | 2 | 3 | 4 | 5 
---|----|----|----|----|---|---|---|---|---|---
0 | 3 | 8 | 3 | 3 | 3 | 3 | 0 | 0 | 0 | 0

perform `rangeChg(-4, 1, 3)` instead of doing a single change multiple times.
