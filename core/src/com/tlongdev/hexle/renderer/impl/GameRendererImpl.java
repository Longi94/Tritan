package com.tlongdev.hexle.renderer.impl;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Disposable;
import com.tlongdev.hexle.animation.TileViewAccessor;
import com.tlongdev.hexle.controller.GameController;
import com.tlongdev.hexle.input.HexleInputProcessor;
import com.tlongdev.hexle.model.Field;
import com.tlongdev.hexle.model.GameModel;
import com.tlongdev.hexle.model.SlideDirection;
import com.tlongdev.hexle.renderer.GameRenderer;
import com.tlongdev.hexle.view.FieldView;
import com.tlongdev.hexle.view.TileView;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import static com.tlongdev.hexle.model.impl.GameModelImpl.TILE_COLUMNS;
import static com.tlongdev.hexle.model.impl.GameModelImpl.TILE_ROWS;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class GameRendererImpl implements GameRenderer, Disposable,
        HexleInputProcessor.HexleInputListener, FieldView.OnAnimationListener {

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

        Tween.registerAccessor(TileView.class, new TileViewAccessor());
        tweenManager = new TweenManager();
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

        fieldView = new FieldView(tweenManager);
        fieldView.setAnimationListener(this);

        TileView[][] tileViews = new TileView[TILE_ROWS][TILE_COLUMNS];
        TileView[] fillerTileViews = new TileView[TILE_ROWS];

        for (int i = 0; i < TILE_ROWS; i++) {
            for (int j = 0; j < TILE_COLUMNS; j++) {
                TileView view = new TileView();
                view.setTile(field.getTiles()[i][j]);
                tileViews[i][j] = view;
            }
            TileView fillerView = new TileView();
            fillerView.setTile(field.getFillerTiles()[i]);
            fillerTileViews[i] = fillerView;
        }

        fieldView.setTileViews(tileViews);
        fieldView.setFillerTileViews(fillerTileViews);
    }

    @Override
    public void update(float dt) {
        tweenManager.update(dt);
    }

    @Override
    public void touchDown(int x, int y) {
        //Only forward input if animation is not happening
        if (!animating) {
            if (fieldView != null) {
                fieldView.touchDown(x, y);
            }
        } else {
            //Touch down whil still animating
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

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void setModel(GameModel model) {
        this.model = model;
    }

    @Override
    public void onAnimationStarted() {
        animating = true;
    }

    @Override
    public void onAnimationFinished() {
        animating = false;
    }
}
