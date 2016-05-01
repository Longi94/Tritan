package com.tlongdev.hexle;

import com.badlogic.gdx.math.MathUtils;

/**
 * @author longi
 * @since 2016.04.14.
 */
public class Consts {

    public static final int FIELD_ROWS = 8;
    public static final int FIELD_COLUMNS = 9;

    public static final float SLIDE_DURATION = 500.0f;

    public static final float SLIDE_THRESHOLD = 1.0f / 3.0f;

    /**
     * The minimum distance in pixels needed for the drag to trigger.
     */
    public static final float MIN_DRAG_DISTANCE = 10.0f;

    public static final float MAGIC_SLIDE_CONSTANT = 35.0f;
    public static final float MAGIC_SLIDE_CONSTANT2 = 10.0f;

    public static final float INCLINATION_LIMIT = MathUtils.PI / 12.0f;
}
