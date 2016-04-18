package com.tlongdev.hexle.model.field;

import com.tlongdev.hexle.factory.TileFactory;
import com.tlongdev.hexle.model.Field;
import com.tlongdev.hexle.model.Tile;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author longi
 * @since 2016.04.15.
 */
public class MinimizeGapsTest {

    private static Tile[] row1;
    private static Tile[] row2;
    private static Tile[] row3;
    private static Tile[] row4;

    private static TileFactory factory;

    private Tile[] result1;
    private Tile[] result2;
    private Tile[] result3;
    private Tile[] result4;

    @BeforeClass
    public static void setUpBeforeClass() {
        row1 = new Tile[4];
        row2 = new Tile[8];
        row3 = new Tile[12];
        row4 = new Tile[16];

        factory = new TileFactory();

        for (int i = 0; i < 16; i++) {
            if (i < row1.length) {
                row1[i] = factory.get(0, 0);
            }
            if (i < row2.length) {
                row2[i] = factory.get(0, 0);
            }
            if (i < row3.length) {
                row3[i] = factory.get(0, 0);
            }
            row4[i] = factory.get(0, 0);
        }
    }

    @Before
    public void setUp() throws Exception {

        for (int i = 0; i < 16; i++) {
            if (i < row1.length) {
                row1[i].resetSlideInOffset();
            }
            if (i < row2.length) {
                row2[i].resetSlideInOffset();
            }
            if (i < row3.length) {
                row3[i].resetSlideInOffset();
            }
            row4[i].resetSlideInOffset();
        }

        result1 = row1.clone();
        result2 = row2.clone();
        result3 = row3.clone();
        result4 = row4.clone();
    }

    @Test
    public void testFull() throws Exception {

        //The operation
        Field.minimizeGaps(result1);
        Field.minimizeGaps(result2);
        Field.minimizeGaps(result3);
        Field.minimizeGaps(result4);

        //Assert no change
        assertArrayEquals(row1, result1);
        assertArrayEquals(row2, result2);
        assertArrayEquals(row3, result3);
        assertArrayEquals(row4, result4);
    }

    @Test
    public void testSimpleGameOdd() throws Exception {

        //Insert blanks
        //XXX___XX
        result2[3] = factory.getBlank(0, 0);
        result2[4] = factory.getBlank(0, 0);
        result2[5] = factory.getBlank(0, 0);

        //XXX_____XXXX
        result3[3] = factory.getBlank(0, 0);
        result3[4] = factory.getBlank(0, 0);
        result3[5] = factory.getBlank(0, 0);
        result3[6] = factory.getBlank(0, 0);
        result3[7] = factory.getBlank(0, 0);

        //XXX_______XXXXXX
        result4[3] = factory.getBlank(0, 0);
        result4[4] = factory.getBlank(0, 0);
        result4[5] = factory.getBlank(0, 0);
        result4[6] = factory.getBlank(0, 0);
        result4[7] = factory.getBlank(0, 0);
        result4[8] = factory.getBlank(0, 0);
        result4[9] = factory.getBlank(0, 0);

        //The operation
        Field.minimizeGaps(result2);
        Field.minimizeGaps(result3);
        Field.minimizeGaps(result4);

        //Assert the there is one blank left
        assertTrue(result2[3].isBlank());
        assertTrue(result3[3].isBlank());
        assertTrue(result4[3].isBlank());

        //Assert that tiles stayed in their place
        for (int i = 0; i < 3; i++) {
            assertEquals(row2[i], result2[i]);
            assertEquals(row3[i], result3[i]);
            assertEquals(row4[i], result4[i]);

            assertEquals(0, result2[i].getSlideInOffset());
            assertEquals(0, result3[i].getSlideInOffset());
            assertEquals(0, result4[i].getSlideInOffset());
        }

        //Assert that the tiles properly shifted
        for (int i = 6; i < row2.length; i++) {
            assertEquals(row2[i], result2[i - 2]);
            assertEquals(2, result2[i - 2].getSlideInOffset());
        }

        for (int i = 8; i < row3.length; i++) {
            assertEquals(row3[i], result3[i - 4]);
            assertEquals(4, result3[i - 4].getSlideInOffset());
        }

        for (int i = 10; i < row4.length; i++) {
            assertEquals(row4[i], result4[i - 6]);
            assertEquals(6, result4[i - 6].getSlideInOffset());
        }

        //Assert that everything is null
        for (int i = 0; i < 2; i++) {
            assertNull(result2[result2.length - i - 1]);
        }

        for (int i = 0; i < 4; i++) {
            assertNull(result3[result3.length - i - 1]);
        }

        for (int i = 0; i < 6; i++) {
            assertNull(result4[result4.length - i - 1]);
        }
    }

