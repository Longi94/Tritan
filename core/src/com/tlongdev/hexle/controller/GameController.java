package com.tlongdev.hexle.controller;

import com.tlongdev.hexle.input.HexleInputProcessor;
import com.tlongdev.hexle.model.Field;
import com.tlongdev.hexle.view.GameView;
import com.tlongdev.hexle.view.TileView;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class GameController implements HexleInputProcessor.HexleInputListener {

    private static final String TAG = GameController.class.getName();

    public static final int TILE_COLUMNS = 9;
    public static final int TILE_ROWS = 8;

    private GameView gameView;

    private Field field;

    public GameController() {
        init();
    }

    private void init() {
        field = new Field(TILE_COLUMNS, TILE_ROWS);
        field.randomize();

        gameView = new GameView();

        TileView[][] tileViews = new TileView[TILE_ROWS][TILE_COLUMNS];

        for (int i = 0; i < TILE_ROWS; i++) {
            for (int j = 0; j < TILE_COLUMNS; j++) {
                TileView view = new TileView();
                view.setTile(field.getTiles()[i][j]);
                tileViews[i][j] = view;
            }
        }

        gameView.setTileViews(tileViews);
    }

    public void update(float dt) {

    }

    public GameView getGameView() {
        return gameView;
    }

    @Override
    public void touchDown(int x, int y) {
        gameView.touchDown(x, y);
    }

    @Override
    public void touchUp(int x, int y) {
        gameView.touchUp(x, y);
    }
}
