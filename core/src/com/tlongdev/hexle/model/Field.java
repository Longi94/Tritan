package com.tlongdev.hexle.model;

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

    public Field(int width, int height) {
        this.width = width;
        this.height = height;

        generator = new Random();
        tiles = new Tile[height][width];
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
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Tile tile = new Tile();

                tile.setPosX(j);
                tile.setPosY(i);

                tile.setColor(Color.values()[generator.nextInt(6)]);

                tiles[i][j] = tile;

            }
        }
    }
}