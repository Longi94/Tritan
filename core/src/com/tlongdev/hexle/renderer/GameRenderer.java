package com.tlongdev.hexle.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.tlongdev.hexle.controller.GameController;
import com.tlongdev.hexle.simple.Triangle;

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

        Triangle triangle = new Triangle();
        triangle.setColor(Color.GREEN);
        triangle.setA(new Vector2(100, 100));
        triangle.setB(new Vector2(100, 200));
        triangle.setC(new Vector2(200, 100));
        triangle.render();

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
