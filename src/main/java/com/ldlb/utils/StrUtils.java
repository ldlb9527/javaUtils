package com.xcbio.libs.utils;

import com.xcbio.libs.functions.Func1;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.text.DecimalFormat;
import java.util.List;


public class StrUtils {

    public static String errStr(Throwable error) {
        if (error == null) return "error=null";
        return error.getMessage() + StackTraces.asString(error.getStackTrace());
    }

    public static String format(String format, Object... args) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
        return ft.getMessage();
    }

    public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence cs : elements) {
            sb.append(delimiter).append(cs);
        }
        return sb.length() > 0 ? sb.substring(delimiter.length()) : sb.toString();
    }

    public static <T> String join(List<T> strList, Func1<T, String> getter) {
        return join(strList, getter, ",");
    }

    public static <T> String join(List<T> strList, Func1<T, String> getter, String splitter) {
        return join(strList, getter, "\"", splitter);
    }

    public static <T> String join(List<T> strList, Func1<T, String> getter, String container, String splitter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strList.size(); i++) {
            T s = strList.get(i);
            if (i != 0) builder.append(splitter);
            builder.append(container).append(getter.accept(s)).append(container);
        }
        return builder.toString();
    }

    public static String threadStr(Thread thread) {
        return "Thread(" + thread.getName() + "," + thread.getId() + ")";
    }

    public static void printAndThrow(Exception error) throws Exception {
        printError(error);
        throw error;
    }

    public static void printError(Throwable error) {
        System.out.println(StrUtils.errStr(error));
    }

    public static void printError(Thread th, Throwable error) {
        System.out.println(StrUtils.threadStr(th) + " : " + StrUtils.errStr(error));
    }

    public static String repeat(String str, int size) {
        StringBuilder sb = new StringBuilder();
        while (size-- > 0) sb.append(str);
        return sb.toString();
    }

    public static String formatPercent(double raw) {
        return formatVal(raw * 100, "%");
    }

    public static String formatVal(double raw, String suffix) {
        return formatVal(raw) + suffix;
    }

    public static String formatVal(double raw) {
        return formatVal(raw, 3);
    }

    public static String formatVal(double raw, int fixed) {
        String flag = raw > 0 ? "+" : "-";
        fixed = Math.max(fixed, 3);
        int pow = 0;
        double absVal = Math.abs(raw);
        double uniformed = absVal;
        // 将数值归一化到[1,10)
        while (uniformed > 10) {
            pow += 1;
            uniformed /= 10;
        }
        while (uniformed < 1 && uniformed != 0) {
            pow -= 1;
            uniformed *= 10;
        }
        if (pow <= 0) fixed += 1;
        String varStr = "";
        uniformed = RangeUtils.round(uniformed, 4);
        //若位数不超过显示字符数,截断显示
        if (Math.abs(pow) < fixed) {
            if (pow >= 0) {
                varStr = absVal > 10 ? (repeat("0", fixed - pow - 1) + (int) absVal) : ("" + RangeUtils.round(absVal, fixed - 2));
            } else {
                varStr = "" + RangeUtils.round(absVal, fixed - 1);
            }
            varStr = (varStr + repeat("0", fixed - varStr.length()));
        }
        //显示字符数超过3,采用科学计数法显示 (absVal>999 or absVal <0.001)
        else if (pow < 0 || fixed > 3) {
            int pb = Math.abs(pow) < 10 ? 1 : 2;
            int pa = fixed - pb - 2;
            StringBuilder pattern = new StringBuilder("0.");
            while ((pa--) >= 0) pattern.append("0");
            pattern.append("E");
            while ((pb--) > 0) pattern.append("0");
            DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
            format.applyPattern(pattern.toString());
            varStr = format.format(raw).replaceAll("E", pow >= 0 ? "↑" : "↓").replaceAll("-", "");
        } else {
            int p3 = (pow % 3 + 3) % 3;
            varStr = "" + RangeUtils.round(uniformed * Math.pow(10, p3), 3);
            varStr = varStr.substring(0, Math.min(p3 > 1 ? 3 : 4, varStr.length())) + (pow >= 18 ? "Z" : pow >= 15 ? "P" : pow >= 12 ? "T" : pow >= 9 ? "G" : pow >= 6 ? "M" : pow >= 3 ? "K" : "");
        }

//        System.out.println("r:" + raw + ",v:" + varStr + ",p:" + pow + ",f:" + fixed);
        return flag + varStr;
    }

    public static void printAndThrowRuntime(Throwable error) {
        printError(error);
        throw new RuntimeException(error);
    }


    /**
     * Return a default string if given string is null or empty.
     *
     * @param origin       origin string
     * @param defaultValue default value
     * @return if origin == null || origin.isEmpty, return defaultValue, else return origin
     */
    public static String getOrDefault(String origin, String defaultValue) {
        if (origin == null || origin.isEmpty())
            return defaultValue;
        return origin;
    }

    public static String toString(Object object) {
        return object == null ? "" : object.toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }


}
