package cn.gt.smsbilling.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SmsCountUtilTest {
    private static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    @Test
    public void testPureChinese() {
        assertEquals(5, SmsCountUtil.countChars("你好世界！"));
    }

    @Test
    public void testPureEnglish() {
        assertEquals(5, SmsCountUtil.countChars("hello"));
    }

    @Test
    public void testMixed() {
        // "hello"=5 + "你好！"=3 = 8
        assertEquals(8, SmsCountUtil.countChars("hello你好！"));
    }

    @Test
    public void testEmoji() {
        // "hi" + grinning face emoji (U+1F600, encoded as surrogate pair \uD83D\uDE00)
        String s = "hi" + new String(new int[]{0x1F600}, 0, 1);
        assertEquals(4, SmsCountUtil.countChars(s));
    }

    @Test
    public void test70CharBoundary() {
        String s = repeat("a", 70);
        assertEquals(1, SmsCountUtil.countBillingSegments(SmsCountUtil.countChars(s)));
    }

    @Test
    public void test71Chars() {
        assertEquals(2, SmsCountUtil.countBillingSegments(71));
    }

    @Test
    public void test150Chars() {
        assertEquals(3, SmsCountUtil.countBillingSegments(150));
    }
}
