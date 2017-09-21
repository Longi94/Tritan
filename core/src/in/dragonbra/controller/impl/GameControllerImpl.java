package in.dragonbra.controller.impl;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import in.dragonbra.model.Field;
import in.dragonbra.model.enumeration.SlideDirection;
import in.dragonbra.renderer.GameRenderer;
import in.dragonbra.view.FieldView;
import in.dragonbra.view.TileView;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class GameControllerImpl implements in.dragonbra.controller.GameController {

    private static final String TAG = GameControllerImpl.class.getSimpleName();

    private Logger logger;

    private GameRenderer renderer;

    private in.dragonbra.model.GameModel model;

    private in.dragonbra.factory.TileFactory tileFactory;

    public GameControllerImpl() {
        init();
    }

    private void init() {
        logger = new Logger(TAG, Logger.DEBUG);
        tileFactory = new in.dragonbra.factory.TileFactory();
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

        if (Math.abs(slideDistance) < tileWidth * (1.0f - in.dragonbra.Consts.SLIDE_THRESHOLD)) {
            //Not enough distance
            fieldView.animateNoMatchSlide();
            return;
        }

        if (Math.abs(slideDistance) % tileWidth > tileWidth * in.dragonbra.Consts.SLIDE_THRESHOLD &&
                Math.abs(slideDistance) % tileWidth < tileWidth * (1.0f - in.dragonbra.Consts.SLIDE_THRESHOLD)) {
            //Tiles are not close enough to each other
            fieldView.animateNoMatchSlide();
            return;
        }

        int steps = (int) (slideDistance / tileWidth);
        if (Math.abs(slideDistance) % tileWidth > tileWidth * (1.0f - in.dragonbra.Consts.SLIDE_THRESHOLD)) {
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
        boolean changed = false;
        for (int i = 0; i < in.dragonbra.Consts.FIELD_ROWS; i++) {
            for (int j = 0; j < in.dragonbra.Consts.FIELD_COLUMNS; j++) {
                if (model.getField().getTiles()[i][j].isMarked()) {
                    changed = true;
                    model.getField().getTiles()[i][j] = tileFactory.getBlank(j, i);
                }
            }
        }

        if (model.getField().generateNewTiles()) {
            renderer.notifyNewTilesGenerated();
        } else if (changed) {
            renderer.notifyModelChanged();
        }
    }

    @Override
    public void notifySlideInAnimationFinish() {
        logger.info("notifySlideInAnimationFinish");

        Field field = model.getField();

        if (field.checkField(true)) {
            boolean changed = false;

            for (int i = 0; i < in.dragonbra.Consts.FIELD_ROWS; i++) {
                for (int j = 0; j < in.dragonbra.Consts.FIELD_COLUMNS; j++) {
                    if (field.getTiles()[i][j].isMarked()) {
                        changed = true;
                        field.getTiles()[i][j] = tileFactory.getBlank(j, i);
                    }
                }
            }

            if (model.getField().generateNewTiles()) {
                renderer.notifyNewTilesGenerated();
            } else if (changed) {
                renderer.notifyModelChanged();
            }
        }
    }

    @Override
    public void notifyOrientationChanged(boolean animating) {
        if (!animating) {
            // TODO: 2016.04.19. this is called way too often
            if (model.getField().generateNewTiles()) {
                renderer.notifyNewTilesGenerated();
            }
        }
    }

    public void update(float dt) {
        renderer.update(dt);
    }

    public void setRenderer(GameRenderer renderer) {
        this.renderer = renderer;
    }

    public void setModel(in.dragonbra.model.GameModel model) {
        this.model = model;
    }
}
