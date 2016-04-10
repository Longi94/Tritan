package com.tlongdev.hexle.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tlongdev.hexle.view.BaseView;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class EquilateralTriangle implements BaseView {

    private Triangle triangle;

    private Vector2 center;

    private float side;

    private float rotation;

    private Color color;

    public EquilateralTriangle() {
        triangle = new Triangle();
    }

    public Vector2 getCenter() {
        return center;
    }

    public void setCenter(Vector2 center) {
        this.center = center;
    }

    public float getSide() {
        return side;
    }

    public void setSide(float side) {
        this.side = side;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void render() {

        float h = side / ((float) Math.sqrt(3));

        triangle.setA(new Vector2(
                center.x + MathUtils.cos(rotation) * h,
                center.y + MathUtils.sin(rotation) * h
        ));

        triangle.setB(new Vector2(
                center.x + MathUtils.cos(rotation + MathUtils.PI2 / 3.0f) * h,
                center.y + MathUtils.sin(rotation + MathUtils.PI2 / 3.0f) * h
        ));

        triangle.setC(new Vector2(
                center.x + MathUtils.cos(rotation + 2 * MathUtils.PI2 / 3.0f) * h,
                center.y + MathUtils.sin(rotation + 2 * MathUtils.PI2 / 3.0f) * h
        ));

        triangle.setColor(color);
        triangle.render();
    }

    @Override
    public void dispose() {
        triangle.dispose();
    }
}
