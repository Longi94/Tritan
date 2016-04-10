package com.tlongdev.hexle.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.tlongdev.hexle.controller.GameController;
import com.tlongdev.hexle.shape.EquilateralTriangle;

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

        EquilateralTriangle triangle = new EquilateralTriangle();
        triangle.setCenter(new Vector2(
                width / 2.0f,
                height /2.0f
        ));

        triangle.setSide(400);
        triangle.setColor(Color.GREEN);
        triangle.setRotation(MathUtils.PI / 2.0f);
        triangle.render();
        triangle.dispose();
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
