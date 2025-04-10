package com.clubfactory.platform.scheduler.common.util;


import java.text.DecimalFormat;
import java.util.Arrays;

public class StringUtils {

    /**
     * This method calls {@link Object#toString()} on the given object, unless the
     * object is an array. In that case, it will use the {@link #arrayToString(Object)}
     * method to create a string representation of the array that includes all contained
     * elements.
     *
     * @param o The object for which to create the string representation.
     * @return The string representation of the object.
     */
    public static String arrayAwareToString(Object o) {
        if (o == null) {
            return "null";
        }
        if (o.getClass().isArray()) {
            return arrayToString(o);
        }

        return o.toString();
    }

    /**
     * Returns a string representation of the given array. This method takes an Object
     * to allow also all types of primitive type arrays.
     *
     * @param array The array to create a string representation for.
     * @return The string representation of the array.
     * @throws IllegalArgumentException If the given object is no array.
     */
    public static String arrayToString(Object array) {
        if (array == null) {
            throw new NullPointerException();
        }

        if (array instanceof int[]) {
            return Arrays.toString((int[]) array);
        }
        if (array instanceof long[]) {
            return Arrays.toString((long[]) array);
        }
        if (array instanceof Object[]) {
            return Arrays.toString((Object[]) array);
        }
        if (array instanceof byte[]) {
            return Arrays.toString((byte[]) array);
        }
        if (array instanceof double[]) {
            return Arrays.toString((double[]) array);
        }
        if (array instanceof float[]) {
            return Arrays.toString((float[]) array);
        }
        if (array instanceof boolean[]) {
            return Arrays.toString((boolean[]) array);
        }
        if (array instanceof char[]) {
            return Arrays.toString((char[]) array);
        }
        if (array instanceof short[]) {
            return Arrays.toString((short[]) array);
        }

        if (array.getClass().isArray()) {
            return "<unknown array type>";
        } else {
            throw new IllegalArgumentException("The given argument is no array.");
        }
    }

    /**
     * 格式化size的大小，TB,GB,MB,KB
     *
     * @param size 传入字节数据
     * @return
     */
    public static String formatSize(Long size) {
        if (size == null) {
            return null;
        }
        long KB = 1024;
        long MB = 1024 * KB;
        long GB = 1024 * MB;
        long TB = 1024 * GB;

        DecimalFormat df = new DecimalFormat("0.00");
        StringBuilder sb = new StringBuilder();
        if (size / TB >= 1) {
            sb.append(df.format(size / (float)TB));
            sb.append("TB");
        } else if (size / GB >= 1) {
            sb.append(df.format(size / (float)GB));
            sb.append("GB");
        } else if (size / MB >= 1) {
            sb.append(df.format(size / (float)MB));
            sb.append("MB");
        }  else if (size / KB >= 1) {
            sb.append(df.format(size / (float)KB));
            sb.append("KB");
        } else {
            sb.append(df.format(size));
            sb.append("B");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(formatSize(1110000L));
    }
}
