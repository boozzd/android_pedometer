package com.example.boozz.pedometer.util;

/**
 * Created by boozz on 2/11/16.
 */
import java.util.Calendar;

public abstract class Util {


    /**
     * @return milliseconds since 1.1.1970 for today 0:00:00
     */
    public static long getToday() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
