package com.tlongdev.hexle.controller.impl;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.tlongdev.hexle.Consts;
import com.tlongdev.hexle.controller.GameController;
import com.tlongdev.hexle.factory.TileFactory;
import com.tlongdev.hexle.model.Field;
import com.tlongdev.hexle.model.GameModel;
import com.tlongdev.hexle.model.enumeration.SlideDirection;
import com.tlongdev.hexle.renderer.GameRenderer;
import com.tlongdev.hexle.view.FieldView;
import com.tlongdev.hexle.view.TileView;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class GameControllerImpl implements GameController {

    private static final String TAG = GameControllerImpl.class.getSimpleName();

    private Logger logger;

    private GameRenderer renderer;

    private GameModel model;

    private TileFactory tileFactory;

    public GameControllerImpl() {
        init();
    }

    private void init() {
        logger = new Logger(TAG, Logger.DEBUG);
        tileFactory = new TileFactory();
    }

    @Override
    public void startGame() {
        logger.info("startGame");
        model.randomizeField();
    }

    @Override
    public void notifyUserInputFinish() {
        logger.info("notifyUserInputFinish");
        FieldView fieldView = renderer.getFieldView();

        SlideDirection slideDirection = fieldView.getSlideDirection();
        float slideDistance = fieldView.getSlideDistance();
        float tileWidth = fieldView.getTileWidth();
        TileView selected = fieldView.getSelectedTile();

        if (Math.abs(slideDistance) < tileWidth * (1.0f - Consts.SLIDE_THRESHOLD)) {
            //Not enough distance
            fieldView.animateNoMatchSlide();
            return;
        }

        if (Math.abs(slideDistance) % tileWidth > tileWidth * Consts.SLIDE_THRESHOLD &&
                Math.abs(slideDistance) % tileWidth < tileWidth * (1.0f - Consts.SLIDE_THRESHOLD)) {
            //Tiles are not close enough to each other
            fieldView.animateNoMatchSlide();
            return;
        }

        int steps = (int) (slideDistance / tileWidth);
        if (Math.abs(slideDistance) % tileWidth > tileWidth * (1.0f - Consts.SLIDE_THRESHOLD)) {
            if (slideDistance > 0) {
                steps++;
            } else {
                steps--;
            }
        }

        //Create a temporary copy of the field
        Field tempField = model.getField().copy();

        //Apply shift to field
        int rowIndex = selected.getTile().getRowIndex(slideDirection);
        tempField.shift(slideDirection, steps * 2, rowIndex);

        if (tempField.checkField(true)) {

            //The is the offset vector that will make the tile animate into its new place
            Vector2 offset = selected.getCenter().cpy().sub(selected.getOriginCenter());
            float length = slideDistance - tileWidth * steps;
            offset.setLength(length);

            //This is some black magic, not sure what it is yet TODO: 2016.04.14.
            if (length * slideDistance < 0) {
                offset.rotateRad(MathUtils.PI);
            }
            renderer.setSlideOffset(offset);

            //Shift successfully creates a group, apply it
            model.setField(tempField);
        } else {
            //No group
            fieldView.animateNoMatchSlide();
        }
    }

    @Override
    public void notifyShiftAnimationFinish() {
        logger.info("notifyShiftAnimationFinish");
        for (int i = 0; i < Consts.FIELD_ROWS; i++) {
            for (int j = 0; j < Consts.FIELD_COLUMNS; j++) {
                if (model.getField().getTiles()[i][j].isMarked()) {
                    model.getField().getTiles()[i][j] = tileFactory.getBlank(j, i);
                }
            }
        }

        model.getField().generateNewTiles();

        renderer.notifyNewTilesGenerated();
    }

    @Override
    public void notifySlideInAnimationFinish() {
        logger.info("notifySlideInAnimationFinish");

        Field field = model.getField();

        if (field.checkField(true)) {
            for (int i = 0; i < Consts.FIELD_ROWS; i++) {
                for (int j = 0; j < Consts.FIELD_COLUMNS; j++) {
                    if (field.getTiles()[i][j].isMarked()) {
                        field.getTiles()[i][j] = tileFactory.getBlank(j, i);
                    }
                }
            }

            model.getField().generateNewTiles();

            renderer.notifyNewTilesGenerated();
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
