package com.tlongdev.hexle.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.tlongdev.hexle.controller.GameController;
import com.tlongdev.hexle.renderer.GameRenderer;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class HexleGameScreen implements Screen {

    private GameController controller;
    private GameRenderer renderer;

    private boolean paused;

    @Override
    public void show() {
        //Set Libgdx log level to DEBUG
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        //Initialize controller and renderer
        controller = new GameController(width, height);
        renderer = new GameRenderer(controller, width, height);

        //Active on start
        paused = false;
    }

    @Override
    public void render(float delta) {
        //Do not update is paused
        if (paused) {
            //Update game world by the time that has passed since last rendered frame.
            controller.update(Gdx.graphics.getDeltaTime());
        }

        //Sets the clear screen color to white
        Gdx.gl.glClearColor(0, 0, 0, 1);

        //Clears the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Render game world to screen
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
