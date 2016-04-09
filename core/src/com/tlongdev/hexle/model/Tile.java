package com.tlongdev.hexle.model;

/**
 * @author longi
 * @since 2016.04.09.
 */
public class Tile {

    private int posX;

    private int posY;

    private Color color;

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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public TileOrientation getOrientation() {
        if ((posX + posY) % 2 == 0) {
            return TileOrientation.DOWN;
        } else {
            return TileOrientation.UP;
        }
    }

    public enum TileOrientation {
        UP, DOWN
    }
}