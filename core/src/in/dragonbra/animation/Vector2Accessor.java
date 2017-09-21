package in.dragonbra.animation;

import com.badlogic.gdx.math.Vector2;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * @author longi
 * @since 2016.04.14.
 */
public class Vector2Accessor implements TweenAccessor<Vector2> {
    public static final int POS_XY = 1;

    @Override
    public int getValues(Vector2 target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POS_XY:
                returnValues[0] = target.x;
                returnValues[1] = target.y;
                return 2;
            default:
                throw new IllegalArgumentException("Unknown tween type: " + tweenType);
        }
    }

    @Override
    public void setValues(Vector2 target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POS_XY:
                target.x = newValues[0];
                target.y = newValues[1];
                break;
            default:
                throw new IllegalArgumentException("Unknown tween type: " + tweenType);
        }

    }
}
