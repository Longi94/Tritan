package com.tlongdev.hexle.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.tlongdev.hexle.controller.GameController;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class GameRenderer implements Disposable{

    private SpriteBatch batch;

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
        batch = new SpriteBatch();
    }

    public void render() {
        batch.begin();
        batch.end();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}