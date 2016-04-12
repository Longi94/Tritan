package com.tlongdev.hexle.model;

import com.tlongdev.hexle.model.Tile.TileOrientation;

import java.util.Random;

/**
 * @author longi
 * @since 2016.04.09.
 */
public class Field {

    private Random generator;

    private int width;

    private int height;

    private SlideDirection orientation;

    private Tile[][] tiles;

    private Tile[] fillerTiles;

    public Field(int width, int height) {
        this.width = width;
        this.height = height;

        generator = new Random();
        tiles = new Tile[height][width];
        fillerTiles = new Tile[height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public SlideDirection getOrientation() {
        return orientation;
    }

    public void setOrientation(SlideDirection orientation) {
        this.orientation = orientation;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void randomize() {
        //Fill up the field with random colored tiles
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Tile tile = new Tile();

                tile.setPosX(j);
                tile.setPosY(i);
                tile.updateIndices();

                if ((tile.getPosX() + tile.getPosY()) % 2 == 0) {
                    tile.setOrientation(TileOrientation.DOWN);
                } else {
                    tile.setOrientation(TileOrientation.UP);
                }

                tile.setTileColor(TileColor.values()[generator.nextInt(6)]);

                tiles[i][j] = tile;

            }

            //Randomize filler tiles
            Tile tile = new Tile();
            tile.setPosX(-1);
            tile.setPosY(i);
            if (tile.getPosY() % 2 == 0) {
                tile.setOrientation(TileOrientation.UP);
            } else {
                tile.setOrientation(TileOrientation.DOWN);
            }
            tile.setTileColor(TileColor.values()[generator.nextInt(6)]);
            fillerTiles[i] = tile;
        }
    }

    public Tile[] getFillerTiles() {
        return fillerTiles;
    }
}