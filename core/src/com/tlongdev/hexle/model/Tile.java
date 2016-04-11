package com.tlongdev.hexle.model;

/**
 * A single tile.
 *
 * @author longi
 * @since 2016.04.09.
 */
public class Tile {

    private int posX;

    private int posY;

    private TileColor tileColor;

    private TileOrientation orientation;

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public TileColor getTileColor() {
        return tileColor;
    }

    public void setTileColor(TileColor tileColor) {
        this.tileColor = tileColor;
    }

    public TileOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(TileOrientation orientation) {
        this.orientation = orientation;
    }

    public boolean isAffectedBySlide(Tile selectedTile, SlideDirection slideDirection) {
        if (selectedTile == null || slideDirection == null) {
            return false;
        }
        int aX = posX;
        int aY = posY;
        int bX = selectedTile.getPosX();
        int bY = selectedTile.getPosY();
        switch (slideDirection) {
            case EAST:
                //Sliding sideways, tile is affected if the Y coordinate is the same
                return aY == bY;
            case NORTH_EAST:
                //Magic to determine whether the tile is in the same left diagonal
                if (bX - aX == bY - aY) {
                    return true;
                }
                switch (getOrientation()) {
                    case UP:
                        return bX - aX - 1 == bY - aY;
                    case DOWN:
                        return bX - aX == bY - aY - 1;
                }
                break;
            case NORTH_WEST:
                //Magic to determine whether the tile is in the same right diagonal
                if (bX - aX == -(bY - aY)) {
                    return true;
                }
                switch (getOrientation()) {
                    case UP:
                        return bX - aX == -(bY - aY) - 1;
                    case DOWN:
                        return bX - aX - 1 == -(bY - aY);
                }
                break;
        }
        return false;
    }

    public enum TileOrientation {
        UP, DOWN
    }
}