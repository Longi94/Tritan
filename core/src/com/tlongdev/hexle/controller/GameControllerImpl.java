package com.tlongdev.hexle.controller;

import com.badlogic.gdx.utils.Logger;
import com.tlongdev.hexle.model.GameModel;
import com.tlongdev.hexle.renderer.GameRenderer;

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

    public void update(float dt) {

    }

    public void setRenderer(GameRenderer renderer) {
        this.renderer = renderer;
    }

    public void setModel(GameModel model) {
        this.model = model;
    }
}
