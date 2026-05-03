package com.jiangtao.smsbilling.util;

import java.math.BigDecimal;

public class SmsCountUtil {
    public static final int SINGLE_SMS_MAX_CHARS = 70;
    public static final int MULTI_SMS_SEGMENT_CHARS = 67;

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
        if (chars <= SINGLE_SMS_MAX_CHARS) return 1;
        return (int) Math.ceil(chars / (double) MULTI_SMS_SEGMENT_CHARS);
    }

    public static BigDecimal calcFee(int segments, BigDecimal price) {
        return price.multiply(new BigDecimal(segments));
    }
}
