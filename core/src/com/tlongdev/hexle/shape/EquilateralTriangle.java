package com.tlongdev.hexle.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tlongdev.hexle.view.BaseView;

/**
 * And equilateral triangle.
 *
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
        //updatePoints();
    }

    public float getSide() {
        return side;
    }

    public void setSide(float side) {
        this.side = side;
        //updatePoints();
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        //updatePoints();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        // TODO: 2016.04.12. this shouldn't be in the render method
        // TODO: 2016.04.12. optimize so setters are not called for every render
        updatePoints();
        triangle.setColor(color);
        triangle.render(shapeRenderer);
    }

    private void updatePoints() {
        float h = side / ((float) Math.sqrt(3));

        //Calculate the 3 points of the triangle based on the center point, the length of the
        //triangle sides and the rotation.

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
    }
}
