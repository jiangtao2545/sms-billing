package com.jiangtao.smsbilling.util;

import java.math.BigDecimal;

public class SmsCountUtil {
    public static int countChars(String content) {
        if (content == null || content.isEmpty()) return 0;
        int count = 0;
        for (int i = 0; i < content.length(); ) {
            int cp = content.codePointAt(i);
            if (Character.isSupplementaryCodePoint(cp)) {
                count += 2;
                i += 2;
            } else {
                count += 1;
                i += 1;
            }
        }
        return count;
    }

    public static int countBillingSegments(int chars) {
        if (chars <= 70) return 1;
        return (int) Math.ceil(chars / 67.0);
    }

    public static BigDecimal calcFee(int segments, BigDecimal price) {
        return price.multiply(new BigDecimal(segments));
    }
}
