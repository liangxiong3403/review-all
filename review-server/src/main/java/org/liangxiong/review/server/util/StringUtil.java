package org.liangxiong.review.server.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author liangxiong
 * @Date 2019-10-30
 * @Description 字符串工具类
 * @Version 1.0.0
 **/
public class StringUtil {

    public static String substringBeforeLast(String str, String separator) {
        if (!StringUtils.isEmpty(str) && !StringUtils.isEmpty(separator)) {
            int pos = str.lastIndexOf(separator);
            return pos == -1 ? str : str.substring(0, pos);
        } else {
            return str;
        }
    }

    public static String substringAfterLast(String str, String separator) {
        if (StringUtils.isEmpty(str)) {
            return str;
        } else if (StringUtils.isEmpty(separator)) {
            return "";
        } else {
            int pos = str.lastIndexOf(separator);
            return pos != -1 && pos != str.length() - separator.length() ? str.substring(pos + separator.length()) : "";
        }
    }

}
