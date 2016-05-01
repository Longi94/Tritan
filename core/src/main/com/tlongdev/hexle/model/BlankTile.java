package com.tlongdev.hexle.model;

/**
 * @author longi
 * @since 2016.04.13.
 */
public class BlankTile extends Tile {
    @Override
    public boolean isBlank() {
        return true;
    }

    @Override
    public Tile copy() {
        BlankTile tile = new BlankTile();
        tile.setOrientation(getOrientation());
        tile.setPosY(getPosY());
        tile.setPosX(getPosX());
        tile.updateIndices();
        return tile;
    }

    @Override
    public String toString() {
        // TODO: 2016.04.19. remove me
        return "B";
    }
}
