package com.example.savi.auth.utils;

/**
 * Created by Savi on 16-07-2016.
 */

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class DateUtils {

    public static final String DATE_TIME_FORMAT_TYPE_dd_MM_yyyy_hh_mm_a = "dd-MM-yyyy hh:mm a";
    public static final String DATE_TIME_FORMAT_TYPE_dd_MMM_yyyy = "dd MMM yyyy";
    public static final String DATE_TIME_FORMAT_TYPE_hh_mm = "hh:mm";
    public static final String DATE_TIME_FORMAT_TYPE_HH_mm = "HH:mm";
    public static final String DATE_TIME_FORMAT_TYPE_mm = "mm";
    public static final String DATE_TIME_FORMAT_TYPE_dd_MM_yyyy = "dd-MM-yyyy";
    public static final String DATE_TIME_FORMAT_TYPE_hh_mm_a = "hh:mm a";
    public static final String DATE_TIME_FORMAT_TYPE_MMM_dd_h_mm_a = "MMM''dd h:mm a";
    public static final String DATE_TIME_FORMAT_TYPE_dd_MMM_h_mm_a = "dd MMM, h:mm a";
    public static final String DATE_TIME_FORMAT_TYPE_dd_MMM = "dd MMM";
    private static final int VALUE_HOUR = 6;

    public static boolean isSameDay(long timeinmillis) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(timeinmillis);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isLiveDate(long startTimeStamp, long endTimeStamp) {
        Calendar todayCal = Calendar.getInstance();
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(startTimeStamp);
        cal2.setTimeInMillis(endTimeStamp);

        return (todayCal.get(Calendar.YEAR) == cal1.get(Calendar.YEAR) &&
                todayCal.get(Calendar.DAY_OF_YEAR) >= cal1.get(Calendar.DAY_OF_YEAR)) && (todayCal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                todayCal.get(Calendar.DAY_OF_YEAR) <= cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static String getDateFormat(long timeStamp) {
        Calendar cal = Calendar.getInstance();
        if (timeStamp < 1000000000000L) {
            timeStamp *= 1000;
        }
        cal.setTimeInMillis(timeStamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        dateFormat.setCalendar(cal);
        return dateFormat.format(cal.getTime());
    }

    public static String getTimeDifference(long timeinmillis) {
        long difference = Calendar.getInstance().getTimeInMillis() - timeinmillis;
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        return hours > 0 ? hours + " hr " + min + " min" : min + " min";
    }

    public static String getFormattedString(long timeStamp, String type) {
        SimpleDateFormat currentDateFormat = new SimpleDateFormat(type);
        if (TextUtils.isEmpty(String.valueOf(timeStamp))) {
            return String.valueOf(currentDateFormat.format(new Date()));
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeStamp);
            return String.valueOf(currentDateFormat.format(calendar.getTime()));
        }
    }

    public static String getDurationString(long millis) {
        return String.format(Locale.getDefault(), "%d.%d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public static String getTimeString(long millis) {
        return getFormattedString(millis, DATE_TIME_FORMAT_TYPE_hh_mm) + " - " + DateUtils.getFormattedString(millis + (30 * 60 * 1000), DateUtils.DATE_TIME_FORMAT_TYPE_hh_mm);
    }

    public static String getTimeString(long startmillis, long endmIllis) {
        return getFormattedString(startmillis, DATE_TIME_FORMAT_TYPE_hh_mm) + " - " +
                getFormattedString(endmIllis, DATE_TIME_FORMAT_TYPE_hh_mm);
    }

    public static long getWeekStartDate(long timeStamp) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTimeInMillis(timeStamp);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return (c.getTimeInMillis() / 1000) * 1000;
    }

    public static long getWeekEndDate(long timeStamp) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTimeInMillis(timeStamp);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return (c.getTimeInMillis() / 1000) * 1000;
    }

    public static long getPreviousMonthsStartDate(long timeStamp, int months) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTimeInMillis(timeStamp);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.add(Calendar.MONTH, -months);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return (c.getTimeInMillis() / 1000) * 1000;
    }

    public static long getNextMonthsEndDate(long timeStamp, int months) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTimeInMillis(timeStamp);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.add(Calendar.MONTH, months);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return (c.getTimeInMillis() / 1000) * 1000;
    }

}