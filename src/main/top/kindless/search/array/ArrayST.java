package main.top.kindless.search.array;

import main.top.kindless.search.ST;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;

public class ArrayST<K,V> implements ST<K,V> {

    private Node[] nodes;

    private static final int DEFAULT_INITIAL_CAPACITY = 8;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private int size;

    private class Node{
        K key;
        V val;

        public Node(K key,V val){
            this.key = key;
            this.val = val;
        }
    }

    public ArrayST(){
        this(DEFAULT_INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public ArrayST(int capacity){
        if (capacity <= 0)
            throw new IllegalArgumentException("capacity must be greater than zero");
        nodes = (Node[]) Array.newInstance(Node.class,capacity);
        nodes[0] = new Node(null,null);
        size = 1;   //默认第一个元素当哨兵，提高性能
        //后台开启垃圾清理
        Thread gc = new Thread(() -> {
            while (true) {
                collectGarbage();
            }
        }, "garbageCollection");
        gc.setDaemon(true);
        gc.start();
    }

    @Override
    public void put(K k, V v) {
        nullValueCheck(k);
        for (int i = 1; i < size; i++) {
            if (nodes[i].key.equals(k)){
                nodes[i].val = v;
                return;
            }
        }
        ensureCapacityInternal(size+1);
        nodes[size++] = new Node(k,v);
    }

    @Override
    public V get(K k) {
        nullValueCheck(k);
        int i = size - 1;
        nodes[0].key = k;
        nodes[0].val = null;
        while (!nodes[i].key.equals(k)){
            i--;
        }
        return nodes[i].val;
    }

    @Override
    public int size() {
        return size-1;
    }

    @Override
    public Iterable<K> keys() {
        List<K> list = new ArrayList<>();
        for (int i = 1; i < size; i++) {
            list.add(nodes[i].key);
        }
        return list;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (int i = 1; i < size; i++) {
            K k;
            V v;
            try {
                k = nodes[i].key;
                v = nodes[i].val;
            } catch (IllegalStateException e) {
                throw new ConcurrentModificationException(e);
            }
            action.accept(k,v);
        }
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

    /**
     * 确保数组容量
     * @param miniCapacity
     */
    private void ensureCapacityInternal(int miniCapacity){
        if (miniCapacity - nodes.length > 0){
            grow(miniCapacity);
        }
    }

    /**
     * 修改数组大小
     * @param miniCapacity
     */
    private void grow(int miniCapacity){
        int oldCapacity = nodes.length;
        int newCapacity = oldCapacity == 1 ? oldCapacity << 1 : oldCapacity + (oldCapacity >> 1);
        if (newCapacity - oldCapacity < 0)
            newCapacity = oldCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(miniCapacity);
        nodes = Arrays.copyOf(nodes,newCapacity);
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

    private void collectGarbage(){
        for (int i = 1; i < size; i++) {
            if (nodes[i].val == null || nodes[i].key == null){
                if (size - 1 - i >= 0) System.arraycopy(nodes, i + 1, nodes, i, size - 1 - i);
                nodes[--size] = null;
            }
        }
    }
}
