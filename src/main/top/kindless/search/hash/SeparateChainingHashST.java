package main.top.kindless.search.hash;

import main.top.kindless.search.ST;
import main.top.kindless.search.annotation.UnSafe;
import main.top.kindless.search.redblackbst.RedBlackBST;
import main.top.kindless.search.sequential.SequentialSearchST;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Implementation of symbol table based on hash table.<br />
 * In order to adapt to the red black tree search structure <br />
 * beyond the treeify threshold, the key must implement the compatible interface.
 * @param <Key>
 * @param <Value>
 * @author kindless
 * @since 1.0
 * @see main.top.kindless.search.ST
 */
@UnSafe
public class SeparateChainingHashST<Key extends Comparable<Key>, Value> implements ST<Key, Value>, Serializable {

    /*----------Fields----------*/
    /**
     * The current number of elements in the symbol table.
     */
    private int N;

    /**
     * The number of hash buckets of this symbol table.
     */
    private int M;

    /**
     * The treeify threshold.<br />
     * When the number of elements in the hash bucket <br />
     * exceeds this value, the data structure of the <br />
     * hash bucket will be converted from a linked <br />
     * list to a red black tree.
     */
    private int threshold;

    /**
     * The number of hash buckets that have been filled.
     */
    private int filledNum;

    /**
     * The hash bucket array, can accommodate a variety <br />
     * of symbol tables, this implementation only uses <br />
     * sequential symbol table (based on linked list), <br />
     * and balanced binary search tree symbol table <br />
     * (based on red black tree).
     */
    private ST<Key, Value>[] sts;
    
    /*-----------Constants----------*/
    /**
     * The hash table load factor, which must be initialized <br />
     * when the object is created. If the filling ratio of hash <br />
     * bucket exceeds this value, the hash bucket array needs <br />
     * to be expanded.
     */
    private final double loadFactor;

    /**
     * The default number of hash buckets. This number seems too large, <br />
     * so if you do not need such a large capacity, please be sure to <br />
     * set the number of initialization hash buckets in the <br />
     * initialization parameter.
     */
    private static final int DEFAULT_SIZE = 997;

    /**
     * Default initialization load factor. <br />
     * Setting it to 0.75 is a balancing strategy in terms of time and <br />
     * space efficiency, because it will not make hash conflicts occur <br />
     * frequently and will not waste too much space.
     */
    private static final double DEFAULT_LOAD_FACTOR = .75;

    /**
     * Treeify threshold. <br />
     * When the number of elements in a hash bucket exceeds this value, <br />
     * its data structure will be converted from a linked list to a red <br />
     * black tree to improve the efficiency of searching or inserting. <br />
     * A good hash function is to insinuate the key value of each element <br />
     * into the hash table, presenting a Poisson distribution. According <br />
     * to the Poisson distribution equation, the probability that the <br />
     * number of elements in the same hash bucket exceeds 8 is very low, <br />
     * so choosing the number of 8 will be a balance strategy in space <br />
     * and time efficiency (the space occupied by red black tree nodes <br />
     * is almost twice that of linked list nodes).
     */
    private static final int TREEIFY_THRESHOLD = 8;

