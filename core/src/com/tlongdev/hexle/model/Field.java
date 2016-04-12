package com.tlongdev.hexle.model;

import com.tlongdev.hexle.model.Tile.TileOrientation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
        List<TileColor> colors = new LinkedList<TileColor>();
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

                colors.clear();
                Collections.addAll(colors, TileColor.values());

                //Prevent any 3+ groups
                do {
                    int randomColor = generator.nextInt(colors.size());
                    tile.setTileColor(colors.get(randomColor));
                    colors.remove(randomColor);
                } while (checkTile(null, tile, 0, true));

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

    public void checkField() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                checkTile(null, tiles[i][j], 0, false);
            }
        }
    }

    /**
     * Check if there are 3 or more tiles of the same color together.
     *
     * @param source
     * @param current
     * @param depth
     * @param init
     * @return
     */
    private boolean checkTile(Tile source, Tile current, int depth, boolean init) {
        //Don't even bother, they are not the same color
        if (source != null && source.getTileColor() != current.getTileColor()) {
            return false;
        }

        //If we reached a depth of  return true;
        boolean result = depth >= 2;

        //init: check when randomizing the field
        if (init && result) {
            return true;
        }

        //Get the neighbors of the current tile
        Tile neighbor1 = null;
        Tile neighbor2 = null;
        Tile neighbor3 = null;

        //The left neighbor
        if (current.getPosX() != 0) {
            neighbor1 = tiles[current.getPosY()][current.getPosX() - 1];
        }

        //The right neighbor
        if (current.getPosX() != width - 1) {
            neighbor2 = tiles[current.getPosY()][current.getPosX() + 1];
        }

        if (current.getOrientation() == TileOrientation.UP) {
            //The bottom neighbor
            if (current.getPosY() != 0) {
                neighbor3 = tiles[current.getPosY() - 1][current.getPosX()];
            }
        } else {
            //The top neighbor
            if (current.getPosY() != height - 1) {
                neighbor3 = tiles[current.getPosY() + 1][current.getPosX()];
            }
        }

        //Recursive group discovery
        if (neighbor1 != null && (source == null || neighbor1 != source)) {
            if (checkTile(current, neighbor1, depth + 1, init)) {
                result = true;
            }
        }

        if (neighbor2 != null && (source == null || neighbor2 != source)) {
            if (checkTile(current, neighbor2, depth + 1, init)) {
                result = true;
            }
        }

        if (neighbor3 != null && (source == null || neighbor3 != source)) {
            if (checkTile(current, neighbor3, depth + 1, init)) {
                result = true;
            }
        }

        if (result && !init) {
            current.setMarked(true);
        }

        return result;
    }
}