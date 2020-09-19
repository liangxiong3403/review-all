package org.liangxiong.message.util;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-08-21 21:37
 * @description 日期工具类，处理日期格式化，Date与LocalDateTime转换
 **/
public class DateUtils {

    /**
     * 用于判断日期格式
     */
    private static final ThreadLocal<Map<Integer, Boolean>> DATE_THREAD_LOCAL =
            new ThreadLocal<>();
    /**
     * 缓存SimpleDateFormat,保证线程安全
     */
    private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_FORMAT_THREAD_LOCAL =
            new ThreadLocal<>();

    /**
     * The following patterns are used in {@link #isADateFormat(Integer, String)}
     */
    private static final Pattern date_ptrn1 = Pattern.compile("^\\[\\$\\-.*?\\]");
    private static final Pattern date_ptrn2 = Pattern.compile("^\\[[a-zA-Z]+\\]");
    private static final Pattern date_ptrn3a = Pattern.compile("[yYmMdDhHsS]");
    /**
     * add "\u5e74 \u6708 \u65e5" for Chinese/Japanese date format:2017 \u5e74 2 \u6708 7 \u65e5
     */
    private static final Pattern date_ptrn3b =
            Pattern.compile("^[\\[\\]yYmMdDhHsS\\-T/\u5e74\u6708\u65e5,. :\"\\\\]+0*[ampAMP/]*$");
    /**
     * elapsed time patterns: [h],[m] and [s]
     */
    private static final Pattern date_ptrn4 = Pattern.compile("^\\[([hH]+|[mM]+|[sS]+)\\]");
    /**
     * for format which start with "[DBNum1]" or "[DBNum2]" or "[DBNum3]" could be a Chinese date
     */
    private static final Pattern date_ptrn5 = Pattern.compile("^\\[DBNum(1|2|3)\\]");
    /**
     * for format which start with "年" or "月" or "日" or "时" or "分" or "秒" could be a Chinese date
     */
    private static final Pattern date_ptrn6 = Pattern.compile("(年|月|日|时|分|秒)+");
    /**
     * 具体日期格式常量
     */
    public static final String DATE_FORMAT_10 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_14 = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_17 = "yyyyMMdd HH:mm:ss";
    public static final String DATE_FORMAT_19 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_19_FORWARD_SLASH = "yyyy/MM/dd HH:mm:ss";
    private static final String MINUS = "-";

    private DateUtils() {
    }