    public SeparateChainingHashST() {
        this(DEFAULT_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public SeparateChainingHashST(int M) {
        this(M, DEFAULT_LOAD_FACTOR);
    }

    /**
     *
     * @param M Initial number of hash buckets, which must be positive.
     * @param loadFactor Hash table load factor, must be between 0 and 1.
     * @throws SymbolTableInitialException
     * Symbol table initialization exception is usually <br />
     * thrown due to incorrect initialization parameters.
     */
    @SuppressWarnings("unchecked")
    public SeparateChainingHashST(int M, double loadFactor) {
        if (M < 1)
            throw new SymbolTableInitialException("initial capacity is invalid, capacity:" + M);
        if (loadFactor <= 0 || loadFactor > 1)
            throw new SymbolTableInitialException("initial loadFactor is invalid, loadFactor:" + loadFactor);
        this.M = M;
        this.loadFactor = loadFactor;
        threshold = (int) (loadFactor * M);
        sts = new ST[M];
        for (int i = 0; i < M; i++) {
            sts[i] = new SequentialSearchST<>();
        }
    }

    /**
     * Insertion operation.
     * First, it checks whether the number of hash buckets filled exceeds
     * the threshold value. If it exceeds the threshold value, the system
     * will reset. Then insert. The insert operation actually calls the
     * insert method of the symbol table in the hash bucket.
     * @param key The unique identification of the data element
     * @param value Data element value
     * @see main.top.kindless.search.sequential.SequentialSearchST
     * @see main.top.kindless.search.redblackbst.RedBlackBST
     */
    @Override
    public void put(Key key, Value value) {
        int hash;
        if (filledNum >= threshold) {
            resize();
        }
        if (sts[hash = hash(key)].size() == 0) {
            filledNum++;
        }
        sts[hash].put(key, value);
        if (sts[hash].size() >= TREEIFY_THRESHOLD) {
            treeify(hash);
        }
        N++;
    }

    /**
     * Deletion operation.
     * It actually calls the deletion method of the symbol table in the hash bucket.
     * @param key The unique identification of the data element
     * @see main.top.kindless.search.sequential.SequentialSearchST
     * @see main.top.kindless.search.redblackbst.RedBlackBST
     */
    @Override
    public void delete(Key key) {
        sts[hash(key)].delete(key);
        N--;
    }

    /**
     * Search operation.
     * It actually calls the search method of the symbol table in the hash bucket.
     * @param key The unique identification of the data element
     * @return The value of the data element
     * @see main.top.kindless.search.sequential.SequentialSearchST
     * @see main.top.kindless.search.redblackbst.RedBlackBST
     */
    @Override
    public Value get(Key key) {
        return sts[hash(key)].get(key);
    }

    /**
     * The number of data elements in the symbol table.
     * @return The number of data elements in the symbol table.
     */
    @Override
    public int size() {
        return N;
    }

    /**
     * Returns all keys in the symbol table, which is iterable.
     * @return all keys in the symbol table
     */
    @Override
    public Iterable<Key> keys() {
        List<Key> list = new ArrayList<>();
        forEach(((key, value) -> list.add(key)));
        return list;
    }

    /**
     * The iteration operation provided internally supports lambda expression.
     * @param action Circulatory body
     */
    @Override
    public void forEach(BiConsumer<? super Key, ? super Value> action) {
        Objects.requireNonNull(action);
        System.out.println(action);
        for (ST<Key, Value> st : sts) {
            st.forEach(action);
        }
    }

    /**
     * Convert all the key value pairs to string.
     * @return string value
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (ST<Key, Value> st : sts) {
            if (st != null) {
                st.forEach((k, v) -> sb.append(k).append("=").append(v).append(","));
            }
        }
        if (sb.charAt(sb.length() - 1) == ',')
            sb.replace(sb.length() - 1, sb.length(), "");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Returns the current load factor of the symbol table.
     * @return  current load factor of the symbol table
     */
    public double loadFactor() {
        return (double) filledNum() / (double) M;
    }

    /**
     * Returns the number of data elements per hash bucket.
     * @return the number of data elements per hash bucket
     */
    public int[] sizeOfEachButton() {
        int[] res = new int[M];
        for (int i = 0; i < M; i++) {
            res[i] = sts[i].size();
        }
        return res;
    }

    /**
     * Returns the number of red and black trees in the current symbol table.
     * @return the number of red and black trees in the current symbol table.
     */
    public int treeNum() {
        int num = 0;
        for (ST<Key, Value> st : sts) {
            if (st instanceof RedBlackBST)
                num++;
        }
        return num;
    }

    /**
     * When the load factor of the current symbol table exceeds the set load <br />
     * factor, the size of hash buckets will be increased to twice the previous capacity.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        ST<Key, Value>[] tmp = new ST[M = M << 1];
        int i;
        for (i = 0; i < sts.length; i++) {
            tmp[i] = sts[i];
        }
        while (i < M) {
            tmp[i++] = new SequentialSearchST<>();
        }
        sts = tmp;
        threshold = (int) (loadFactor * M);
    }

    /**
     * Represents the number of buckets filled in the current hash table,<br />
     * the more hash buckets are filled, the less hash conflicts are.<br />
     * However, when the filling ratio of the current hash table is greater than. 75 (default load factor),<br />
     * the probability of hash conflict will increase, and the capacity of hash bucket needs to be expanded.<br />
     * @return The number of hash buckets that have been filled
     * @author kindless
     * @since 1.0
     */
    private int filledNum() {
        return filledNum;
    }

    /**
     * This method is triggered when the number of nodes in <br />
     * the hash bucket is greater than 8,<br />
     * The data structure of hash bucket is transformed <be />
     * from linked list to red black tree to improve the efficiency of search and insert.
     * @param hash The array index of the hash bucket to be converted, <br />
     *             which is also the hash value of the key
     * @author kindless
     * @since 1.0
     */
    private void treeify(int hash) {
        ST<Key, Value> st = sts[hash];
        sts[hash] = new RedBlackBST<>();
        st.forEach((k, v) -> sts[hash].put(k, v));
    }

    /**
     * Calculate the hash value of the key, and the hash code of the key <br />
     * and <strong>0x7fffffff</strong> are combined to obtain a non negative number.<br/>
     * If the key passed in is null, a hash value of 0 is returned.
     * @param key Unique identification of data
     * @return Hash value of the data
     * @author kindless
     * @since 1.0
     */
    private int hash(Key key) {
        return key == null ? 0 : (key.hashCode() & 0x7fffffff) % M;
    }
}

/**
 * Symbol table initialization exception.
 * @see java.lang.Exception
 * @see java.lang.RuntimeException
 * @see java.lang.Throwable
 */
class SymbolTableInitialException extends RuntimeException {

    public SymbolTableInitialException(String message) {
        super(message);
    }

    public SymbolTableInitialException(String message, Throwable e) {
        super(message, e);
    }
}