package com.qiqi.financial.util;

import java.text.DecimalFormat;
import java.util.Calendar;

public class Util {

    public static void setMonthStartEnd(long timeInMillis, long[] out) {
        if (out.length >= 2) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
            calendar.set(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            out[0] = calendar.getTimeInMillis();
            calendar.roll(Calendar.MONTH, 1);
            // 下月为1月，说明已经跨年了
            if (calendar.get(Calendar.MONTH) == 0) {
                calendar.roll(Calendar.YEAR, 1);
            }
            out[1] = calendar.getTimeInMillis();
        }
    }

    public static String getFormattedValue(float value) {
        return new DecimalFormat("0.##").format(value);
    }
}
