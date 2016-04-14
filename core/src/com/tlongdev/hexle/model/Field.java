package com.tlongdev.hexle.model;

import com.tlongdev.hexle.factory.TileFactory;
import com.tlongdev.hexle.model.Tile.TileOrientation;
import com.tlongdev.hexle.util.Util;

import java.util.ArrayList;
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

    private TileFactory tileFactory;

    private int width;

    private int height;

    private SlideDirection orientation;

    private Tile[][] tiles;

    private Tile[] fillerTiles;

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        init();
    }

    private void init() {
        generator = new Random();
        tiles = new Tile[height][width];
        fillerTiles = new Tile[height];
        tileFactory = new TileFactory();
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
                Tile tile = tileFactory.get(j, i);

                colors.clear();
                Collections.addAll(colors, TileColor.values());

                //Prevent any 3+ groups
                do {
                    int randomColor = generator.nextInt(colors.size());
                    tile.setTileColor(colors.get(randomColor));
                    colors.remove(randomColor);

                } while (checkTile(null, tile, 0, true) || getSameColorNeighbors(tile) > 1);

                tiles[i][j] = tile;
            }

            //Randomize filler tiles
            Tile tile = tileFactory.get(-1, i);
            tile.setTileColor(TileColor.values()[generator.nextInt(6)]);
            fillerTiles[i] = tile;
        }
    }

    public Tile[] getFillerTiles() {
        return fillerTiles;
    }

    /**
     * Check if a field hs groups
     *
     * @return whether the field has groups
     */
    public boolean checkField() {
        boolean result = false;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (checkTile(null, tiles[i][j], 0, false)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Get the number of neighboring tiles that have the same color of the given tile.
     *
     * @param tile the tile
     * @return numbah
     */
    private int getSameColorNeighbors(Tile tile) {
        int count = 0;

        for (Tile neighbor : getNeighbors(tile)) {
            if (neighbor.getTileColor() == tile.getTileColor()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Get the neighbors of the tile. Will not include null values.
     *
     * @param tile the tile
     * @return the neighbors
     */
    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<Tile>();

        //Get the neighbors of the current tile
        Tile neighbor1 = null;
        Tile neighbor2 = null;
        Tile neighbor3 = null;

        //The left neighbor
        if (tile.getPosX() != 0) {
            neighbor1 = tiles[tile.getPosY()][tile.getPosX() - 1];
        }

        //The right neighbor
        if (tile.getPosX() != width - 1) {
            neighbor2 = tiles[tile.getPosY()][tile.getPosX() + 1];
        }

        if (tile.getOrientation() == TileOrientation.UP) {
            //The bottom neighbor
            if (tile.getPosY() != 0) {
                neighbor3 = tiles[tile.getPosY() - 1][tile.getPosX()];
            }
        } else {
            //The top neighbor
            if (tile.getPosY() != height - 1) {
                neighbor3 = tiles[tile.getPosY() + 1][tile.getPosX()];
            }
        }

        if (neighbor1 != null) {
            neighbors.add(neighbor1);
        }

        if (neighbor2 != null) {
            neighbors.add(neighbor2);
        }

        if (neighbor3 != null) {
            neighbors.add(neighbor3);
        }

        return neighbors;
    }

    /**
     * Check if there are 3 or more tiles of the same color together.
     *
     * @param source  the tile the call came from
     * @param current the current tile
     * @param depth   recursion depth
     * @param init    if true, it won't go deeper than 3
     * @return if there is a group
     */
    private boolean checkTile(Tile source, Tile current, int depth, boolean init) {
        //Blanco
        if (current.isBlank()) {
            return false;
        }

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

        //Recursively check the neighbors
        for (Tile neighbor : getNeighbors(current)) {
            if (source == null || neighbor != source) {
                if (checkTile(current, neighbor, depth + 1, init)) {
                    result = true;
                }
            }
        }

        if (result && !init) {
            current.setMarked(true);
        }

        return result;
    }

    /**
     * @return a cpy of this object
     */
    public Field copy() {
        Field field = new Field(width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                field.getTiles()[i][j] = tiles[i][j].copy();
                field.getTiles()[i][j].updateIndices();
            }
            field.getFillerTiles()[i] = fillerTiles[i].copy();
        }
        return field;
    }

    /**
     * Shift a row
     *
     * @param slideDirection the direction to shift
     * @param steps          the number of tiles to shift
     * @param rowIndex       the index of the row to shift
     */
    public void shift(SlideDirection slideDirection, int steps, int rowIndex) {

        //Number of tiles in the row
        int tileCount = getRowTileCount(slideDirection, rowIndex);

        if (Math.abs(steps) == tileCount + 1) {
            return;
        }

        //Create a temporary row that will store all the tiles and the filler
        Tile[] tempRow = new Tile[tileCount + 1];

        //Position of the filler
        int fillerIndex = getFillerIndex(slideDirection, rowIndex);

        //Add the filler to the end of the new array
        tempRow[tileCount] = fillerTiles[fillerIndex];

        int startX;
        int startY;
        int x;
        int y;

        switch (slideDirection) {
            case EAST:
                //Create a new array
                System.arraycopy(tiles[rowIndex], 0, tempRow, 0, tileCount);

                //Shift the array
                tempRow = Util.shiftArray(tempRow, steps);

                //Apply changes, update indices
                for (int i = 0; i < tileCount; i++) {
                    tiles[rowIndex][i] = tempRow[i];
                    tiles[rowIndex][i].setPosX(i);
                    tiles[rowIndex][i].setPosY(rowIndex);
                    tiles[rowIndex][i].updateIndices();
                }

                //Apply filler (it's the last in the array)
                fillerTiles[rowIndex] = tempRow[tileCount];
                fillerTiles[rowIndex].setPosX(-1);
                fillerTiles[rowIndex].setPosY(rowIndex);
                break;
            case NORTH_EAST:
                if (rowIndex < 4) {
                    y = startY = 6 - rowIndex * 2;
                    x = startX = 0;
                } else {
                    y = startY = 0;
                    x = startX = 2 * rowIndex - 7;
                }

                //Create a new array
                for (int i = 0; i < tileCount; i++) {
                    tempRow[i] = tiles[y][x];
                    if (tempRow[i].getOrientation() == TileOrientation.UP) {
                        x++;
                    } else {
                        y++;
                    }
                }

                //Shift the array
                tempRow = Util.shiftArray(tempRow, steps);

                x = startX;
                y = startY;

                //Create a new array
                for (int i = 0; i < tileCount; i++) {
                    tiles[y][x] = tempRow[i];
                    tiles[y][x].setPosY(y);
                    tiles[y][x].setPosX(x);
                    tiles[y][x].updateIndices();
                    if (tiles[y][x].getOrientation() == TileOrientation.UP) {
                        x++;
                    } else {
                        y++;
                    }
                }

                //Apply filler (it's the last in the array)
                fillerTiles[fillerIndex] = tempRow[tileCount];
                fillerTiles[fillerIndex].setPosX(-1);
                fillerTiles[fillerIndex].setPosY(fillerIndex);

                break;
            case NORTH_WEST:
                if (rowIndex < 4) {
                    y = startY = 0;
                    x = startX = 1 + rowIndex * 2;
                } else {
                    y = startY = 2 * rowIndex - 8;
                    x = startX = width - 1;
                }

                //Create a new array
                for (int i = 0; i < tileCount; i++) {
                    tempRow[i] = tiles[y][x];
                    if (tempRow[i].getOrientation() == TileOrientation.UP) {
                        x--;
                    } else {
                        y++;
                    }
                }

                //Shift the array
                tempRow = Util.shiftArray(tempRow, steps);

                x = startX;
                y = startY;

                //Create a new array
                for (int i = 0; i < tileCount; i++) {
                    tiles[y][x] = tempRow[i];
                    tiles[y][x].setPosY(y);
                    tiles[y][x].setPosX(x);
                    tiles[y][x].updateIndices();
                    if (tiles[y][x].getOrientation() == TileOrientation.UP) {
                        x--;
                    } else {
                        y++;
                    }
                }

                //Apply filler (it's the last in the array)
                fillerTiles[fillerIndex] = tempRow[tileCount];
                fillerTiles[fillerIndex].setPosX(-1);
                fillerTiles[fillerIndex].setPosY(fillerIndex);
                break;
        }
    }

    public static int getRowTileCount(SlideDirection slideDirection, int rowIndex) {
        return slideDirection == SlideDirection.EAST ? 9 :
                3 + Math.min(rowIndex, 7 - rowIndex) * 4;
    }

    public static int getFillerIndex(SlideDirection slideDirection, int rowIndex) {
        switch (slideDirection) {
            case EAST:
                return rowIndex;
            case NORTH_EAST:
                if (rowIndex < 4) {
                    return 6 - 2 * rowIndex;
                } else {
                    return 15 - 2 * rowIndex;
                }
            default:
                if (rowIndex < 4) {
                    return rowIndex * 2 + 1;
                } else {
                    return rowIndex * 2 - 8;
                }
        }
    }
}