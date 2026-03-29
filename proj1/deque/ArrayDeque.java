package deque;

public class ArrayDeque<T> {
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

    public void addFirst(T item) {
        items[nextFirst] = item;
        nextFirst = oneMinus(nextFirst);
        size++;
    }

    public void addLast(T item) {
        items[nextLast] = item;
        nextLast = onePlus(nextLast);
        size++;
    }

    public T get(int index) {
        if (index < 0 || index > this.size()) {
            return null;
        }

        int actualIndex = (nextFirst + 1 + index) % items.length;
        return items[actualIndex];
    }

    public int size() {
        return size;
    }

    public Boolean isEmpty() {
        return this.size() == 0;
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        nextFirst = onePlus(nextFirst);
        T removed = items[nextFirst];

        items[nextFirst] = null;
        size--;

        return removed;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        nextLast = oneMinus(nextLast);
        T removed = items[nextLast];

        items[nextLast] = null;
        size--;

        return removed;
    }

    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

}