    @Test
    public void testSimpleGameEven() throws Exception {

        //Insert blanks
        //XXXX__XX
        result2[4] = factory.getBlank(0, 0);
        result2[5] = factory.getBlank(0, 0);

        //XXXX____XXXX
        result3[4] = factory.getBlank(0, 0);
        result3[5] = factory.getBlank(0, 0);
        result3[6] = factory.getBlank(0, 0);
        result3[7] = factory.getBlank(0, 0);

        //XXXX______XXXXXX
        result4[4] = factory.getBlank(0, 0);
        result4[5] = factory.getBlank(0, 0);
        result4[6] = factory.getBlank(0, 0);
        result4[7] = factory.getBlank(0, 0);
        result4[8] = factory.getBlank(0, 0);
        result4[9] = factory.getBlank(0, 0);

        //The operation
        Field.minimizeGaps(result2);
        Field.minimizeGaps(result3);
        Field.minimizeGaps(result4);

        //Assert the there is one blank left
        assertFalse(result2[3].isBlank());
        assertFalse(result3[3].isBlank());
        assertFalse(result4[3].isBlank());

        //Assert that tiles stayed in their place
        for (int i = 0; i < 4; i++) {
            assertEquals(row2[i], result2[i]);
            assertEquals(row3[i], result3[i]);
            assertEquals(row4[i], result4[i]);

            assertEquals(0, result2[i].getSlideInOffset());
            assertEquals(0, result3[i].getSlideInOffset());
            assertEquals(0, result4[i].getSlideInOffset());
        }

        //Assert that the tiles properly shifted
        for (int i = 6; i < row2.length; i++) {
            assertEquals(row2[i], result2[i - 2]);
            assertEquals(2, result2[i - 2].getSlideInOffset());
        }

        for (int i = 8; i < row3.length; i++) {
            assertEquals(row3[i], result3[i - 4]);
            assertEquals(4, result3[i - 4].getSlideInOffset());
        }

        for (int i = 10; i < row4.length; i++) {
            assertEquals(row4[i], result4[i - 6]);
            assertEquals(6, result4[i - 6].getSlideInOffset());
        }

        //Assert that everything is null
        for (int i = 0; i < 2; i++) {
            assertNull(result2[result2.length - i - 1]);
        }

        for (int i = 0; i < 4; i++) {
            assertNull(result3[result3.length - i - 1]);
        }

        for (int i = 0; i < 6; i++) {
            assertNull(result4[result4.length - i - 1]);
        }
    }

    @Test
    public void testEmptyRows() throws Exception {
        //Insert blanks
        for (int i = 0; i < 16; i++) {
            if (i < result1.length) {
                result1[i] = factory.getBlank(0, 0);
            }
            if (i < result2.length) {
                result2[i] = factory.getBlank(0, 0);
            }
            if (i < result3.length) {
                result3[i] = factory.getBlank(0, 0);
            }
            result4[i] = factory.getBlank(0, 0);
        }

        //The operation
        Field.minimizeGaps(result1);
        Field.minimizeGaps(result2);
        Field.minimizeGaps(result3);
        Field.minimizeGaps(result4);

        //Assert that everything is null
        for (int i = 0; i < 16; i++) {
            if (i < result1.length) {
                assertNull(result1[i]);
            }
            if (i < result2.length) {
                assertNull(result2[i]);
            }
            if (i < result3.length) {
                assertNull(result3[i]);
            }
            assertNull(result4[i]);
        }
    }

