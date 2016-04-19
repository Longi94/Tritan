package com.tlongdev.hexle.model;

import com.tlongdev.hexle.factory.TileFactory;
import com.tlongdev.hexle.model.Tile.TileOrientation;
import com.tlongdev.hexle.model.enumration.TileColor;
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

    private com.tlongdev.hexle.model.enumration.SlideDirection orientation = com.tlongdev.hexle.model.enumration.SlideDirection.ANTI_DIAGONAL;

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

    public com.tlongdev.hexle.model.enumration.SlideDirection getOrientation() {
        return orientation;
    }

    public void setOrientation(com.tlongdev.hexle.model.enumration.SlideDirection orientation) {
        this.orientation = orientation;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void randomize() {
        List<com.tlongdev.hexle.model.enumration.TileColor> colors = new LinkedList<com.tlongdev.hexle.model.enumration.TileColor>();
        //Fill up the field with random colored tiles
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Tile tile = tileFactory.get(j, i);

                colors.clear();
                Collections.addAll(colors, com.tlongdev.hexle.model.enumration.TileColor.values());

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
            tile.setTileColor(com.tlongdev.hexle.model.enumration.TileColor.values()[generator.nextInt(6)]);
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
    public void shift(com.tlongdev.hexle.model.enumration.SlideDirection slideDirection, int steps, int rowIndex) {

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
            case SIDEWAYS:
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
                fillerTiles[rowIndex].updateIndices();
                break;
            case ANTI_DIAGONAL:
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
                fillerTiles[fillerIndex].updateIndices();

                break;
            case MAIN_DIAGONAL:
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
                fillerTiles[fillerIndex].updateIndices();
                break;
        }
    }

    /**
     * Get the number of tiles in a row (excluding the filler)
     *
     * @param slideDirection the direction if the slite
     * @param rowIndex       the index of the row
     * @return the number of tiles
     */
    public static int getRowTileCount(com.tlongdev.hexle.model.enumration.SlideDirection slideDirection, int rowIndex) {
        return slideDirection == com.tlongdev.hexle.model.enumration.SlideDirection.SIDEWAYS ? 9 :
                3 + Math.min(rowIndex, 7 - rowIndex) * 4;
    }

    /**
     * Get the index of the filler tile in the array
     *
     * @param slideDirection the direction if the slite
     * @param rowIndex       the index of the row
     * @return the index of the filler
     */
    public static int getFillerIndex(com.tlongdev.hexle.model.enumration.SlideDirection slideDirection, int rowIndex) {
        switch (slideDirection) {
            case SIDEWAYS:
                return rowIndex;
            case ANTI_DIAGONAL:
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

    public void generateNewTiles() {
        for (int i = 0; i < 8; i++) {
            slideIn(orientation, i);
        }
    }

    private void slideIn(com.tlongdev.hexle.model.enumration.SlideDirection slideDirection, int rowIndex) {
        //Number of tiles in the row
        int tileCount = getRowTileCount(orientation, rowIndex);

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
            case SIDEWAYS:
                //Create a new array
                System.arraycopy(tiles[rowIndex], 0, tempRow, 0, tileCount);

                minimizeGaps(tempRow);
                randomizeEnd(tempRow, tileFactory, generator);

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
                fillerTiles[rowIndex].updateIndices();
                break;
            case ANTI_DIAGONAL:
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

                minimizeGaps(tempRow);
                randomizeEnd(tempRow, tileFactory, generator);

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
                fillerTiles[fillerIndex].updateIndices();

                break;
            case MAIN_DIAGONAL:
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

                minimizeGaps(tempRow);
                randomizeEnd(tempRow, tileFactory, generator);

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
                fillerTiles[fillerIndex].updateIndices();
                break;
        }
    }

    /**
     * Reduces the gaps in the row to 0 of the length of the gap is even, 1 if odd
     *
     * @param tiles the row tho reduce
     */
    public static void minimizeGaps(Tile[] tiles) {

        Tile[] backup = tiles.clone();

        boolean hasBlank = false;

        for (Tile tile : tiles) {
            if (tile.isBlank()) {
                hasBlank = true;
                break;
            }
        }

        //Do nothing if the row has no gaps
        if (!hasBlank) {
            return;
        }

        //This will contain the total number null tiles at the end of the array
        int totalShifts = 0;

        //This will be reduced by the amount of nulls so we don't unnecessarily iterate to those
        int remainingLength = tiles.length;
        for (int i = 0; i < remainingLength; i++) {

            //Find a gap
            if (tiles[i].isBlank()) {
                int blankCount = 0;

                //Count the number of blanks and nulls
                for (int j = i; j < remainingLength && tiles[j].isBlank(); j++) {
                    blankCount++;
                }

                //Holes with the size of 1 cannote be filled
                if (blankCount == 1) {
                    continue;
                }

                //If the number of blank tiles is odd, on tile must be left blank
                if (blankCount % 2 == 1) {
                    blankCount--;
                    i++;
                }

                remainingLength -= blankCount;
                totalShifts += blankCount;

                //Shift the tiles down, so there is only 1 or 0 blank tiles left in the current gap
                System.arraycopy(tiles, i + blankCount, tiles, i, remainingLength - i);

                for (int j = i; j < remainingLength; j++) {
                    tiles[j].addSlideInOffset(blankCount);
                }
            }
        }

        try {
            //Nullify the end of the row
            for (int i = 1; i <= totalShifts; i++) {
                tiles[tiles.length - i] = null;
            }
        } catch (Exception e) {
            String s = "";
            for (Tile tile : backup) {
                s += tile.toString();
            }
            throw new RuntimeException(s, e);
        }
    }

    public static void randomizeEnd(Tile[] tiles, TileFactory tileFactory, Random generator) {
        //Fill up the nulls with blank tiles
        int i = tiles.length - 1;
        int slideInOffset = 0;
        while (i >= 0 && tiles[i] == null) {
            tiles[i] = tileFactory.get(0, 0);
            tiles[i].setTileColor(TileColor.values()[generator.nextInt(6)]);
            i--;
            slideInOffset++;
        }

        for (int j = 1; j <= slideInOffset; j++) {
            tiles[tiles.length - j].addSlideInOffset(slideInOffset);
        }
    }
}