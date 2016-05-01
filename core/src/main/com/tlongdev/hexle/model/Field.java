package com.tlongdev.hexle.model;

import com.tlongdev.hexle.Consts;
import com.tlongdev.hexle.factory.TileFactory;
import com.tlongdev.hexle.model.Tile.TileOrientation;
import com.tlongdev.hexle.model.enumeration.Orientation;
import com.tlongdev.hexle.model.enumeration.SlideDirection;
import com.tlongdev.hexle.model.enumeration.TileColor;
import com.tlongdev.hexle.util.Util;

import org.jetbrains.annotations.Contract;

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

    private Orientation orientation = Orientation.NONE;

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

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
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
                tiles[i][j] = tile;

                colors.clear();
                Collections.addAll(colors, TileColor.values());

                //Prevent any 3+ groups
                do {
                    int randomColor = generator.nextInt(colors.size());
                    tile.setTileColor(colors.get(randomColor));
                    colors.remove(randomColor);

                } while (checkField(false));
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
     * @param markIfTrue only mark tiles if set to true
     * @return whether the field has groups
     */
    public boolean checkField(boolean markIfTrue) {
        boolean result = false;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tiles[i][j] != null && checkTile(tiles[i][j], markIfTrue)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Get the neighboring tiles that have the same color of the given tile.
     *
     * @param tile the tile
     * @return numbah
     */
    private List<Tile> getSameColorNeighbors(Tile tile) {
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

        if (neighbor1 != null && tile.getTileColor() == neighbor1.getTileColor()) {
            neighbors.add(neighbor1);
        }

        if (neighbor2 != null && tile.getTileColor() == neighbor2.getTileColor()) {
            neighbors.add(neighbor2);
        }

        if (neighbor3 != null && tile.getTileColor() == neighbor3.getTileColor()) {
            neighbors.add(neighbor3);
        }

        return neighbors;
    }

    /**
     * Check if the tile has 2 ore more tiles of the same color
     *
     * @param tile       the current tile
     * @param markIfTrue only mark tiles if set to true
     * @return if there is a group
     */
    private boolean checkTile(Tile tile, boolean markIfTrue) {
        //Blanco
        if (tile.isBlank()) {
            return false;
        }

        //The same color neighbors
        List<Tile> colorNeighbors = getSameColorNeighbors(tile);

        if (tile.isMarked() || colorNeighbors.size() >= 2) {
            if (markIfTrue) {
                tile.setMarked(true);

                for (Tile neighbor : colorNeighbors) {
                    neighbor.setMarked(true);
                }
            }
            return true;
        }
        return false;
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
        field.setOrientation(orientation);
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
        Tile[] tempRow = getRow(slideDirection, rowIndex);

        //Shift and replace
        replaceRow(slideDirection, rowIndex, Util.shiftArray(tempRow, steps));
    }

    /**
     * Get the number of tiles in a row (excluding the filler)
     *
     * @param slideDirection the direction if the slite
     * @param rowIndex       the index of the row
     * @return the number of tiles
     */
    public static int getRowTileCount(SlideDirection slideDirection, int rowIndex) {
        return slideDirection == SlideDirection.SIDEWAYS ? 9 :
                3 + Math.min(rowIndex, 7 - rowIndex) * 4;
    }

    /**
     * Get the index of the filler tile in the array
     *
     * @param slideDirection the direction if the slite
     * @param rowIndex       the index of the row
     * @return the index of the filler
     */
    @Contract(pure = true)
    public static int getFillerIndex(SlideDirection slideDirection, int rowIndex) {
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

    public boolean generateNewTiles() {
        if (orientation == Orientation.NONE) {
            return false;
        }

        boolean newTiles = false;

        for (int i = 0; i < 8; i++) {
            newTiles = slideIn(orientation, i) || newTiles;
        }

        return newTiles;
    }

    private boolean slideIn(Orientation orientation, int rowIndex) {
        //Create a temporary row that will store all the tiles and the filler
        Tile[] tempRow = getRow(orientation, rowIndex);

        //Shrink holes
        minimizeGaps(tempRow);

        int newTiles = randomizeEnd(tempRow, tileFactory, generator);

        //Insert new tiles
        replaceRow(orientation, rowIndex, tempRow);

        return newTiles > 0;
    }

    /**
     * Reduces the gaps in the row to 0 of the length of the gap is even, 1 if odd
     *
     * @param tiles the row tho reduce
     */
    public static void minimizeGaps(Tile[] tiles) {
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

        //Nullify the end of the row
        for (int i = 1; i <= totalShifts; i++) {
            tiles[tiles.length - i] = null;
        }
    }

    public static int randomizeEnd(Tile[] tiles, TileFactory tileFactory, Random generator) {
        //Fill up the nulls with blank tiles
        int i = tiles.length - 1;
        int slideInOffset = 0;
        while (i >= 0 && tiles[i] == null) {
            tiles[i] = tileFactory.get(0, 0);
            tiles[i].setTileColor(TileColor.values()[generator.nextInt(6)]);
            i--;
            slideInOffset++;
        }

        //The list tile doesn't get a slide offset
        for (int j = 2; j <= slideInOffset; j++) {
            tiles[tiles.length - j].addSlideInOffset(slideInOffset);
        }

        return slideInOffset;
    }

    private Tile[] getRow(SlideDirection direction, int rowIndex) {

        //Number of tiles in the row
        int tileCount = getRowTileCount(direction, rowIndex);

        //Create a temporary row that will store all the tiles and the filler
        Tile[] temp = new Tile[tileCount + 1];

        //Position of the filler
        int fillerIndex = getFillerIndex(direction, rowIndex);

        //Add the filler to the end of the new array
        temp[tileCount] = fillerTiles[fillerIndex];

        int x = getRowStartX(direction, rowIndex);
        int y = getRowStartY(direction, rowIndex);

        switch (direction) {
            case SIDEWAYS:
                //Create a new array
                System.arraycopy(tiles[rowIndex], 0, temp, 0, tileCount);
                break;
            case ANTI_DIAGONAL:
                //Create a new array
                for (int i = 0; i < tileCount; i++) {
                    temp[i] = tiles[y][x];
                    if (temp[i].getOrientation() == TileOrientation.UP) {
                        x++;
                    } else {
                        y++;
                    }
                }
                break;
            case MAIN_DIAGONAL:
                //Create a new array
                for (int i = 0; i < tileCount; i++) {
                    temp[i] = tiles[y][x];
                    if (temp[i].getOrientation() == TileOrientation.UP) {
                        x--;
                    } else {
                        y++;
                    }
                }
                break;
        }

        return temp;
    }

    private Tile[] getRow(Orientation orientation, int rowIndex) {
        Tile[] row;
        switch (orientation) {
            case EAST:
                row = getRow(SlideDirection.SIDEWAYS, rowIndex);
                return row;
            case WEST:
                row = getRow(SlideDirection.SIDEWAYS, rowIndex);
                Util.reversArrayExceptLast(row);
                return row;
            case NORTH_EAST:
                row = getRow(SlideDirection.ANTI_DIAGONAL, rowIndex);
                return row;
            case NORTH_WEST:
                row = getRow(SlideDirection.MAIN_DIAGONAL, rowIndex);
                return row;
            case SOUTH_EAST:
                row = getRow(SlideDirection.MAIN_DIAGONAL, rowIndex);
                Util.reversArrayExceptLast(row);
                return row;
            case SOUTH_WEST:
                row = getRow(SlideDirection.ANTI_DIAGONAL, rowIndex);
                Util.reversArrayExceptLast(row);
                return row;
            default:
                throw new IllegalArgumentException(orientation.toString() + " not allowed");
        }
    }

    private void replaceRow(SlideDirection direction, int rowIndex, Tile[] newRow) {
        //Number of tiles in the row
        int tileCount = getRowTileCount(direction, rowIndex);

        //Position of the filler
        int fillerIndex = getFillerIndex(direction, rowIndex);

        int x = getRowStartX(direction, rowIndex);
        int y = getRowStartY(direction, rowIndex);

        switch (direction) {
            case SIDEWAYS:
                //Apply changes, update indices
                for (int i = 0; i < tileCount; i++) {
                    tiles[rowIndex][i] = newRow[i];
                    tiles[rowIndex][i].setPosX(i);
                    tiles[rowIndex][i].setPosY(rowIndex);
                    tiles[rowIndex][i].updateIndices();
                }

                //Apply filler (it's the last in the array)
                fillerTiles[rowIndex] = newRow[tileCount];
                fillerTiles[rowIndex].setPosX(-1);
                fillerTiles[rowIndex].setPosY(rowIndex);
                fillerTiles[rowIndex].updateIndices();
                break;
            case ANTI_DIAGONAL:
                //Create a new array
                for (int i = 0; i < tileCount; i++) {
                    tiles[y][x] = newRow[i];
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
                fillerTiles[fillerIndex] = newRow[tileCount];
                fillerTiles[fillerIndex].setPosX(-1);
                fillerTiles[fillerIndex].setPosY(fillerIndex);
                fillerTiles[fillerIndex].updateIndices();
                break;
            case MAIN_DIAGONAL:
                //Create a new array
                for (int i = 0; i < tileCount; i++) {
                    tiles[y][x] = newRow[i];
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
                fillerTiles[fillerIndex] = newRow[tileCount];
                fillerTiles[fillerIndex].setPosX(-1);
                fillerTiles[fillerIndex].setPosY(fillerIndex);
                fillerTiles[fillerIndex].updateIndices();
                break;
        }
    }

    private void replaceRow(Orientation orientation, int rowIndex, Tile[] newRow) {
        switch (orientation) {
            case EAST:
                replaceRow(SlideDirection.SIDEWAYS, rowIndex, newRow);
                break;
            case WEST:
                Util.reversArrayExceptLast(newRow);
                replaceRow(SlideDirection.SIDEWAYS, rowIndex, newRow);
                break;
            case NORTH_EAST:
                replaceRow(SlideDirection.ANTI_DIAGONAL, rowIndex, newRow);
                break;
            case NORTH_WEST:
                replaceRow(SlideDirection.MAIN_DIAGONAL, rowIndex, newRow);
                break;
            case SOUTH_EAST:
                Util.reversArrayExceptLast(newRow);
                replaceRow(SlideDirection.MAIN_DIAGONAL, rowIndex, newRow);
                break;
            case SOUTH_WEST:
                Util.reversArrayExceptLast(newRow);
                replaceRow(SlideDirection.ANTI_DIAGONAL, rowIndex, newRow);
                break;
            default:
                throw new IllegalArgumentException(orientation.toString() + " not allowed");
        }
    }

    @Contract(pure = true)
    private static int getRowStartX(SlideDirection direction, int rowIndex) {
        switch (direction) {
            case ANTI_DIAGONAL:
                if (rowIndex < 4) {
                    return 0;
                } else {
                    return 2 * rowIndex - 7;
                }
            case MAIN_DIAGONAL:
                if (rowIndex < 4) {
                    return 1 + rowIndex * 2;
                } else {
                    return Consts.FIELD_COLUMNS - 1;
                }
            default:
                return 0;
        }
    }

    @Contract(pure = true)
    private static int getRowStartY(SlideDirection direction, int rowIndex) {
        switch (direction) {
            case ANTI_DIAGONAL:
                if (rowIndex < 4) {
                    return 6 - rowIndex * 2;
                } else {
                    return 0;
                }
            case MAIN_DIAGONAL:
                if (rowIndex < 4) {
                    return 0;
                } else {
                    return 2 * rowIndex - 8;
                }
            default:
                return 0;
        }
    }
}