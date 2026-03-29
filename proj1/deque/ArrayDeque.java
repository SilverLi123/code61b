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
}
