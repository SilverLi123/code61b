package bstmap;

import java.util.*;

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
        Set<K> keySet = new TreeSet<>();
        BSTNode node = root;
        addKey(node, keySet);
        return keySet;
    }

    private void addKey(BSTNode node, Set<K> keySet) {
        if (node == null) {
            return;
        }
        addKey(node.left, keySet);
        keySet.add(node.key);
        addKey(node.right, keySet);
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }
        V val = get(key);
        root = removeHelper(root, key);
        size -= 1;
        return val;
    }

    private BSTNode removeHelper(BSTNode node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = removeHelper(node.left, key);
        } else if (cmp > 0) {
            node.right = removeHelper(node.right, key);
        } else {
            if (node.right == null) return node.left;
            if (node.left == null) return node.right;

            BSTNode successor = min(node.right);
            node.key = successor.key;
            node.val = successor.val;
            node.right = removeHelper(node.right, successor.key);
        }

        return node;
    }

    private BSTNode min(BSTNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public V remove(K key, V val) {
        if (get(key) == val) {
            remove(key);
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIter();
}

    private class BSTMapIter implements Iterator<K> {
        private Deque<BSTMap.BSTNode> stack;

        public BSTMapIter() {
            stack = new ArrayDeque<>();
            pushLeft(root);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public K next() {
            BSTMap.BSTNode node = stack.pop();
            pushLeft(node.right);
            return (K) node.key;
        }

        private void pushLeft(BSTMap.BSTNode node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }
    }
}
