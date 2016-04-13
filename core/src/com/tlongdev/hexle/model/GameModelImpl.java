package com.tlongdev.hexle.model;

import com.tlongdev.hexle.renderer.GameRenderer;

/**
 * @author longi
 * @since 2016.04.13.
 */
public class GameModelImpl implements GameModel {

    public static final int TILE_COLUMNS = 9;
    public static final int TILE_ROWS = 8;

    private GameRenderer renderer;

    private Field field;

    public GameModelImpl() {
        init();
    }

    private void init() {
        field = new Field(TILE_COLUMNS, TILE_ROWS);
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public void randomizeField() {
        field.randomize();
        renderer.notifyModelChanged();
    }

    public void setRenderer(GameRenderer renderer) {
        this.renderer = renderer;
    }
}
