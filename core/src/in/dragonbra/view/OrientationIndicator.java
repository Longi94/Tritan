package in.dragonbra.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import in.dragonbra.model.enumeration.Orientation;
import in.dragonbra.shape.Triangle;

/**
 * @author longi
 * @since 2016.04.19.
 */
public class OrientationIndicator implements BaseView {

    private Vector2 center;
    private float radius;

    private Orientation orientation;
    private Triangle triangle;

    public OrientationIndicator() {
        center = new Vector2();
        triangle = new Triangle();
        triangle.setColor(Color.WHITE);
        orientation = Orientation.NONE;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {

        shapeRenderer.setColor(Color.WHITE);
        if (orientation == Orientation.NONE) {
            shapeRenderer.circle(center.x, center.y, radius / 2.0f);
            return;
        }

        float angle;

        switch (orientation) {
            case WEST:
                angle = MathUtils.PI;
                break;
            case NORTH_EAST:
                angle = MathUtils.PI / 3.0f;
                break;
            case NORTH_WEST:
                angle = 2.0f * MathUtils.PI / 3.0f;
                break;
            case SOUTH_EAST:
                angle = -MathUtils.PI / 3.0f;
                break;
            case SOUTH_WEST:
                angle = -2.0f * MathUtils.PI / 3.0f;
                break;
            default:
                angle = 0;
                break;
        }

        triangle.setA(
                center.x + MathUtils.cos(angle + MathUtils.PI) * radius,
                center.y + MathUtils.sin(angle + MathUtils.PI) * radius
        );

        triangle.setB(
                center.x + MathUtils.cos(angle + MathUtils.PI / 6.0f) * radius,
                center.y + MathUtils.sin(angle + MathUtils.PI / 6.0f) * radius
        );

        triangle.setC(
                center.x + MathUtils.cos(angle - MathUtils.PI / 6.0f) * radius,
                center.y + MathUtils.sin(angle - MathUtils.PI / 6.0f) * radius
        );

        triangle.render(shapeRenderer);
    }

    public Vector2 getCenter() {
        return center;
    }

    public void setCenter(Vector2 center) {
        center.set(center);
    }

    public void setCenter(float x, float y) {
        center.set(x, y);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }
}
