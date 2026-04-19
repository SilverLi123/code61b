package bstmap;

import java.util.Set;
import java.util.Iterator;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;
    private int size;

    private class BSTNode {
        K key;
        V val;
        BSTNode left, right;

        BSTNode(K key, V val) {
            this.key = key;
            this.val = val;
        }
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            return false;
        }
        if (root == null) {
            return false;
        }
        BSTNode node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp > 0) {
                node = node.right;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }
        if (root == null) {
            return null;
        }
        BSTNode node = root;

        while(node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp > 0) {
                node = node.right;
            }
            else if (cmp < 0) {
                node = node.left;
            }
            else {
                return node.val;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void put(K key, V val) {
        root = putHelper(root, key, val);
    }

    private BSTNode putHelper(BSTNode node, K key, V val) {
        if (node == null) {
            size+= 1;
            return new BSTNode(key, val);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = putHelper(node.left, key, val);
        }
        else if (cmp > 0) {
            node.right = putHelper(node.right, key, val);
        }
        else {
            node.val = val;
        }

        return node;
    }

    public void printInOrder() {
        printInOrderHelper(root);
    }

    private void printInOrderHelper(BSTNode node) {
        if (node == null) {
            return;
        }
        printInOrderHelper(node.left);
        System.out.println(node.key);
        printInOrderHelper(node.right);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
