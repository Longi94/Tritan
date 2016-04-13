package com.tlongdev.hexle.controller.impl;

import com.badlogic.gdx.utils.Logger;
import com.tlongdev.hexle.controller.GameController;
import com.tlongdev.hexle.model.Field;
import com.tlongdev.hexle.model.GameModel;
import com.tlongdev.hexle.model.SlideDirection;
import com.tlongdev.hexle.renderer.GameRenderer;
import com.tlongdev.hexle.view.FieldView;
import com.tlongdev.hexle.view.TileView;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class GameControllerImpl implements GameController {

    private static final String TAG = GameControllerImpl.class.getName();

    private Logger logger;

    private GameRenderer renderer;

    private GameModel model;

    public GameControllerImpl() {
        init();
    }

    private void init() {
        logger = new Logger(TAG, Logger.DEBUG);
    }

    @Override
    public void startGame() {
        model.randomizeField();
    }

    @Override
    public void notifySlideEnd() {
        FieldView fieldView = renderer.getFieldView();

        SlideDirection slideDirection = fieldView.getSlideDirection();
        float slideDistance = fieldView.getSlideDistance();
        float tileWidth = fieldView.getTileWidth();
        TileView selected = fieldView.getSelectedTile();

        if (Math.abs(slideDistance) < tileWidth * 3.0f / 4.0f) {
            //Not enough distance
            fieldView.noMatch();
            return;
        }

        if (Math.abs(slideDistance) % tileWidth > tileWidth / 4.0f &&
                Math.abs(slideDistance) % tileWidth < tileWidth * 3.0f / 4.0f) {
            //Tiles are not close enough to each other
            fieldView.noMatch();
            return;
        }

        int steps = (int) (slideDistance / tileWidth);
        if (Math.abs(slideDistance) % tileWidth > tileWidth * 3.0f / 4.0f) {
            if (slideDistance > 0) {
                steps++;
            } else {
                steps--;
            }
        }

        //Create a temporary copy of the field
        Field tempField = model.getField().copy();

        //Apply shift to field
        tempField.shift(slideDirection, steps * 2, selected.getTile().getRowIndex(slideDirection));

        if (tempField.checkField()) {
            //Shift successfully creates a group, apply it
            model.setField(tempField);
        } else {
            //No group
            fieldView.noMatch();
        }
    }

    public void update(float dt) {
        renderer.update(dt);
    }

    public void setRenderer(GameRenderer renderer) {
        this.renderer = renderer;
    }

    public void setModel(GameModel model) {
        this.model = model;
    }
}
