package flik;

import static org.junit.Assert.*;
import org.junit.Test;

public class FilkTest {
    @Test
    public void testIsSameNumberMystery() {
        // --- 测试 1: 小数字区间 ---
        // Java 对 -128 到 127 之间的整数有缓存。
        int num1 = 100;
        int num2 = 100;

        // 我们预期：100 == 100 应该是相同的（True）。
        // 如果这里失败了，说明 Flik 库有基本逻辑错误。
        assertTrue("测试 1 失败：100 应该是相同的数字", Flik.isSameNumber(num1, num2));
        System.out.println("测试 1 通过：100 是相同的（预期内）");

        // --- 测试 2: 边界数字 (Bug 触发点!) ---
        // 注意：Horrible Steve 的程序在 i=128 时退出了循环。
        // 这意味着 Flik.isSameNumber(128, 128) 错误地返回了 FALSE。
        int num3 = 128;
        int num4 = 128;

        // 我们预期：即使超出了默认缓存范围，128 == 128 在逻辑上依然应该是相同的。
        // **当你运行测试时，这行断言非常可能会报错！**
        assertTrue("测试 2 失败：Flik 错误地认为 128 和 128 不同 (i=" + num3 + ")", Flik.isSameNumber(num3, num4));
        System.out.println("测试 2 通过：128 是相同的"); // 这行可能永远不会打印，直到你修复 Bug

        // --- 测试 3: 大数字区间 ---
        // 进一步确认超出缓存范围后的行为。
        int num5 = 500;
        int num6 = 500;

        // 我们预期：500 == 500 应该是相同的。
        assertTrue("测试 3 失败：500 应该是相同的数字", Flik.isSameNumber(num5, num6));
        System.out.println("测试 3 通过：500 是相同的");
    }
}
