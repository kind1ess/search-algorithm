package main.top.kindless.search.binary;

import main.top.kindless.search.OrderedST;

import java.util.*;
import java.util.function.BiConsumer;


public class BinarySearchST<K extends Comparable<K>,V> implements OrderedST<K,V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 8;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private K[] keys;

    private V[] values;

    private int size;

    public BinarySearchST(){
        this(DEFAULT_INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public BinarySearchST(int capacity){
        keys = (K[]) new Comparable[capacity];
        values = (V[]) new Object[capacity];
    }

    @Override
    public K min() {
        if (isEmpty())
            throw new NoSuchElementException("called min() with empty symbol table");
        return keys[0];
    }

    @Override
    public K max() {
        if (isEmpty())
            throw new NoSuchElementException("called max() with empty symbol table");
        return keys[size-1];
    }

    @Override
    public K floor(K k) {
        nullValueCheck(k);
        int i = rank(k);
        if (i < size && k.compareTo(keys[i]) == 0)
            return keys[i];
        return i == 0 ? null : keys[i-1];
    }

    @Override
    public K ceiling(K k) {
        nullValueCheck(k);
        return keys[rank(k)];
    }

    @Override
    public int rank(K k) {
        nullValueCheck(k);
        int lo = 0, hi = size - 1;
        while (lo <= hi){
            int mid = (lo+hi)/2;
            int cmp = k.compareTo(keys[mid]);
            if (cmp < 0)
                hi = mid - 1;
            else if (cmp > 0)
                lo = mid + 1;
            else return mid;
        }
        return lo;
    }

    @Override
    public K select(int i) {
        rangeCheck(i);
        return keys[i];
    }

    @Override
    public Iterable<K> keys(K lo, K hi) {
        nullValueCheck(lo,hi);
        List<K> list = new ArrayList<>();
        if (lo.compareTo(hi) > 0)
            return list;
        list.addAll(Arrays.asList(keys).subList(rank(lo), rank(hi)));
        if (contains(hi))
            list.add(keys[rank(hi)]);
        return list;
    }

    /**
     * 在容量不足的时候会进行扩容操作，这样会大大降低该次插入值的效率，
     * <br />
     * 所以最好提前预估二分查找符号表的大小
     * @param k 键
     * @param v 值
     */
    @Override
    public void put(K k, V v) {
        nullValueCheck(k,v);
        int i = rank(k);
        if (i < size && keys[i].compareTo(k) == 0){
            values[i] = v;
            return;
        }
        ensureCapacityInternal(size + 1);
        for (int j = size; j > i; j--) {
            keys[j] = keys[j - 1];
            values[j] = values[j-1];
        }
        keys[i] = k;
        values[i] = v;
        size++;
    }

    @Override
    public V get(K k) {
        if (isEmpty())
            return null;
        int i = rank(k);
        if (i < size && keys[i].compareTo(k) == 0)  //如果存在该键
            return values[i];
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void delete(K k) {
        nullValueCheck(k);
        if (isEmpty())
            return;
        int i = rank(k);
        if (i == size || keys[i].compareTo(k) != 0)
            return;
        for (int j = i; j < size - 1; j++) {
            keys[j] = keys[j + 1];
            values[j] = values[j + 1];
        }
        size--;
        keys[size] = null;
        values[size] = null;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (int i = 0; i < size; i++) {
            K k;
            V v;
            try {
                k = keys[i];
                v = values[i];
            } catch (IllegalStateException e) {
                throw new ConcurrentModificationException(e);
            }
            action.accept(k,v);
        }
    }

    /**
     * 确保数组容量
     * @param miniCapacity
     */
    private void ensureCapacityInternal(int miniCapacity){
        if (miniCapacity - keys.length > 0){
            grow(miniCapacity);
        }
    }

    /**
     * 修改数组大小
     * @param miniCapacity
     */
    private void grow(int miniCapacity){
        int oldCapacity = keys.length;
        int newCapacity = oldCapacity == 1 ? oldCapacity << 1 : oldCapacity + (oldCapacity >> 1);
        if (newCapacity - oldCapacity < 0)
            newCapacity = oldCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(miniCapacity);
        synchronized (this){
            keys = Arrays.copyOf(keys,newCapacity);
            values = Arrays.copyOf(values,newCapacity);
        }
    }

    /**
     * 修改数组大小，从ArrayList源码搬来的
     * @param miniCapacity
     * @return
     */
    private static int hugeCapacity(int miniCapacity){
        if (miniCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (miniCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    /**
     * 检查索引是否越界
     * @param index 索引
     */
    private void rangeCheck(int index){
        if (index >= size || index < 0)
            throw new IndexOutOfBoundsException("Index:"+index+", Size:"+size);
    }

    /**
     * 检查空参数，任意一个参数为空都会检查不通过
     * @param value 参数列表
     */
    private void nullValueCheck(Object ...value){
        for (Object o : value) {
            if (o == null){
                throw new IllegalArgumentException("argument is null");
            }
        }
    }
}
