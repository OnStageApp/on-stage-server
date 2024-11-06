package org.onstage.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.Math.toIntExact;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;

public class DateUtils {
    public static Date addHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);
        calendar.add(HOUR, hours);

        return calendar.getTime();
    }

    public static Date addMinutes(Date date, Long minutes) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);
        calendar.add(MINUTE, toIntExact(minutes));

        return calendar.getTime();
    }

    public static String formatDate(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm");
        return date.format(formatter);
    }
}
