package com.tlongdev.hexle.renderer.impl;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.tlongdev.hexle.Config;
import com.tlongdev.hexle.animation.Vector2Accessor;
import com.tlongdev.hexle.controller.GameController;
import com.tlongdev.hexle.input.HexleInputProcessor;
import com.tlongdev.hexle.model.Field;
import com.tlongdev.hexle.model.GameModel;
import com.tlongdev.hexle.model.SlideDirection;
import com.tlongdev.hexle.renderer.GameRenderer;
import com.tlongdev.hexle.view.FieldView;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class GameRendererImpl implements GameRenderer, Disposable, FieldView.OnAnimationListener,
        HexleInputProcessor.HexleInputListener, FieldView.OnSlideEndListener {

    private GameController controller;

    private GameModel model;

    private ShapeRenderer shapeRenderer;

    private TweenManager tweenManager;

    private FieldView fieldView;

    private boolean animating = false;

    private boolean touchWhileAnim = false;

    public GameRendererImpl() {
        init();
    }

    private void init() {
        shapeRenderer = new ShapeRenderer();

        Tween.registerAccessor(Vector2.class, new Vector2Accessor());
        tweenManager = new TweenManager();

        fieldView = new FieldView(tweenManager);
        fieldView.setAnimationListener(this);
        fieldView.setSlideEndListener(this);
    }

    @Override
    public void render() {
        //Render the game
        shapeRenderer.begin(ShapeType.Filled);
        if (fieldView != null) {
            fieldView.render(shapeRenderer);
        }
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        if (fieldView != null) {
            fieldView.setDimensions(width, height);
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void notifyModelChanged() {
        Field field = model.getField();

        for (int i = 0; i < Config.FIELD_ROWS; i++) {
            for (int j = 0; j < Config.FIELD_COLUMNS; j++) {
                fieldView.getTileViews()[i][j].setTile(field.getTiles()[i][j]);
            }
            fieldView.getFillerTileViews()[i].setTile(field.getFillerTiles()[i]);
        }

        fieldView.animateSlide();
    }

    @Override
    public void update(float dt) {
        tweenManager.update(dt);
    }

    @Override
    public FieldView getFieldView() {
        return fieldView;
    }

    @Override
    public void touchDown(int x, int y) {
        //Only forward input if animation is not happening
        if (!animating) {
            if (fieldView != null) {
                fieldView.touchDown(x, y);
            }
        } else {
            //Touch down while still animating
            touchWhileAnim = true;
        }
    }

    @Override
    public void touchUp(int x, int y) {
        //Only forward input if animation is not happening
        if (!animating) {
            //Don't forward input if dragging started while animating
            if (!touchWhileAnim && fieldView != null) {
                fieldView.touchUp();
            }
            //Released touch while not animating
            touchWhileAnim = false;
        }
    }

    @Override
    public void touchDragged(SlideDirection direction, float dst) {
        //Only forward input if animation is not happening
        //Don't forward input if dragging started while animating
        if (!animating && !touchWhileAnim && fieldView != null) {
            fieldView.setDrag(direction, dst);
        }
    }

    @Override
    public void onAnimationStarted() {
        animating = true;
    }

    @Override
    public void onAnimationFinished() {
        animating = false;
    }

    @Override
    public void onSlideEnd() {
        controller.notifySlideEnd();
    }

    @Override
    public void setOffset(Vector2 offset) {
        fieldView.setSlideVector(offset);
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void setModel(GameModel model) {
        this.model = model;
    }
}
