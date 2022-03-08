package com.nk.libo.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FutureDate {
    public static long getFutureMillis(long currentMillis, String time) {
        Date date = new Date(currentMillis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (time) {
            case "issue for 1 day": calendar.add(Calendar.DATE, 1); break;
            case "issue for 1 week": calendar.add(Calendar.DATE, 7); break;
            case "issue for 1 month": calendar.add(Calendar.MONTH, 1); break;
            case "issue for 2 month": calendar.add(Calendar.MONTH, 2); break;
            case "issue for 3 month": calendar.add(Calendar.MONTH, 3); break;
            case "issue for 4 month": calendar.add(Calendar.MONTH, 4); break;
            case "issue for 5 month": calendar.add(Calendar.MONTH, 5); break;
            case "issue for 6 month": calendar.add(Calendar.MONTH, 7); break;
            case "issue for 1 year": calendar.add(Calendar.YEAR, 1); break;
        }
        return calendar.getTimeInMillis();
    }

    @SuppressLint("SimpleDateFormat")
    public static String toDate(long currentMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentMillis);

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(calendar.getTime());
    }

    public static boolean isDateGreater(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        Date date = new Date();

        if (date.after(calendar.getTime())) return true;
        return false;
    }
}
