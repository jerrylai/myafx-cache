package cn.myafx.cache;

/**
 * 有序集合model
 */
public final class SortSetModel<T> {
    /**
     * 集合数据
     */
    public final T Value;
    /**
     * 排序Score
     */
    public final double Score;

    /**
     * SortSetModel
     * @param value 集合数据
     * @param score 排序Score
     */
    public SortSetModel(T value, double score){
        this.Value = value;
        this.Score = score;
    }
}
