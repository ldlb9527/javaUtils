package com.ldlb.time;


import com.ldlb.utils.StrUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTime {
    public static final String RegISODate = "(\\d{4})-((0[1-9])|(1[0-2]))-((0[1-9])|(([12])[0-9])|(3[0-1]))";
    public static final String RegISOTime = "((([0-1])[0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])";
    public static final String RegISODateTime = RegISODate + " " + RegISOTime;
    public static final String RegISODateTimeMs = RegISODateTime + "(\\.\\d{3})";
    public static final String RegYYYYMMDD = RegISODate.replace(")-(", ")(");
    public static final String RegHHMMSS = RegISOTime.replace("):(", ")(");
    public static final String RegYYYYMMDDhhmmss = RegYYYYMMDD + RegHHMMSS;

    private static String toRegex(String format) {
        if (ISODateTimeMs.equals(format)) {
            return RegISODateTimeMs;
        } else if (ISODateTime.equals(format)) {
            return RegISODateTime;
        } else if (ISODate.equals(format)) {
            return RegISODate;
        } else if (ISOTime.equals(format)) {
            return RegISOTime;
        } else if (YYYYMMDD.equals(format)) {
            return RegYYYYMMDD;
        } else if (YYYYMMDDhhmmss.equals(format)) {
            return RegYYYYMMDDhhmmss;
        } else if (HHMMSS.equals(format)) {
            return RegHHMMSS;
        }
        return format;
    }

    public static String toEmptyRegex(String format) {
        String reg = toRegex(format);
        return reg.equals(format) ? format : ("(" + reg + ")?");
    }

    public static final String ISODateTimeMs = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String ISODateTime = "yyyy-MM-dd HH:mm:ss";
    public static final String ISODate = "yyyy-MM-dd";
    public static final String ISOTime = "HH:mm:ss";
    public static final String YYYYMMDDhhmmss = "yyyyMMddHHmmss";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String HHMMSS = "HHmmss";

    private final long tsLocalNanos;
    private final long fastDate;

    public static DateTime now() {
        return new DateTime();
    }

    public static String uniformTs(String ts) {
        if (ts.matches(RegISODateTimeMs)) {
            return ts;
        }
        if (ts.matches(RegISODateTime)) {
            return ts + ".000";
        }
        if (ts.matches(RegISODate)) {
            return ts + " 00:00:00.000";
        }
        if (ts.matches(RegYYYYMMDDhhmmss + "\\.\\d{3}")) {
            return StrUtils.format("{}-{}-{} {}:{}:{}.{}",
                    ts.substring(0, 4), ts.substring(4, 6), ts.substring(6, 8),
                    ts.substring(8, 10), ts.substring(10, 12), ts.substring(12, 14), ts.substring(14));
        }
        if (ts.matches(RegYYYYMMDDhhmmss)) {
            return StrUtils.format("{}-{}-{} {}:{}:{}.000",
                    ts.substring(0, 4), ts.substring(4, 6), ts.substring(6, 8),
                    ts.substring(8, 10), ts.substring(10, 12), ts.substring(12, 14));
        }
        if (ts.matches(RegYYYYMMDD)) {

            return StrUtils.format("{}-{}-{} 00:00:00.000",
                    ts.substring(0, 4), ts.substring(4, 6), ts.substring(6, 8));
        }
        return ts;
    }


    public DateTime(Date date) {
        this.tsLocalNanos = (date.getTime() / ms2sec) * nano2sec + (date.getTime() % ms2sec) * nano2ms;
        this.fastDate = toYYYYMMDDhhmmss(date);
    }

    public static long toYYYYMMDDhhmmss(Date date) {
        return Long.parseLong(new SimpleDateFormat(YYYYMMDDhhmmss).format(date));
    }

    public DateTime(long timeStamp, long nanos) {
        this(timeStamp * nano2sec + nanos % nano2sec);
    }

    public DateTime() {
        this(System.currentTimeMillis() * nano2ms + System.nanoTime() % nano2ms);
    }

    public DateTime(long timeStampNanos) {
        this.tsLocalNanos = timeStampNanos;
        this.fastDate = toYYYYMMDDhhmmss(new Date(timeStampNanos / nano2ms));
    }


    public static final long nano2us = 1000L;
    public static final long nano2ms = nano2us * 1000;
    public static final long nano2sec = nano2ms * 1000;
    public static final long sec2hour = 3600;
    public static final long sec2day = 86400;
    private static final long fastMod = 100;
    private final static int fastLvlSecond = 0;
    private final static int fastLvlMinute = 1;
    private final static int fastLvlHour = 2;
    private final static int fastLvlDay = 3;
    private final static int fastLvlMonth = 4;
    private final static int fastLvlYear = 5;
    private final static int fastSizeYear = 2;

    private int getField(int level, int min, int max) {
        int size = level >= fastLvlYear ? 2 : 1;
        int value = (int) ((fastDate % pow100(level + size) / pow100(level)) % pow100(size));
        return Math.min(Math.max(value, min), max);
    }

    private long pow100(int size) {
        long value = 1;
        for (; size > 0; size--) value *= 100;
        return value;
    }


    public static final long ms2sec = 1000L;
    public static final Calendar calendar = Calendar.getInstance();

    public static int getTZOffsetMs() {
        calendar.setTimeZone(TimeZone.getDefault());
        return -1 * calendar.get(Calendar.ZONE_OFFSET);
    }

    public static int getTZOffset() {
        return getTZOffsetMs() / (int) ms2sec;
    }


    public static int getDaysOfYear(int year) {
        return isLeapYear(year) ? 366 : 365;
    }

    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    public static int getDaysOfMonth(int month, boolean isLeapYear) {
        if (month == 2) return isLeapYear ? 29 : 28;
        if (month <= 7) return 30 + month % 2;
        return 31 - month % 2;
    }

    public int getYear() {
        return getField(fastLvlYear, 1, 9999);
    }

    public int getDate() {
        return getField(fastLvlDay, 1, 31);
    }

    public int getMonth() {
        return getField(fastLvlMonth, 1, 12);
    }

    public int getMinute() {
        return getField(fastLvlMinute, 0, 59);
    }

    public int getHour() {
        return getField(fastLvlHour, 0, 24);
    }

    public int getSecond() {
        return getField(fastLvlSecond, 0, 59);
    }

    public int getMillisecond() {
        return (int) ((tsLocalNanos / nano2ms) % 1000 + 1000) % 1000;
    }

    public int getMicrosecond() {
        return (int) ((tsLocalNanos / 1000) % 1000 + 1000) % 1000;
    }

    public int getNanosecond() {
        return (int) ((tsLocalNanos) % 1000 + 1000) % 1000;
    }

    public int getTsSec() {
        return (int) (tsLocalNanos / nano2sec);
    }

    public long getTsMs() {
        return tsLocalNanos / nano2ms;
    }

    public long getTsNano() {
        return tsLocalNanos;
    }

    public static DateTime parseSecTS(int timeStamp) {
        return new DateTime(timeStamp * nano2sec);
    }

    public static DateTime parseMsTS(long timeStampMs) {
        return new DateTime(timeStampMs * nano2ms);
    }

    public static DateTime parseFormat(String value, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(value);
            return new DateTime(date);
        } catch (ParseException e) {
            throw new RuntimeException("DateEx parse " + value + " with '" + format + "' failed");
        }
    }


    public static DateTime parseString(String value) {
        return parseFormat(uniformTs(value), ISODateTimeMs);
    }

    public static java.sql.Date toSqlDT(DateTime ldt) {
        java.sql.Date date = java.sql.Date.valueOf(ldt.format(ISODateTime));
        date.setTime(ldt.getTsNano());
        return date;
    }

    public String format(String format) {
        String formatted = format;
        formatted = formatted.contains("NNN") ? formatted.replace("NNN", fix3(getNanosecond() % 1000)) :
                formatted;
        formatted = formatted.contains("SSSSSS") ? formatted.replace("SSSSSS", fix3(getMillisecond() / 1000) + fix3(getMicrosecond() / 1000)) :
                formatted;
        formatted = formatted.contains("SSS") ? formatted.replace("SSS", fix3(getMillisecond())) :
                formatted;
        formatted = formatted.contains("ss") ? formatted.replace("ss", fix2(getSecond())) :
                formatted;
        formatted = formatted.contains("mm") ? formatted.replace("mm", fix2(getMinute())) :
                formatted;
        formatted = formatted.contains("HH") ? formatted.replace("HH", fix2(getHour())) :
                formatted;
        formatted = formatted.contains("hh") ? formatted.replace("hh", fix2(getHour() % 12)) :
                formatted;
        formatted = formatted.contains("dd") ? formatted.replace("dd", fix2(getDate())) :
                formatted;
        formatted = formatted.contains("MM") ? formatted.replace("MM", fix2(getMonth())) :
                formatted;
        formatted = formatted.contains("YYYY") ? formatted.replace("YYYY", fix4(getYear())) :
                formatted;
        formatted = formatted.contains("yyyy") ? formatted.replace("yyyy", fix4(getYear())) :
                formatted;
        return formatted;
    }

    private String fix2(int value) {
        DecimalFormat f2 = (DecimalFormat) DecimalFormat.getInstance();
        f2.applyPattern("00");
        return f2.format(value);
    }

    private String fix3(int value) {
        DecimalFormat f2 = (DecimalFormat) DecimalFormat.getInstance();
        f2.applyPattern("000");
        return f2.format(value);
    }

    private String fix4(int value) {
        DecimalFormat f2 = (DecimalFormat) DecimalFormat.getInstance();
        f2.applyPattern("0000");
        return f2.format(value);
    }

    public String ISODate() {
        return format(ISODate);
    }

    public String ISOTime() {
        return format(ISOTime);
    }

    public String ISODateTime() {
        return format(ISODateTime);
    }

    public String ISODateTimeMs() {
        return format(ISODateTimeMs);
    }

    public long YYYYMMDDhhmmss() {
        return fastDate;
    }

    public int YYYYMMDD() {
        return (int) (fastDate / 1000000);
    }

    public int hhmmss() {
        return (int) (fastDate % 1000000);
    }

    public Date toUtilDate() {
        return new Date(getTsMs());
    }

    public static Date toUtilDate(DateTime ldt) {
        return ldt.toUtilDate();
    }

    @SuppressWarnings("MagicConstant")
    public static java.util.Calendar toUtilCalendar(DateTime ldt) {
        java.util.Calendar c = new GregorianCalendar();
        c.set(ldt.getYear(), ldt.getMonth(), ldt.getDate(), ldt.getHour(), ldt.getMinute(), ldt.getSecond());
        return c;
    }

    public static double sinceNow(DateTime from) {
        return round(sinceNowMs(from) * 1.0 / 1000, 3);
    }

    public static double sinceNow(DateTime from, double offsetSec) {
        return round(sinceNowMs(from) * 1.0 / 1000 - offsetSec, 3);
    }

    public static long sinceNowMs(DateTime from) {
        return diffMs(from, DateTime.now());
    }

    public static double round(double value, int precision) {
        long unit = (long) Math.pow(10, precision);
        return Math.round(value * unit) * 1.0 / unit;
    }

    public static long diffMs(String from, String to) {
        return diffMs(parseString(uniformTs(from)), parseString(uniformTs(to)));
    }

    public static long diffSec(String from, String to) {
        return diffSec(parseString(uniformTs(from)), parseString(uniformTs(to)));
    }

    public static int diffSec(DateTime from, DateTime to) {
        return to.getTsSec() - from.getTsSec();
    }

    public static long diffMs(DateTime from, DateTime to) {
        return to.getTsMs() - from.getTsMs();
    }

    public static long diffNano(DateTime from, DateTime to) {
        return to.getTsNano() - from.getTsNano();
    }

    public static double diffHours(String from, String to) {
        return diffHours(parseString(uniformTs(from)), parseString(uniformTs(to)));
    }

    public static double diffHours(DateTime from, DateTime to) {
        return round(diffSec(from, to) / 3600.0, 3);
    }

    public static double diffDays(String from, String to) {
        return diffDays(parseString(uniformTs(from)), parseString(uniformTs(to)));
    }

    public static double diffDays(DateTime from, DateTime to) {
        return round(diffHours(from, to) / 24.0, 0);
    }


    public static String todayISO() {
        return DateTime.now().format(ISODateTime).substring(0, 10) + " 00:00:00";
    }

    public static String todayYYYYMMDD() {
        return DateTime.now().format(YYYYMMDDhhmmss).substring(0, 8);
    }

    public static String nowYYYYMMDDhhmmss() {
        return DateTime.now().format(YYYYMMDDhhmmss);
    }


    public static String nowISO() {
        return DateTime.now().format(ISODateTime);
    }

    public static String zeroISO() {
        return "1970-01-01 00:00:00";
    }

    public static String minISO() {
        return "0001-01-01 00:00:00";
    }

    public static String maxISO() {
        return "9999-12-31 23:59:59";
    }

    public static String nowISOMs() {
        return DateTime.now().format(ISODateTimeMs);
    }

    public static DateTime yesterday() {
        return moveInYear(today(), 0, 0, -1);
    }

    public static DateTime zero() {
        return parseFormat(zeroISO(), ISODateTime);
    }

    public static DateTime min() {
        return parseFormat(minISO(), ISODateTime);
    }

    public static DateTime max() {
        return parseFormat(maxISO(), ISODateTime);
    }

    public static DateTime today() {
        DateTime now = DateTime.now();
        return beginOfDay(now.getYear(), now.getMonth(), now.getDate());
    }

    public static DateTime beginOfDay(DateTime date) {
        return beginOfDay(date.getYear(), date.getMonth(), date.getDate());
    }

    public static DateTime beginOfDay(int y, int m, int d) {
        String year = (y > 999 ? "" : y > 99 ? "0" : "00") + (y % 10000);
        String month = (m > 9 ? "" : "0") + (m % 13);
        String day = (d > 9 ? "" : "0") + (d % 32);
        return parseFormat(StrUtils.format("{}-{}-{} 00:00:00.000", year, month, day), ISODateTimeMs);
    }

    public DateTime moveInYear(int y, int m, int d) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(toUtilDate());
        rightNow.add(Calendar.YEAR, y);
        rightNow.add(Calendar.MONTH, m);
        rightNow.add(Calendar.DAY_OF_YEAR, d);
        return new DateTime(rightNow.getTime());
    }

    public static DateTime moveInYear(DateTime from, int y, int m, int d) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(from.toUtilDate());
        rightNow.add(Calendar.YEAR, y);
        rightNow.add(Calendar.MONTH, m);
        rightNow.add(Calendar.DAY_OF_YEAR, d);
        return from.moveInYear(y, m, d);
    }

    public static DateTime moveInDay(DateTime from, int h, int m, int s) {
        return from.moveInDay(h, m, s);
    }

    public DateTime moveInDay(int h, int m, int s) {
        int sec = (h * 60 + m) * 60 + s;
        return plusSec(sec);
    }

    public DateTime plusNano(int nano) {
        return new DateTime((getTsNano() + nano));
    }

    public DateTime plusSec(int second) {
        return new DateTime((getTsSec() + second), (int) (getTsNano() % nano2sec));
    }

    public boolean isBefore(DateTime oth) {
        return this.getTsNano() < oth.getTsNano();
    }

    public boolean isAfter(DateTime oth) {
        return this.getTsNano() > oth.getTsNano();
    }

    public static DateTime endOfDay(int y, int m, int d) {
        String year = y > 999 ? "" : y > 99 ? "0" : "00" + (y % 10000);
        String month = (m > 9 ? "" : "0") + (m % 13);
        String day = (d > 9 ? "" : "0") + (d % 32);
        return parseFormat(StrUtils.format("{}-{}-{} 23:59:59.999", year, month, day), ISODateTimeMs);
    }

    public static DateTime beginOfMonth(int year, int month) {
        return beginOfDay(year, month, 1);
    }

    public static DateTime beginOfMonth(DateTime dateTime) {
        return beginOfMonth(dateTime.getYear(), dateTime.getMonth());
    }

    public static DateTime beginOfYear(DateTime dateTime) {
        return beginOfYear(dateTime.getYear());
    }

    public static DateTime beginOfYear(int year) {
        return beginOfDay(year, 1, 1);
    }

}
