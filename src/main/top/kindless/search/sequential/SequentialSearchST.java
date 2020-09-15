package main.top.kindless.search.sequential;

import main.top.kindless.search.ST;

import java.util.*;
import java.util.function.BiConsumer;

public class SequentialSearchST<K,V> implements ST<K,V> {

    private Node first;

    private int size;

    public SequentialSearchST(){
    }

    private class Node{
        K k;
        V v;
        Node next;

        public Node(K k,V v,Node next){
            this.k = k;
            this.v = v;
            this.next = next;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "k=" + k +
                    ", v=" + v +
                    '}';
        }
    }

    @Override
    public void put(K k, V v) {
        for (Node x = first; x != null; x = x.next){
            if (x.k.equals(k)){
                x.v = v;
                return;
            }
        }
        first = new Node(k,v,first);
        size++;
    }

    @Override
    public V get(K k) {
        for (Node x = first; x != null; x = x.next)
            if (x.k.equals(k))
                return x.v;
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterable<K> keys() {
        List<K> keys = new Stack<>();
        for (Node x = first; x != null; x = x.next)
            keys.add(x.k);
        return keys;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Node x = first; x != null; x=x.next){
            K k;
            V v;
            try {
                k = x.k;
                v = x.v;
            } catch (IllegalStateException e) {
                throw new ConcurrentModificationException(e);
            }
            action.accept(k,v);
        }
    }

    @Override
    public void delete(K k) {
        Objects.requireNonNull(k);
        if (size() == 0)
            return;
        Node p = new Node(null,null,first);
        Node head = p;
        while (p.next != null){
            if (p.next.k.equals(k)) {
                p.next = p.next.next;
                first = head.next;
                size--;
                return;
            }
            p = p.next;
        }
    }

    /**
     * 注意，垃圾回收并不一定起作用，必要的时候可以选择强制垃圾回收
     */
    private void collectGarbage() {
        while (first != null && first.v == null){
            first = first.next;
        }
        if (first == null){
            return;
        }
        Node f = first;
        for (Node x = f.next; x != null; x = x.next, f = f.next){
            if (x.v == null || x.k == null){
                x = x.next;
                f.next = x;
                if (x == null){
                    break;
                }
            }
        }
    }
}
