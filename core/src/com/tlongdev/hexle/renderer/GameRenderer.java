package com.tlongdev.hexle.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.tlongdev.hexle.controller.GameController;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class GameRenderer implements Disposable{

    private OrthographicCamera camera;

    private SpriteBatch batch;

    private GameController controller;

    public GameRenderer(GameController controller) {
        this.controller = controller;
    }

    private void init() {

    }

    public void render() {

    }

    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {

    }
}
