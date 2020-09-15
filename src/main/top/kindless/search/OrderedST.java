package main.top.kindless.search;

public interface OrderedST<K extends Comparable<K>,V> extends ST<K,V>{

    /**
     * 最小的键
     * @return
     */
    K min();

    /**
     * 最大的键
     * @return
     */
    K max();

    /**
     * 比k小的最大的键
     * @param k
     * @return
     */
    K floor(K k);

    /**
     * 比k大的最小的键
     * @param k
     * @return
     */
    K ceiling(K k);

    /**
     * 小于k的键的数量
     * @param k
     * @return
     */
    int rank(K k);

    /**
     * 排名为i的键
     * @param i
     * @return
     */
    K select(int i);

    /**
     * 删除最小键
     */
    default void deleteMin(){
        delete(min());
    }

    /**
     * 删除最大键
     */
    default void deleteMax(){
        delete(max());
    }

    /**
     * lo到hi之间键的数量
     * @param lo
     * @param hi
     * @return
     */
    default int size(K lo,K hi){
        if (hi.compareTo(lo) < 0){
            return 0;
        }
        else if (contains(hi)){
            return rank(hi) - rank(lo) + 1;
        }
        else return rank(hi) - rank(lo);
    }

    /**
     * lo到hi之间所有键的数量，已排序
     * @param lo
     * @param hi
     * @return
     */
    Iterable<K> keys(K lo,K hi);

    @Override
    default Iterable<K> keys(){
        return keys(min(),max());
    }
}