    @Test
    public void testBeginningOdd() throws Exception {

        //Insert blanks
        //___X
        result1[0] = factory.getBlank(0, 0);
        result1[1] = factory.getBlank(0, 0);
        result1[2] = factory.getBlank(0, 0);

        //___XXXXX
        result2[0] = factory.getBlank(0, 0);
        result2[1] = factory.getBlank(0, 0);
        result2[2] = factory.getBlank(0, 0);

        //_____XXXXXXX
        result3[0] = factory.getBlank(0, 0);
        result3[1] = factory.getBlank(0, 0);
        result3[2] = factory.getBlank(0, 0);
        result3[3] = factory.getBlank(0, 0);
        result3[4] = factory.getBlank(0, 0);

        //_______XXXXXXXXX
        result4[0] = factory.getBlank(0, 0);
        result4[1] = factory.getBlank(0, 0);
        result4[2] = factory.getBlank(0, 0);
        result4[3] = factory.getBlank(0, 0);
        result4[4] = factory.getBlank(0, 0);
        result4[5] = factory.getBlank(0, 0);
        result4[6] = factory.getBlank(0, 0);

        //The operation
        Field.minimizeGaps(result1);
        Field.minimizeGaps(result2);
        Field.minimizeGaps(result3);
        Field.minimizeGaps(result4);

        //Assert the there is one blank left
        assertTrue(result1[0].isBlank());
        assertTrue(result2[0].isBlank());
        assertTrue(result3[0].isBlank());
        assertTrue(result4[0].isBlank());

        //Assert that the tiles properly shifted
        assertEquals(row1[3], result1[1]);
        assertEquals(2, result1[1].getSlideInOffset());

        for (int i = 3; i < row2.length; i++) {
            assertEquals(row2[i], result2[i - 2]);
            assertEquals(2, result2[i - 2].getSlideInOffset());
        }

        for (int i = 5; i < row3.length; i++) {
            assertEquals(row3[i], result3[i - 4]);
            assertEquals(4, result3[i - 4].getSlideInOffset());
        }

        for (int i = 7; i < row4.length; i++) {
            assertEquals(row4[i], result4[i - 6]);
            assertEquals(6, result4[i - 6].getSlideInOffset());
        }

        //Assert that the end is null
        for (int i = 0; i < 2; i++) {
            assertNull(result1[result1.length - i - 1]);
            assertNull(result2[result2.length - i - 1]);
        }

        for (int i = 0; i < 4; i++) {
            assertNull(result3[result3.length - i - 1]);
        }

        for (int i = 0; i < 6; i++) {
            assertNull(result4[result4.length - i - 1]);
        }
    }

    @Test
    public void testEndOdd() throws Exception {

        //Insert blanks
        //X___
        result1[1] = factory.getBlank(0, 0);
        result1[2] = factory.getBlank(0, 0);
        result1[3] = factory.getBlank(0, 0);

        //XXXXX___
        result2[5] = factory.getBlank(0, 0);
        result2[6] = factory.getBlank(0, 0);
        result2[7] = factory.getBlank(0, 0);

        //XXXXXXX_____
        result3[7] = factory.getBlank(0, 0);
        result3[8] = factory.getBlank(0, 0);
        result3[9] = factory.getBlank(0, 0);
        result3[10] = factory.getBlank(0, 0);
        result3[11] = factory.getBlank(0, 0);

        //XXXXXXXXX_______
        result4[9] = factory.getBlank(0, 0);
        result4[10] = factory.getBlank(0, 0);
        result4[11] = factory.getBlank(0, 0);
        result4[12] = factory.getBlank(0, 0);
        result4[13] = factory.getBlank(0, 0);
        result4[14] = factory.getBlank(0, 0);
        result4[15] = factory.getBlank(0, 0);

        //The operation
        Field.minimizeGaps(result1);
        Field.minimizeGaps(result2);
        Field.minimizeGaps(result3);
        Field.minimizeGaps(result4);

        //Assert the there is one blank left
        assertTrue(result1[1].isBlank());
        assertTrue(result2[5].isBlank());
        assertTrue(result3[7].isBlank());
        assertTrue(result4[9].isBlank());

        //Assert that tiles stayed in their place
        assertEquals(row1[0], result1[0]);
        assertEquals(0, result1[0].getSlideInOffset());

        for (int i = 0; i < 5; i++) {
            assertEquals(row2[i], result2[i]);
            assertEquals(0, result2[i].getSlideInOffset());
        }

        for (int i = 0; i < 7; i++) {
            assertEquals(row3[i], result3[i]);
            assertEquals(0, result3[i].getSlideInOffset());
        }

        for (int i = 0; i < 9; i++) {
            assertEquals(row4[i], result4[i]);
            assertEquals(0, result4[i].getSlideInOffset());
        }

        //Assert that the end is null
        for (int i = 0; i < 2; i++) {
            assertNull(result1[result1.length - i - 1]);
            assertNull(result2[result2.length - i - 1]);
        }

        for (int i = 0; i < 4; i++) {
            assertNull(result3[result3.length - i - 1]);
        }

        for (int i = 0; i < 6; i++) {
            assertNull(result4[result4.length - i - 1]);
        }
    }

