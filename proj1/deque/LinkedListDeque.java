package deque;

import net.sf.saxon.functions.ConstantFunction;

public class LinkedListDeque<T> {

    private class Node {
        T item;
        Node prev;
        Node next;

        private Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public void addFirst(T item) {
        Node newNode = new Node(item, sentinel, sentinel.next);

        sentinel.next.prev = newNode;
        sentinel.next = newNode;

        size++;
    }

    public void addLast(T item) {
        Node newNode = new Node(item, sentinel.prev, sentinel);

        sentinel.prev.next = newNode;
        sentinel.prev = newNode;

        size++;
    }

    public int size() {
        return size;
    }

    public Boolean isEmpty() {
        return this.size() == 0;
    }

    public void printDeque() {
        Node p = sentinel.next;
        while (p.next != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }

        Node firstNode = sentinel.next;
        T item = firstNode.item;

        firstNode.next.prev = sentinel;
        sentinel.next = firstNode.next;

        firstNode.item = null;
        firstNode.next = null;
        firstNode.prev = null;

        return item;
    }

    public T removeLast() {
        Node lastNode = sentinel.prev;
        T item = lastNode.item;

        lastNode.prev.next = sentinel;
        sentinel.prev = lastNode.prev;

        lastNode.item = null;
        lastNode.prev = null;
        lastNode.next = null;

        return item;
    }

    

}
