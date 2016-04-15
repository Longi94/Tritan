package com.tlongdev.hexle.model.field;

import com.tlongdev.hexle.factory.TileFactory;
import com.tlongdev.hexle.model.Field;
import com.tlongdev.hexle.model.Tile;
import com.tlongdev.hexle.model.TileColor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * @author longi
 * @since 2016.04.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class RandomizeEndTest {

    private Tile[] tiles;

    private TileFactory tileFactory;

    @Mock
    private Random generator;

    @Before
    public void setUp() throws Exception {
        tiles = new Tile[10];

        tileFactory = new TileFactory();
        for (int i = 0; i < 10; i++) {
            tiles[i] = tileFactory.get(0, 0);
        }

        when(generator.nextInt(6)).thenReturn(0);
    }

    @Test
    public void testEmpty() {
        for (int i = 0; i < 10; i++) {
            tiles[i] = null;
        }

        Field.randomizeEnd(tiles, tileFactory, generator);

        for (Tile tile: tiles) {
            assertNotNull(tile);
            assertEquals(TileColor.RED, tile.getTileColor());
        }
    }

    @Test
    public void testHalf() {
        for (int i = 5; i < 10; i++) {
            tiles[i] = null;
        }

        Field.randomizeEnd(tiles, tileFactory, generator);

        for (Tile tile: tiles) {
            assertNotNull(tile);
            assertEquals(TileColor.RED, tile.getTileColor());
        }
    }

    @Test
    public void testOneNotNull() {
        for (int i = 1; i < 10; i++) {
            tiles[i] = null;
        }

        Field.randomizeEnd(tiles, tileFactory, generator);

        for (Tile tile: tiles) {
            assertNotNull(tile);
            assertEquals(TileColor.RED, tile.getTileColor());
        }
    }

    @Test
    public void testOneNull() {
        tiles[9] = null;

        Field.randomizeEnd(tiles, tileFactory, generator);

        for (Tile tile: tiles) {
            assertNotNull(tile);
            assertEquals(TileColor.RED, tile.getTileColor());
        }
    }

    @Test
    public void testFull() {
        Field.randomizeEnd(tiles, tileFactory, generator);

        for (Tile tile: tiles) {
            assertNotNull(tile);
            assertEquals(TileColor.RED, tile.getTileColor());
        }
    }
}
