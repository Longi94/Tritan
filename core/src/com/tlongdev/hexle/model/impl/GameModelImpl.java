package com.tlongdev.hexle.model.impl;

import com.tlongdev.hexle.Config;
import com.tlongdev.hexle.model.Field;
import com.tlongdev.hexle.model.GameModel;
import com.tlongdev.hexle.renderer.GameRenderer;

/**
 * @author longi
 * @since 2016.04.13.
 */
public class GameModelImpl implements GameModel {

    private GameRenderer renderer;

    private Field field;

    public GameModelImpl() {
        init();
    }

    private void init() {
        field = new Field(Config.FIELD_COLUMNS, Config.FIELD_ROWS);
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

    @Override
    public void setField(Field field) {
        this.field = field;
        renderer.notifyModelChanged();
    }
}
