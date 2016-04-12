package com.tlongdev.hexle.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.tlongdev.hexle.view.BaseView;

/**
 * @author longi
 * @since 2016.04.12.
 */
public class Rectangle implements BaseView {

    private Vector2 base;

    private float width;

    private float height;

    private Color color;

    public Rectangle() {
        base = new Vector2();
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.rect(base.x, base.y, width, height);
    }

    public void setX(float x) {
        base.x = x;
    }

    public void setY(float y) {
        base.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
