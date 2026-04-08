package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void testAddGet() {
        ArrayDeque<String> dq = new ArrayDeque<>();
        dq.addFirst("middle");
        dq.addFirst("front");
        dq.addLast("back");

        assertEquals("front", dq.get(0));
        assertEquals("middle", dq.get(1));
        assertEquals("back", dq.get(2));
        assertEquals(3, dq.size());
    }

    @Test
    public void testEmptyAndBounds() {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        assertTrue(dq.isEmpty());
        assertNull(dq.get(0));
        assertNull(dq.get(-1));

        dq.addFirst(1);
        assertFalse(dq.isEmpty());
        assertNull(dq.get(100));
    }

    @Test
    public void testRemoveMix() {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        dq.addFirst(1);
        dq.addLast(2);
        dq.addFirst(0);

        assertEquals(Integer.valueOf(2), dq.removeLast());
        assertEquals(Integer.valueOf(0), dq.removeFirst());
        assertEquals(Integer.valueOf(1), dq.get(0));

        dq.removeLast();
        assertTrue(dq.isEmpty());
    }

    @Test
    public void testRemoveEmpty() {
        ArrayDeque<String> dq = new ArrayDeque<>();
        assertNull(dq.removeFirst());
        assertNull(dq.removeLast());
    }

    @Test
    public void testResizeUp() {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        for (int i = 0; i < 16; i++) {
            dq.addLast(i);
        }
        assertEquals(16, dq.size());
        for (int i = 0; i < 16; i++) {
            assertEquals(Integer.valueOf(i), dq.get(i));
        }
    }

    @Test
    public void testResizeDown() {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        for (int i = 0; i < 16; i++) {
            dq.addLast(i);
        }
        for (int i = 0; i < 14; i++) {
            dq.removeFirst();
        }
        assertEquals(2, dq.size());
        assertEquals(Integer.valueOf(14), dq.get(0));
        assertEquals(Integer.valueOf(15), dq.get(1));
    }

    @Test
    public void testIterator() {
        ArrayDeque<String> dq = new ArrayDeque<>();
        dq.addLast("a");
        dq.addLast("b");
        dq.addLast("c");

        String[] expected = {"a", "b", "c"};
        int i = 0;
        for (String s : dq) {
            assertEquals(expected[i], s);
            i++;
        }
        assertEquals(3, i);
    }

    @Test
    public void testIteratorEmpty() {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        int count = 0;
        for (Integer x : dq) {
            count++;
        }
        assertEquals(0, count);
    }

    @Test
    public void testEqualsSameObject() {
        ArrayDeque<String> dq = new ArrayDeque<>();
        dq.addLast("a");
        assertTrue(dq.equals(dq));
    }

    @Test
    public void testEqualsIdenticalContent() {
        ArrayDeque<String> dq1 = new ArrayDeque<>();
        dq1.addLast("a");
        dq1.addLast("b");

        ArrayDeque<String> dq2 = new ArrayDeque<>();
        dq2.addLast("a");
        dq2.addLast("b");

        assertTrue(dq1.equals(dq2));
        assertTrue(dq2.equals(dq1));
    }

    @Test
    public void testEqualsDifferentSize() {
        ArrayDeque<Integer> dq1 = new ArrayDeque<>();
        dq1.addLast(1);
        dq1.addLast(2);

        ArrayDeque<Integer> dq2 = new ArrayDeque<>();
        dq2.addLast(1);

        assertFalse(dq1.equals(dq2));
    }

    @Test
    public void testEqualsDifferentContent() {
        ArrayDeque<String> dq1 = new ArrayDeque<>();
        dq1.addLast("a");
        dq1.addLast("b");

        ArrayDeque<String> dq2 = new ArrayDeque<>();
        dq2.addLast("a");
        dq2.addLast("c");

        assertFalse(dq1.equals(dq2));
    }

    @Test
    public void testEqualsEmpty() {
        ArrayDeque<String> dq1 = new ArrayDeque<>();
        ArrayDeque<String> dq2 = new ArrayDeque<>();
        assertTrue(dq1.equals(dq2));
    }

    @Test
    public void testEqualsNull() {
        ArrayDeque<String> dq = new ArrayDeque<>();
        assertFalse(dq.equals(null));
    }

    @Test
    public void testEqualsWrongType() {
        ArrayDeque<String> dq = new ArrayDeque<>();
        dq.addLast("a");
        assertFalse(dq.equals("a string"));
    }

    @Test
    public void testCircularWrap() {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        for (int i = 0; i < 8; i++) {
            dq.addLast(i);
        }
        dq.removeFirst();
        dq.removeFirst();
        dq.addLast(8);
        dq.addLast(9);

        assertEquals(8, dq.size());
        assertEquals(Integer.valueOf(2), dq.get(0));
        assertEquals(Integer.valueOf(9), dq.get(7));
    }

}
