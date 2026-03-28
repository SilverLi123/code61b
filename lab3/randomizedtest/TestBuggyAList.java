package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
  @Test
  public void testThreeAddThreeRemove() {
      AListNoResizing<Integer> correct = new AListNoResizing<>();
      BuggyAList<Integer> broken = new BuggyAList<>();

      correct.addLast(5);
      correct.addLast(10);
      correct.addLast(15);

      broken.addLast(5);
      broken.addLast(10);
      broken.addLast(15);

      assertEquals(correct.size(), broken.size());

      assertEquals(correct.removeLast(), broken.removeLast());
      assertEquals(correct.removeLast(), broken.removeLast());
      assertEquals(correct.removeLast(), broken.removeLast());
  }

  @Test
    public void randomizedTest() {
      AListNoResizing<Integer> L = new AListNoResizing<>();

      int N = 500;
      for (int i = 0; i < N; i += 1) {
          int operationNumber = StdRandom.uniform(0, 2);
          if (operationNumber == 0) {
              // addLast
              int randVal = StdRandom.uniform(0, 100);
              L.addLast(randVal);
              System.out.println("addLast(" + randVal + ")");
          } else if (operationNumber == 1) {
              // size
              int size = L.size();
              System.out.println("size: " + size);
          }
      }
  }

    @Test
    public void randomizedTest1() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4); // 0, 1, 2, 3

            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
            } else if (operationNumber == 1) {
                // size 对比
                assertEquals(correct.size(), broken.size());
            } else if (operationNumber == 2) {
                // getLast 对比 (必须 size > 0)
                if (correct.size() > 0) {
                    assertEquals(correct.getLast(), broken.getLast());
                }
            } else if (operationNumber == 3) {
                // removeLast 对比 (必须 size > 0)
                if (correct.size() > 0) {
                    Integer cLast = correct.removeLast();
                    Integer bLast = broken.removeLast();
                    assertEquals(cLast, bLast);
                }
            }
        }
    }

}
