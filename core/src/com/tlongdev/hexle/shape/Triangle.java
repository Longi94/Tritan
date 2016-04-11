package com.tlongdev.hexle.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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

    private ShapeRenderer shapeRenderer;

    public Triangle(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
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

    public void setA(Vector2 a) {
        this.a = a;
    }

    public Vector2 getB() {
        return b;
    }

    public void setB(Vector2 b) {
        this.b = b;
    }

    public Vector2 getC() {
        return c;
    }

    public void setC(Vector2 c) {
        this.c = c;
    }

    @Override
    public void render() {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.triangle(a.x, a.y, b.x, b.y, c.x, c.y);
        shapeRenderer.end();
    }
}
