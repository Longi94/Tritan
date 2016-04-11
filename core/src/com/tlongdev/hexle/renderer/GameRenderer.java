package com.tlongdev.hexle.renderer;

import com.badlogic.gdx.utils.Disposable;
import com.tlongdev.hexle.controller.GameController;
import com.tlongdev.hexle.view.GameView;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class GameRenderer implements Disposable{

    private GameController controller;

    private int width;

    private int height;

    public GameRenderer(GameController controller, int width, int height) {
        this.controller = controller;
        this.width = width;
        this.height = height;
        init();
    }

    private void init() {
    }

    public void render() {
        //Render the game
        GameView view = controller.getGameView();
        view.setDimensions(width, height);
        view.render();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void dispose() {
        controller.getGameView().dispose();
    }
}
