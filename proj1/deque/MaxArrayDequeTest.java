package deque;

import org.junit.Test;
import java.util.Comparator;
import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    // 按字符串长度比较的 Comparator
    private static class StringLengthComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.length() - b.length();
        }
    }

    // 按字符串字典序比较
    private static class NaturalStringComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    }

    // 按整数自然顺序比较
    private static class NaturalIntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }

    // 按整数倒序比较的 Comparator（大的算小，小的算大）
    private static class ReverseIntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return b - a;
        }
    }

    @Test
    public void testMaxEmpty() {
        MaxArrayDeque<String> dq = new MaxArrayDeque<>(new StringLengthComparator());
        assertNull(dq.max());
        assertNull(dq.max(new StringLengthComparator()));
    }

    @Test
    public void testMaxSingleElement() {
        MaxArrayDeque<Integer> dq = new MaxArrayDeque<>(new NaturalIntComparator());
        dq.addLast(5);
        assertEquals(Integer.valueOf(5), dq.max());
    }

    @Test
    public void testMaxWithConstructorComparator() {
        MaxArrayDeque<String> dq = new MaxArrayDeque<>(new StringLengthComparator());
        dq.addLast("hi");
        dq.addLast("hello");
        dq.addLast("hey");

        // 按长度比，最长的应该是 "hello"
        assertEquals("hello", dq.max());
    }

    @Test
    public void testMaxWithCustomComparator() {
        MaxArrayDeque<String> dq = new MaxArrayDeque<>(new StringLengthComparator());
        dq.addLast("hi");
        dq.addLast("hello");
        dq.addLast("apple");

        // 用构造函数的 Comparator（按长度）→ "hello"
        assertEquals("hello", dq.max());

        // 用自然顺序（字典序）→ "hi"
        assertEquals("hi", dq.max(new NaturalStringComparator()));
    }

    @Test
    public void testMaxIntegers() {
        MaxArrayDeque<Integer> dq = new MaxArrayDeque<>(new NaturalIntComparator());
        dq.addLast(3);
        dq.addLast(1);
        dq.addLast(7);
        dq.addLast(4);

        assertEquals(Integer.valueOf(7), dq.max());
    }

    @Test
    public void testMaxWithReverseComparator() {
        MaxArrayDeque<Integer> dq = new MaxArrayDeque<>(new NaturalIntComparator());
        dq.addLast(3);
        dq.addLast(1);
        dq.addLast(7);
        dq.addLast(4);

        // 默认 Comparator → 最大值 7
        assertEquals(Integer.valueOf(7), dq.max());

        // 倒序 Comparator → "最大"变成最小值 1
        assertEquals(Integer.valueOf(1), dq.max(new ReverseIntComparator()));
    }

    @Test
    public void testMaxAfterRemove() {
        MaxArrayDeque<Integer> dq = new MaxArrayDeque<>(new NaturalIntComparator());
        dq.addLast(3);
        dq.addLast(9);
        dq.addLast(5);

        assertEquals(Integer.valueOf(9), dq.max());

        dq.removeFirst(); // 移除 3
        assertEquals(Integer.valueOf(9), dq.max());

        dq.removeLast(); // 移除 5
        assertEquals(Integer.valueOf(9), dq.max());

        dq.removeFirst(); // 移除 9
        assertNull(dq.max());
    }

}
