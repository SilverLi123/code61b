package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private static int INITIAL_CAPACITY = 8;

    public ArrayDeque() {
        items = (T[]) new Object[INITIAL_CAPACITY];
        size = 0;
        nextFirst = 3;
        nextLast = 4;
    }

    private int oneMinus(int index) {
        index = (index - 1 + items.length) % items.length;
        return index;
    }

    private int onePlus(int index) {
        index = (index + 1) % items.length;
        return index;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }

        items[nextFirst] = item;
        nextFirst = oneMinus(nextFirst);
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = item;
        nextLast = onePlus(nextLast);
        size++;
    }

    private void resize(int newCapacity) {
        T[] newArray = (T[]) new Object[newCapacity];

        for (int i = 0; i < size; i++) {
            newArray[i] = this.get(i);
        }

        nextFirst = newCapacity - 1;
        nextLast = size;

        items = newArray;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= this.size()) {
            return null;
        }

        int actualIndex = (nextFirst + 1 + index) % items.length;
        return items[actualIndex];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        nextFirst = onePlus(nextFirst);
        T removed = items[nextFirst];

        items[nextFirst] = null;
        size--;

        if (items.length > INITIAL_CAPACITY && size < items.length / 4) {
            resize(Math.max(INITIAL_CAPACITY, items.length / 2));
        }

        return removed;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        nextLast = oneMinus(nextLast);
        T removed = items[nextLast];

        items[nextLast] = null;
        size--;

        if (items.length > INITIAL_CAPACITY && size < items.length / 4) {
            resize(Math.max(INITIAL_CAPACITY, items.length / 2));
        }

        return removed;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        int current = 0;

        @Override
        public boolean hasNext() {
            return current < size;
        }

        @Override
        public T next() {
            return get(current++);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (!(o instanceof ArrayDeque)) {
            return false;
        }

        ArrayDeque<T> other = (ArrayDeque<T>) o;

        if (other.size != this.size) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            T thisItem = this.get(i);
            T otherItem = other.get(i);
            if (thisItem == null) {
                if (otherItem != null) return false;
            } else if (!thisItem.equals(otherItem)) {
                return false;
            }
        }
        return true;
    }

}
