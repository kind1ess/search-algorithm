package main.top.kindless.search.bst;

import main.top.kindless.search.OrderedST;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class BST<K extends Comparable<K>,V> implements OrderedST<K,V>{

    private Node root;

    private class Node{
        K key;
        V val;
        int N;
        Node left,right;

        public Node(K key,V val,int N){
            this.key = key;
            this.val = val;
            this.N = N;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    ", val=" + val +
                    '}';
        }
    }

    @Override
    public K min() {
        Node min = min(root);
        return min == null ? null : min.key;
    }

    private Node min(Node root){
        return (root == null || root.left == null) ? root : min(root.left);
    }

    @Override
    public K max() {
        Node max = max(root);
        return max == null ? null : max.key;
    }

    private Node max(Node root){
        return (root == null || root.right == null) ? root : max(root.right);
    }

    @Override
    public K floor(K k) {
        Node floor = floor(k, root);
        return floor == null ? null : floor.key;
    }

    private Node floor(K k,Node root){
        if (root == null)
            return null;
        int cmp = root.key.compareTo(k);
        if (cmp == 0)
            return root;
        if (cmp > 0){
            return floor(k,root.left);
        }
        Node right = floor(k, root.right);
        return right == null ? root : right;
    }

    @Override
    public K ceiling(K k) {
        Node ceiling = ceiling(k, root);
        return ceiling == null ? null : ceiling.key;
    }

    private Node ceiling(K k,Node root){
        if (root == null)
            return null;
        int cmp = root.key.compareTo(k);
        if (cmp == 0)
            return root;
        if (cmp < 0){
            return floor(k,root.right);
        }
        Node left = floor(k, root.left);
        return left == null ? root : left;
    }

    @Override
    public int rank(K k) {
        return rank(root,k);
    }

    private int rank(Node node,K k){
        if (node == null){
            return 0;
        }
        int cmp = k.compareTo(node.key);
        if (cmp == 0){
            return size(node.left);
        }
        if (cmp > 0){
            return rank(node.right,k) + rank(node.left,k) + 1;
        }
        return rank(node.left,k);
    }

    @Override
    public K select(int i) {
        rangeCheck(i);
        List<K> list = new ArrayList<>();
        inorder(root,list);
        return list.get(i);
    }

    @Override
    public Iterable<K> keys(K lo, K hi) {
        List<K> list = new ArrayList<>();
        inorder(root,list,lo,hi);
        return list;
    }

    @Override
    public void put(K k, V v) {
        root = put(root,k,v);
    }

    @Override
    public V get(K k) {
        return get(root,k);
    }

    @Override
    public int size() {
        return size(root);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        inorder(root,action);
    }

    private int size(Node node){
        return node == null ? 0 : node.N;
    }

    private V get(Node node, K key){
        while (node != null){
            int cmp = node.key.compareTo(key);
            if (cmp == 0){
                break;
            }
            else if (cmp < 0){
                node = node.right;
            }
            else node = node.left;
        }
        return node == null ? null : node.val;
    }

    @Override
    public void deleteMin() {
        if (root == null)
            return;
        root = deleteMin(root);
    }

    private Node deleteMin(Node node){
        if (node.left == null)
            return node.right;
        node.left = deleteMin(node.left);
        resize(node);
        return node;
    }

    @Override
    public void deleteMax() {
        if (root == null)
            return;
        root = deleteMax(root);
    }

    private Node deleteMax(Node node){
        if (node.right == null)
            return node.left;
        node.right = deleteMax(node.right);
        resize(node);
        return node;
    }

    @Override
    public void delete(K k) {
        root = delete(root,k);
    }

    private Node delete (Node node,K k){
        if (node == null)
            return null;
        int cmp = k.compareTo(node.key);
        if (cmp < 0)
            node.left = delete(node.left,k);
        else if (cmp > 0)
            node.right = delete(node.right,k);
        else {
            if (node.right == null)
                return node.left;
            if (node.left == null)
                return node.right;
            Node t = node;
            node = min(t.right);//当前删除结点赋值为其后继结点，即右子树的最小结点
            node.right = deleteMin(t.right);//删除其右子树的最小结点
            node.left = t.left;
        }
        resize(node);
        return node;
    }

    private Node put(Node node, K key, V value){
        //递归出口，node为空则创建新结点
        if (node == null)   return new Node(key,value,1);
        int cmp = node.key.compareTo(key);
        //大于当前子树根结点的键则插入右子树
        if (cmp < 0)    node.right = put(node.right,key,value);
        //小于则插入左子树
        else if (cmp > 0)  node.left = put(node.left,key,value);
        //否则修改当前子树的根结点的值
        else node.val = value;
        //插入成功修改根节点以及每棵子树的N
//        node.N = size(node.left) + size(node.right) + 1;
        resize(node);
        return node;
    }

    private void inorder(Node node,BiConsumer<? super K, ? super V> action){
        if (node != null){
            inorder(node.left,action);
            K k;
            V v;
            try {
                k = node.key;
                v = node.val;
            } catch (IllegalStateException e) {
                throw new ConcurrentModificationException(e);
            }
            action.accept(k,v);
            inorder(node.right,action);
        }
    }

    private void inorder(Node node,List<K> keys){
        if (node != null){
            inorder(node.left,keys);
            keys.add(node.key);
            inorder(node.right,keys);
        }
    }

    private void inorder(Node node,List<K> keys,K lo,K hi){
        if (node != null){
            inorder(node.left,keys,lo,hi);
            K key = node.key;
            if (key.compareTo(lo) >= 0 && key.compareTo(hi) <= 0)
                keys.add(node.key);
            inorder(node.right,keys,lo,hi);
        }
    }

    /**
     * 检查索引是否越界
     * @param index 索引
     */
    private void rangeCheck(int index){
        if (index >= size() || index < 0)
            throw new IndexOutOfBoundsException("Index:"+index+", Size:"+size());
    }

    private void resize(Node node){
        if (node != null)
            node.N = size(node.left) + size(node.right) + 1;
    }
}
