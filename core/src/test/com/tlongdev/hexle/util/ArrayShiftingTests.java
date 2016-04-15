package com.tlongdev.hexle.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author longi
 * @since 2016.04.15.
 */
public class ArrayShiftingTests {

    private Integer[] array;

    @Before
    public void setUp() {
        array = new Integer[]{
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0
        };
    }

    @Test
    public void testForward() {
        Integer[] result = Util.shiftArray(array, 3);
        Integer[] expected = new Integer[] {
                8, 9, 0, 1, 2, 3, 4, 5, 6, 7
        };
        assertArrayEquals(expected, result);
    }

    @Test
    public void testBackWard() {
        Integer[] result = Util.shiftArray(array, -3);
        Integer[] expected = new Integer[] {
                4, 5, 6, 7, 8, 9, 0, 1, 2, 3
        };
        assertArrayEquals(expected, result);
    }
}