    @Test
    public void testBeginningEven() throws Exception {

        //Insert blanks
        //__XX
        result1[0] = factory.getBlank(0, 0);
        result1[1] = factory.getBlank(0, 0);

        //__XXXXXX
        result2[0] = factory.getBlank(0, 0);
        result2[1] = factory.getBlank(0, 0);

        //____XXXXXXXX
        result3[0] = factory.getBlank(0, 0);
        result3[1] = factory.getBlank(0, 0);
        result3[2] = factory.getBlank(0, 0);
        result3[3] = factory.getBlank(0, 0);

        //______XXXXXXXXXX
        result4[0] = factory.getBlank(0, 0);
        result4[1] = factory.getBlank(0, 0);
        result4[2] = factory.getBlank(0, 0);
        result4[3] = factory.getBlank(0, 0);
        result4[4] = factory.getBlank(0, 0);
        result4[5] = factory.getBlank(0, 0);

        //The operation
        Field.minimizeGaps(result1);
        Field.minimizeGaps(result2);
        Field.minimizeGaps(result3);
        Field.minimizeGaps(result4);

        //Assert that the tiles properly shifted
        assertEquals(row1[2], result1[0]);
        assertEquals(2, result1[0].getSlideInOffset());
        assertEquals(row1[3], result1[1]);
        assertEquals(2, result1[1].getSlideInOffset());

        for (int i = 2; i < row2.length; i++) {
            assertEquals(row2[i], result2[i - 2]);
            assertEquals(2, result2[i - 2].getSlideInOffset());
        }

        for (int i = 4; i < row3.length; i++) {
            assertEquals(row3[i], result3[i - 4]);
            assertEquals(4, result3[i - 4].getSlideInOffset());
        }

        for (int i = 6; i < row4.length; i++) {
            assertEquals(row4[i], result4[i - 6]);
            assertEquals(6, result4[i - 6].getSlideInOffset());
        }

        //Assert that the end is null
        for (int i = 0; i < 2; i++) {
            assertNull(result1[result1.length - i - 1]);
            assertNull(result2[result2.length - i - 1]);
        }

        for (int i = 0; i < 4; i++) {
            assertNull(result3[result3.length - i - 1]);
        }

        for (int i = 0; i < 6; i++) {
            assertNull(result4[result4.length - i - 1]);
        }
    }

    @Test
    public void testEndEven() throws Exception {

        //Insert blanks
        //XX__
        result1[2] = factory.getBlank(0, 0);
        result1[3] = factory.getBlank(0, 0);

        //XXXXXX__
        result2[6] = factory.getBlank(0, 0);
        result2[7] = factory.getBlank(0, 0);

        //XXXXXXXX____
        result3[8] = factory.getBlank(0, 0);
        result3[9] = factory.getBlank(0, 0);
        result3[10] = factory.getBlank(0, 0);
        result3[11] = factory.getBlank(0, 0);

        //XXXXXXXXXX______
        result4[10] = factory.getBlank(0, 0);
        result4[11] = factory.getBlank(0, 0);
        result4[12] = factory.getBlank(0, 0);
        result4[13] = factory.getBlank(0, 0);
        result4[14] = factory.getBlank(0, 0);
        result4[15] = factory.getBlank(0, 0);

        //The operation
        Field.minimizeGaps(result1);
        Field.minimizeGaps(result2);
        Field.minimizeGaps(result3);
        Field.minimizeGaps(result4);

        //Assert that tiles stayed in their place
        assertEquals(row1[0], result1[0]);
        assertEquals(0, result1[0].getSlideInOffset());
        assertEquals(row1[1], result1[1]);
        assertEquals(0, result1[1].getSlideInOffset());

        for (int i = 0; i < 6; i++) {
            assertEquals(row2[i], result2[i]);
            assertEquals(0, result2[i].getSlideInOffset());
        }

        for (int i = 0; i < 8; i++) {
            assertEquals(row3[i], result3[i]);
            assertEquals(0, result3[i].getSlideInOffset());
        }

        for (int i = 0; i < 10; i++) {
            assertEquals(row4[i], result4[i]);
            assertEquals(0, result4[i].getSlideInOffset());
        }

        //Assert that the end is null
        for (int i = 0; i < 2; i++) {
            assertNull(result1[result1.length - i - 1]);
            assertNull(result2[result2.length - i - 1]);
        }

        for (int i = 0; i < 4; i++) {
            assertNull(result3[result3.length - i - 1]);
        }

        for (int i = 0; i < 6; i++) {
            assertNull(result4[result4.length - i - 1]);
        }
    }

