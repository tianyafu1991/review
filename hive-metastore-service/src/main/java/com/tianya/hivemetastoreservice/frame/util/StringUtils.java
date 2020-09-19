package com.tianya.hivemetastoreservice.frame.util;

public class StringUtils {

    public static boolean isBlank(String str){
        return org.apache.commons.lang.StringUtils.isBlank(str) || "null".equals(str.toLowerCase());
    }

    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }

    public static boolean isEmpty(String str) {
      return (str == null) || (str.isEmpty());
    }

    public static boolean isNotEmpty(String str) {
      return (str != null) && (!str.isEmpty());
    }

    public static boolean isTrimBlank(String str) {
      return (str == null) || (str.trim().isEmpty());
    }

    public static boolean isNoTrimBlank(String str) {
      return (str != null) && (!str.trim().isEmpty());
    }


}
