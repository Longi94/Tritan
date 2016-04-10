package com.tlongdev.hexle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.tlongdev.hexle.controller.GameController;
import com.tlongdev.hexle.renderer.GameRenderer;

public class HexleGame implements ApplicationListener {

    private static final String TAG = HexleGame.class.getName();

    private GameController controller;
    private GameRenderer renderer;

    private boolean paused;

    @Override
    public void create() {
        //Set Libgdx log level to DEBUG
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        //Initialize controller and renderer
        controller = new GameController();
        renderer = new GameRenderer(controller);

        //Active on start
        paused = false;
    }

    @Override
    public void render() {
        //Do not pdate is paused
        if (paused) {
            //Update game world by the time that has passed since last rendered frame.
            controller.update(Gdx.graphics.getDeltaTime());
        }

        //Sets the clear screen color to white
        Gdx.gl.glClearColor(1, 1, 1, 1);

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
    public void dispose() {
        renderer.dispose();
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }
}
