package com.tlongdev.hexle.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.tlongdev.hexle.view.BaseView;

/**
 * Simple triangle shape.
 *
 * @author longi
 * @since 2016.04.10.
 */
public class Triangle implements BaseView {

    private Color color;

    private Vector2 a;
    private Vector2 b;
    private Vector2 c;

    public Triangle() {
        a = new Vector2();
        b = new Vector2();
        c = new Vector2();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector2 getA() {
        return a;
    }

    public void setA(float x, float y) {
        a.x = x;
        a.y = y;
    }

    public Vector2 getB() {
        return b;
    }

    public void setB(float x, float y) {
        b.x = x;
        b.y = y;
    }

    public Vector2 getC() {
        return c;
    }

    public void setC(float x, float y) {
        c.x = x;
        c.y = y;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.triangle(a.x, a.y, b.x, b.y, c.x, c.y);
    }
}
