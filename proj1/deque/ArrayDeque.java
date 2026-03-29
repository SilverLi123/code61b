package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private static int INITIAL_CAPACITY = 8;

    public void ArrayDeque() {
        items = (T[]) new Object[INITIAL_CAPACITY];
        size = 0;
        nextFirst = 3;
        nextLast = 4;
    }

    private int oneMinus(int nextFirst) {
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        return nextFirst;
    }

    private int onePlus(int nextLast) {
        nextLast = (nextLast + 1) % items.length;
        return nextLast;
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
        nextFirst = onePlus(nextFirst);
        T removed = items[nextFirst];

        items[nextFirst] = null;
        size--;

        return removed;
    }

    public T removeLast() {
        nextLast = oneMinus(nextLast);
        T removed = items[nextLast];

        items[nextLast] = null;
        size--;

        return removed;
    }

    public void printDeque() {
        int actualIndex0 = (nextFirst + 1) % items.length;

        for (int i = actualIndex0; i < actualIndex0 + size; i++) {
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

}
