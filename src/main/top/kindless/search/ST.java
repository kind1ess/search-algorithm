package main.top.kindless.search;

import java.util.function.BiConsumer;

/**
 * 无序符号表
 * @param <K> 键
 * @param <V> 值
 */
public interface ST<K,V>{
    /**
     * 在符号表中插入记录
     * @param k 键
     * @param v 值
     */
    void put(K k,V v);

    /**
     * 从符号表中获取记录
     * @param k 键
     * @return 值
     */
    V get(K k);

    /**
     * 从符号表中删除记录，只是简单的将它的值赋为null，
     * <br />
     * 请不要尝试将一个键对应的值存为null，因为它随时
     * <br />
     * 有可能会被清除
     * @param k 键
     */
    default void delete(K k){
        if (contains(k)){
            put(k,null);
        }
    }

    /**
     * 符号表是否包含键为k的记录
     * @param k 键
     * @return 包含则返回true
     */
    default boolean contains(K k){
        return get(k) != null;
    }

    /**
     * 符号表是否为空
     * @return 是否为空
     */
    default boolean isEmpty(){
        return size() == 0;
    }

    /**
     * 符号表的大小
     * @return 符号表的大小
     */
    int size();

    /**
     * 返回符号表的所有键
     * @return 符号表的所有键
     */
    Iterable<K> keys();

    /**
     * lambda for循环
     * @param action 循环体
     */
    void forEach(BiConsumer<? super K,? super V> action);
}
