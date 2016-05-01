package com.tlongdev.hexle.animation;

import com.tlongdev.hexle.view.TileView;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * @author longi
 * @since 2016.04.13.
 */
public class TileViewAccessor implements TweenAccessor<TileView> {

    public static final int POS_XY = 1;
    public static final int SCALE = 2;

    @Override
    public int getValues(TileView target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POS_XY:
                returnValues[0] = target.getCenter().x;
                returnValues[1] = target.getCenter().y;
                return 2;
            case SCALE:
                returnValues[0] = target.getSide();
                return 1;
            default:
                throw new IllegalArgumentException("Unknown tween type: " + tweenType);
        }
    }

    @Override
    public void setValues(TileView target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POS_XY:
                target.getCenter().x = newValues[0];
                target.getCenter().y = newValues[1];
                break;
            case SCALE:
                target.setSide(newValues[0]);
                break;
            default:
                throw new IllegalArgumentException("Unknown tween type: " + tweenType);
        }
    }
}
