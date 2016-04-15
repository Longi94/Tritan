package com.tlongdev.hexle.util;

/**
 * @author longi
 * @since 2016.04.14.
 */
public class Util {

    /**
     * Circular shift an array. If n is 0, it will return the original array. Otherwise this will
     * create a NEW array  and leave the original array untouched.
     *
     * @param array the array to shift
     * @param n     number to shift by
     * @param <T>   the type of the array
     * @return shifted array
     */
    public static <T> T[] shiftArray(T[] array, int n) {
        if (modulo(n, array.length) == 0) {
            return array;
        }

        T[] newArray = array.clone();
        for (int i = 0; i < array.length; i++) {
            newArray[modulo((i + n), newArray.length)] = array[i];
        }

        return newArray;
    }

    /**
     * Module operation. The % operator gives you the remainder, meaning if a is negative, the
     * result will be negative too. This method will return the positive module even if a is
     * negative.
     *
     * @param a a in a mod b
     * @param b b in a mod b
     * @return the modulo
     */
    public static int modulo(int a, int b) {
        return (a % b + b) % b;
    }
}