    /**
     * 字符串转日期
     *
     * @param dateString 字符串形式日期
     * @param dateFormat 日期格式
     * @return Date类型日期
     * @throws ParseException
     */
    public static Date parseDate(String dateString, String dateFormat) throws ParseException {
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = switchDateFormat(dateString);
        }
        return getCacheDateFormat(dateFormat).parse(dateString);
    }

    /**
     * 字符串转日期,推测日期格式
     *
     * @param dateString 字符串形式日期
     * @return Date类型日期
     * @throws ParseException
     */
    public static Date parseDate(String dateString) throws ParseException {
        return parseDate(dateString, switchDateFormat(dateString));
    }

    /**
     * 日期数据格式化为字符串,默认使用yyyy-MM-dd HH:mm:ss格式
     *
     * @param date 日期
     * @return 格式化以后数据
     */
    public static String format(Date date) {
        return format(date, null);
    }

    /**
     * 使用指定格式来格式化日期
     *
     * @param date       需要被格式化的日期
     * @param dateFormat 指定格式化类型
     * @return
     */
    public static String format(Date date, String dateFormat) {
        if (date == null) {
            return "";
        }
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = DATE_FORMAT_19;
        }
        return getCacheDateFormat(dateFormat).format(date);
    }

    /**
     * Date转LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * localDateTime转Date
     *
     * @param localDateTime
     * @return
     */
    public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 选择日期格式
     *
     * @param dateString
     * @return
     */
    private static String switchDateFormat(String dateString) {
        int length = dateString.length();
        switch (length) {
            case 19:
                if (dateString.contains(MINUS)) {
                    return DATE_FORMAT_19;
                } else {
                    return DATE_FORMAT_19_FORWARD_SLASH;
                }
            case 17:
                return DATE_FORMAT_17;
            case 14:
                return DATE_FORMAT_14;
            case 10:
                return DATE_FORMAT_10;
            default:
                throw new IllegalArgumentException("can not find date format for：" + dateString);
        }
    }

    private static DateFormat getCacheDateFormat(String dateFormat) {
        Map<String, SimpleDateFormat> dateFormatMap = DATE_FORMAT_THREAD_LOCAL.get();
        if (dateFormatMap == null) {
            dateFormatMap = new HashMap<String, SimpleDateFormat>();
            DATE_FORMAT_THREAD_LOCAL.set(dateFormatMap);
        } else {
            SimpleDateFormat dateFormatCached = dateFormatMap.get(dateFormat);
            if (dateFormatCached != null) {
                return dateFormatCached;
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        dateFormatMap.put(dateFormat, simpleDateFormat);
        return simpleDateFormat;
    }

    /**
     * 判断格式化类型是否合法
     *
     * @param formatIndex
     * @param formatString
     * @return
     */
    public static boolean isADateFormat(Integer formatIndex, String formatString) {
        if (formatIndex == null) {
            return false;
        }
        Map<Integer, Boolean> isDateCache = DATE_THREAD_LOCAL.get();
        if (isDateCache == null) {
            isDateCache = new HashMap<Integer, Boolean>();
            DATE_THREAD_LOCAL.set(isDateCache);
        } else {
            Boolean isDateCachedData = isDateCache.get(formatIndex);
            if (isDateCachedData != null) {
                return isDateCachedData;
            }
        }
        boolean isDate = isADateFormatUncached(formatIndex, formatString);
        isDateCache.put(formatIndex, isDate);
        return isDate;
    }

    /**
     * 判断格式化类型是否合法
     *
     * @param formatIndex
     * @param formatString
     * @return
     */
    public static boolean isADateFormatUncached(Integer formatIndex, String formatString) {
        if (isInternalDateFormat(formatIndex)) {
            return true;
        }
        if (StringUtils.isEmpty(formatString)) {
            return false;
        }
        String fs = formatString;
        final int length = fs.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = fs.charAt(i);
            if (i < length - 1) {
                char nc = fs.charAt(i + 1);
                if (c == '\\') {
                    switch (nc) {
                        case '-':
                        case ',':
                        case '.':
                        case ' ':
                        case '\\':
                            // skip current '\' and continue to the next char
                            continue;
                    }
                } else if (c == ';' && nc == '@') {
                    i++;
                    // skip ";@" duplets
                    continue;
                }
            }
            sb.append(c);
        }
        fs = sb.toString();

        if (date_ptrn4.matcher(fs).matches()) {
            return true;
        }
        fs = date_ptrn5.matcher(fs).replaceAll("");
        fs = date_ptrn1.matcher(fs).replaceAll("");
        fs = date_ptrn2.matcher(fs).replaceAll("");
        final int separatorIndex = fs.indexOf(';');
        if (0 < separatorIndex && separatorIndex < fs.length() - 1) {
            fs = fs.substring(0, separatorIndex);
        }
        if (!date_ptrn3a.matcher(fs).find()) {
            return false;
        }
        boolean result = date_ptrn3b.matcher(fs).matches();
        if (result) {
            return true;
        }
        result = date_ptrn6.matcher(fs).find();
        return result;
    }

    /**
     * 判断是否是内部格式
     *
     * @see #isADateFormat(Integer, String)
     */
    public static boolean isInternalDateFormat(int format) {
        switch (format) {
            // Internal Date Formats as described on page 427 in
            // Microsoft Excel Dev's Kit...
            // 14-22
            case 0x0e:
            case 0x0f:
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
            case 0x14:
            case 0x15:
            case 0x16:
                // 27-36
            case 0x1b:
            case 0x1c:
            case 0x1d:
            case 0x1e:
            case 0x1f:
            case 0x20:
            case 0x21:
            case 0x22:
            case 0x23:
            case 0x24:
                // 45-47
            case 0x2d:
            case 0x2e:
            case 0x2f:
                // 50-58
            case 0x32:
            case 0x33:
            case 0x34:
            case 0x35:
            case 0x36:
            case 0x37:
            case 0x38:
            case 0x39:
            case 0x3a:
                return true;
        }
        return false;
    }

    public static void removeThreadLocalCache() {
        DATE_THREAD_LOCAL.remove();
        DATE_FORMAT_THREAD_LOCAL.remove();
    }

}
