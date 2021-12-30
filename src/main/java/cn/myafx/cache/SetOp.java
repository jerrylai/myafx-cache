package cn.myafx.cache;

/**
 * 集合交集运算选项
 */
public enum SetOp {
    /**
     * Returns the members of the set resulting from the union of all the given sets.
     */
    Union,
    /**
     * Returns the members of the set resulting from the intersection of all the given sets.
     */
    Intersect,
    /**
     * Returns the members of the set resulting from the difference between the first set and all the successive sets.
     */
    Difference,
}
