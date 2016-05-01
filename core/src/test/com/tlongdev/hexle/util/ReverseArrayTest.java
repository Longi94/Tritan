package com.tlongdev.hexle.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author longi
 * @since 2016.04.19.
 */
public class ReverseArrayTest {

    @Test
    public void testReversArrayExceptLastEven() throws Exception {
        Integer[] result = new Integer[] {
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        };
        Util.reversArrayExceptLast(result);
        Integer[] expected = new Integer[] {
                8, 7, 6, 5, 4, 3, 2, 1, 0, 9
        };
        assertArrayEquals(expected, result);
    }

    @Test
    public void testReversArrayExceptLastOdd() throws Exception {
        Integer[] result = new Integer[] {
                1, 2, 3, 4, 5, 6, 7, 8, 9
        };
        Util.reversArrayExceptLast(result);
        Integer[] expected = new Integer[] {
                8, 7, 6, 5, 4, 3, 2, 1, 9
        };
        assertArrayEquals(expected, result);

    }
}