    @Test
    public void testIgnoreSingleGaps() throws Exception {

        //Insert blanks
        //XX_X
        result1[2] = factory.getBlank(0, 0);

        //XXX_XXXX
        result2[3] = factory.getBlank(0, 0);

        //XX_XXXX_XXXX
        result3[2] = factory.getBlank(0, 0);
        result3[7] = factory.getBlank(0, 0);

        //XX_XXX____XXX_XX
        result4[2] = factory.getBlank(0, 0);
        result4[6] = factory.getBlank(0, 0);
        result4[7] = factory.getBlank(0, 0);
        result4[8] = factory.getBlank(0, 0);
        result4[9] = factory.getBlank(0, 0);
        result4[13] = factory.getBlank(0, 0);

        //The operation
        Field.minimizeGaps(result1);
        Field.minimizeGaps(result2);
        Field.minimizeGaps(result3);
        Field.minimizeGaps(result4);

        assertTrue(result1[2].isBlank());

        assertTrue(result2[3].isBlank());

        assertTrue(result3[2].isBlank());
        assertTrue(result3[7].isBlank());

        assertTrue(result4[2].isBlank());
        assertTrue(result4[9].isBlank());
    }

    @Test
    public void testIgnoreSingleGapBeginning() throws Exception {

        //XXX_XXXX
        result2[0] = factory.getBlank(0, 0);

        //XX_XXX____XX
        result3[0] = factory.getBlank(0, 0);
        result3[6] = factory.getBlank(0, 0);
        result3[7] = factory.getBlank(0, 0);
        result3[8] = factory.getBlank(0, 0);
        result3[9] = factory.getBlank(0, 0);

        Field.minimizeGaps(result2);
        Field.minimizeGaps(result3);

        assertTrue(result2[0].isBlank());

        assertTrue(result3[0].isBlank());
    }

    @Test
    public void testOddAndEven() throws Exception {

        //XX___XXX____XXXX
        result4[2] = factory.getBlank(0, 0);
        result4[3] = factory.getBlank(0, 0);
        result4[4] = factory.getBlank(0, 0);
        result4[8] = factory.getBlank(0, 0);
        result4[9] = factory.getBlank(0, 0);
        result4[10] = factory.getBlank(0, 0);
        result4[11] = factory.getBlank(0, 0);

        Field.minimizeGaps(result4);

        assertEquals(row4[0], result4[0]);
        assertEquals(row4[1], result4[1]);

        assertTrue(result4[2].isBlank());

        assertEquals(row4[5], result4[3]);
        assertEquals(row4[6], result4[4]);
        assertEquals(row4[7], result4[5]);

        assertEquals(row4[12], result4[6]);
        assertEquals(row4[13], result4[7]);
        assertEquals(row4[14], result4[8]);
        assertEquals(row4[15], result4[9]);

        for (int i = 0; i < 6; i++) {
            assertNull(result4[result4.length - i - 1]);
        }
    }

    @Test
    public void testOneRemainingEnd() throws Exception {
        //Insert blanks
        for (int i = 0; i < 15; i++) {
            if (i < result1.length - 1) {
                result1[i] = factory.getBlank(0, 0);
            }
            if (i < result2.length - 1) {
                result2[i] = factory.getBlank(0, 0);
            }
            if (i < result3.length - 1) {
                result3[i] = factory.getBlank(0, 0);
            }
            result4[i] = factory.getBlank(0, 0);
        }

        //The operation
        Field.minimizeGaps(result1);
        Field.minimizeGaps(result2);
        Field.minimizeGaps(result3);
        Field.minimizeGaps(result4);

        assertTrue(result1[0].isBlank());
        assertTrue(result2[0].isBlank());
        assertTrue(result3[0].isBlank());
        assertTrue(result4[0].isBlank());

        assertFalse(result1[1].isBlank());
        assertFalse(result2[1].isBlank());
        assertFalse(result3[1].isBlank());
        assertFalse(result4[1].isBlank());


        //Insert blanks
        for (int i = 2; i < 16; i++) {
            if (i < result1.length) {
                assertNull(result1[i]);
            }
            if (i < result2.length) {
                assertNull(result2[i]);
            }
            if (i < result3.length) {
                assertNull(result3[i]);
            }
            assertNull(result4[i]);
        }
    }
}
