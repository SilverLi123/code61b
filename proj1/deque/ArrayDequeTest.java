package deque;

import afu.org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    /** 测试1：基本的 add 和 get 组合 */
    public void testAddGet() {
        ArrayDeque<String> dq = new ArrayDeque<>();
        dq.addFirst("middle"); // [middle]
        dq.addFirst("front");  // [front, middle]
        dq.addLast("back");    // [front, middle, back]

        assertEquals("front", dq.get(0));
        assertEquals("middle", dq.get(1));
        assertEquals("back", dq.get(2));
        assertEquals(3, dq.size());
    }

    @Test
    /** 测试2：测试空队列及边界 get */
    public void testEmptyAndBounds() {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        assertTrue("New deque should be empty", dq.isEmpty());
        assertNull("Get on empty deque should return null", dq.get(0));
        assertNull("Out of bounds get should return null", dq.get(100));

        dq.addFirst(1);
        assertFalse("Deque should not be empty", dq.isEmpty());
        assertNull("Negative index should return null", dq.get(-1));
    }

    @Test
    /** 测试3：核心测试——环形绕回逻辑 (Circular Wrap-around)
     * 初始容量 8，我们要填满它，触发指针跨越数组两端
     */
    public void testCircularFill() {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        // 填满 8 个元素
        for (int i = 0; i < 8; i++) {
            dq.addLast(i);
        }
        // 此时 nextLast 应该已经绕回到 nextFirst 附近
        assertEquals(8, dq.size());
        assertEquals(Integer.valueOf(0), dq.get(0));
        assertEquals(Integer.valueOf(7), dq.get(7));

        // 移除一部分再加回去，测试指针持续转圈
        assertEquals(Integer.valueOf(0), dq.removeFirst()); // 移除 0
        assertEquals(Integer.valueOf(1), dq.removeFirst()); // 移除 1
        dq.addLast(8); // 此时 8 应该放在之前 0 或 1 的物理位置上
        dq.addLast(9);

        assertEquals(8, dq.size());
        assertEquals(Integer.valueOf(2), dq.get(0)); // 现在第0个应该是2
        assertEquals(Integer.valueOf(9), dq.get(7)); // 最后一个应该是9
    }

    @Test
    /** 测试4：removeFirst 和 removeLast 的交替操作 */
    public void testRemoveMix() {
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        dq.addFirst(1);
        dq.addLast(2);
        dq.addFirst(0); // [0, 1, 2]

        assertEquals(Integer.valueOf(2), dq.removeLast());  // [0, 1]
        assertEquals(Integer.valueOf(0), dq.removeFirst()); // [1]
        assertEquals(Integer.valueOf(1), dq.get(0));

        dq.removeLast();
        assertTrue("Should be empty after removing all", dq.isEmpty());
        assertEquals(0, dq.size());
    }

    @Test
    /** 测试5：printDeque 视觉检查（手动观察控制台） */
    public void testPrint() {
        ArrayDeque<String> dq = new ArrayDeque<>();
        dq.addLast("a");
        dq.addLast("b");
        dq.addFirst("c");
        System.out.println("Expected: c a b");
        System.out.print("Actual:   ");
        dq.printDeque();
    }

    @Test
    public void testIteratorBasic() {
        LinkedListDeque<Integer> d = new LinkedListDeque<>();
        d.addLast(1);
        d.addLast(2);
        d.addLast(3);

        int[] expected = {1, 2, 3};
        int i = 0;

        for (int x : d) {
            assertEquals(expected[i], x);
            i++;
        }
    }

    @Test
    public void testEqualsSameObject() {
        LinkedListDeque<String> lld = new LinkedListDeque<>();
        lld.addLast("a");
        lld.addLast("b");
        // 和自己比
        assertTrue(lld.equals(lld));
    }

    @Test
    public void testEqualsIdenticalContent() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<>();
        lld1.addLast("a");
        lld1.addLast("b");
        lld1.addLast("c");

        LinkedListDeque<String> lld2 = new LinkedListDeque<>();
        lld2.addLast("a");
        lld2.addLast("b");
        lld2.addLast("c");

        assertTrue(lld1.equals(lld2));
        assertTrue(lld2.equals(lld1));
    }

    @Test
    public void testEqualsDifferentSize() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addLast(1);
        lld1.addLast(2);

        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();
        lld2.addLast(1);

        assertFalse(lld1.equals(lld2));
    }

    @Test
    public void testEqualsDifferentContent() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<>();
        lld1.addLast("a");
        lld1.addLast("b");

        LinkedListDeque<String> lld2 = new LinkedListDeque<>();
        lld2.addLast("a");
        lld2.addLast("c");

        assertFalse(lld1.equals(lld2));
    }

    @Test
    public void testEqualsEmpty() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<>();
        LinkedListDeque<String> lld2 = new LinkedListDeque<>();
        assertTrue(lld1.equals(lld2));
    }

    @Test
    public void testEqualsNull() {
        LinkedListDeque<String> lld = new LinkedListDeque<>();
        assertFalse(lld.equals(null));
    }

    @Test
    public void testEqualsWrongType() {
        LinkedListDeque<String> lld = new LinkedListDeque<>();
        lld.addLast("a");
        assertFalse(lld.equals("a string"));
    }

}
