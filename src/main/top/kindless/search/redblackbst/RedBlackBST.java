package main.top.kindless.search.redblackbst;

import main.top.kindless.search.OrderedST;
import main.top.kindless.search.sequential.SequentialSearchST;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;

public class RedBlackBST<Key extends Comparable<Key>, Value>
        implements OrderedST<Key, Value> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;
    private Node root;

    private class Node {
        Key key;
        Value value;
        boolean color;
        Node left, right;
        int N;

        public Node(Key key, Value value, int N, boolean color) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.N = N;
        }
    }

    @Override
    public void put(Key key, Value value) {
        root = put(root, key, value);
        root.color = BLACK;
    }

    @Override
    public Value get(Key key) {
        Node node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp == 0)
                return node.value;
            else if (cmp > 0)
                node = node.right;
            else node = node.left;
        }
        return null;
    }

    @Override
    public int size() {
        return size(root);
    }

    @Override
    public Iterable<Key> keys() {
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super Key, ? super Value> action) {
        Objects.requireNonNull(action);
        inorder(root, action);
    }

    @Override
    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        if (!contains(key)) return;

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = delete(root, key);
        if (!isEmpty()) root.color = BLACK;
    }

    @Override
    public Key min() {
        if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
        return min(root).key;
    }

    @Override
    public Key max() {
        if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
        return max(root).key;
    }

    @Override
    public Key floor(Key key) {
        return null;
    }

    @Override
    public Key ceiling(Key key) {
        return null;
    }

    @Override
    public int rank(Key key) {
        return 0;
    }

    @Override
    public Key select(int i) {
        return null;
    }

    @Override
    public Iterable<Key> keys(Key lo, Key hi) {
        return null;
    }

    @Override
    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("BST underflow");

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMin(root);
        if (!isEmpty()) root.color = BLACK;
    }

    @Override
    public void deleteMax() {
        if (isEmpty()) throw new NoSuchElementException("BST underflow");

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMax(root);
        if (!isEmpty()) root.color = BLACK;
    }

    /**
     * 判断一个结点是不是红结点<br/>
     * 一个结点是红结点代表的是指向它的链接是红色的<br/>
     * 红链接只能是左倾斜的，代表的是2-3树中的3结点之间的两个键之间的链接<br/>
     * （在红黑树中没有3结点，而是用这种左倾斜的红链接以及它连接的两个2结点来代替），<br/>
     *
     * @param x
     * @return
     */
    private boolean isRed(Node x) {
        return x != null && x.color == RED;
    }

    private Node put(Node node, Key key, Value value) {
        if (node == null)
            return new Node(key, value, 1, RED);
        int cmp = key.compareTo(node.key);
        if (cmp < 0) node.left = put(node.left, key, value);
        else if (cmp > 0) node.right = put(node.right, key, value);
        else node.value = value;

        //开始旋转以及变色
        if (isRed(node.right) && !isRed(node.left))
            node = rotateLeft(node);
        if (isRed(node.left) && isRed(node.left.left))
            node = rotateRight(node);
        if (isRed(node.left) && isRed(node.right))
            flipColors(node);
        resize(node);
        return node;
    }

    /**
     * 左旋操作。红链接只能是左倾斜的，如果一个红链接是右倾斜的，<br/>
     * 那么就需要对它的根结点进行左旋。
     *
     * @param node
     * @return
     */
    private Node rotateLeft(Node node) {
        Node x = node.right;
        node.right = x.left;
        x.left = node;
        x.color = node.color;
        node.color = RED;
        x.N = node.N;
        resize(node);
        return x;
    }

    /**
     * 右旋操作。当一个红结点的左孩子结点也是红结点的时候是不符合规则的<br/>
     * 此时就需要右旋，使得该结点不是红结点，但是它的左右孩子结点都是红结点。<br/>
     * 此时还是不符合规则的，接下来就需要变色，将两个左孩子结点变为黑结点，将根结点变为红结点。
     *
     * @param node
     * @return
     */
    private Node rotateRight(Node node) {
        Node x = node.left;
        node.left = x.right;
        x.right = node;
        x.color = node.color;
        node.color = RED;
        x.N = node.N;
        resize(node);
        return x;
    }

    private void flipColors(Node node) {
        node.color = RED;
        node.left.color = BLACK;
        node.right.color = BLACK;
    }

    private void resize(Node node) {
        node.N = size(node.left) + size(node.right) + 1;
    }

    private int size(Node node) {
        return node == null ? 0 : node.N;
    }

    private void inorder(Node node, BiConsumer<? super Key, ? super Value> action) {
        if (node != null) {
            inorder(node.left, action);
            Key key;
            Value value;
            try {
                key = node.key;
                value = node.value;
            } catch (IllegalStateException e) {
                throw new ConcurrentModificationException(e);
            }
            action.accept(key, value);
            inorder(node.right, action);
        }
    }

    private Node delete(Node h, Key key) {
        // assert get(h, key) != null;

        if (key.compareTo(h.key) < 0) {
            if (!isRed(h.left) && !isRed(h.left.left)) {
                h = moveRedLeft(h);
            }
            h.left = delete(h.left, key);
        } else {
            if (isRed(h.left))
                h = rotateRight(h);
            if (key.compareTo(h.key) == 0 && (h.right == null))
                return null;
            if (!isRed(h.right) && !isRed(h.right.left))
                h = moveRedRight(h);
            if (key.compareTo(h.key) == 0) {
                Node x = min(h.right);
                h.key = x.key;
                h.value = x.value;
                // h.val = get(h.right, min(h.right).key);
                // h.key = min(h.right).key;
                h.right = deleteMin(h.right);
            } else h.right = delete(h.right, key);
        }
        return balance(h);
    }

    private Node moveRedLeft(Node h) {
        // assert (h != null);
        // assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }

    // Assuming that h is red and both h.right and h.right.left
    // are black, make h.right or one of its children red.
    private Node moveRedRight(Node h) {
        // assert (h != null);
        // assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }

    private Node min(Node x) {
        // assert x != null;
        if (x.left == null) return x;
        else return min(x.left);
    }

    private Node max(Node x) {
        // assert x != null;
        if (x.right == null) return x;
        else return max(x.right);
    }

    private Node deleteMin(Node h) {
        if (h.left == null)
            return null;

        if (!isRed(h.left) && !isRed(h.left.left))
            h = moveRedLeft(h);

        h.left = deleteMin(h.left);
        return balance(h);
    }

    private Node balance(Node h) {
        // assert (h != null);

        if (isRed(h.right)) h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right)) flipColors(h);

        resize(h);
        return h;
    }

    private Node deleteMax(Node h) {
        if (isRed(h.left))
            h = rotateRight(h);

        if (h.right == null)
            return null;

        if (!isRed(h.right) && !isRed(h.right.left))
            h = moveRedRight(h);

        h.right = deleteMax(h.right);

        return balance(h);
    }
}
