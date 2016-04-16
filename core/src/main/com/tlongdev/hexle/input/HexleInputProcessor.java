package com.tlongdev.hexle.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;
import com.tlongdev.hexle.Config;
import com.tlongdev.hexle.model.enumration.Orientation;
import com.tlongdev.hexle.model.enumration.SlideDirection;

/**
 * Input processor for the standard game.
 *
 * @author Long
 * @since 2016. 04. 11.
 */
public class HexleInputProcessor implements InputProcessor {

    private static final String TAG = HexleInputProcessor.class.getSimpleName();

    private Logger logger;

    private HexleInputListener listener;

    /**
     * Start position of the drag
     */
    private int startX;
    private int startY;

    /**
     * Direction of the drag
     */
    private SlideDirection direction = null;

    private int fingerCount = 0;

    //Accelerometer data
    private float accelX;
    private float accelY;
    private float accelZ;
    private int orientation;

    private Vector3 g;

    public HexleInputProcessor() {
        logger = new Logger(TAG, Logger.DEBUG);
        g = new Vector3();
    }

    @Override
    public boolean keyDown(int keycode) {
        // unused
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        // unused
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        // unused
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        fingerCount++;
        if (fingerCount > 1) {
            return true;
        }
        if (listener != null) {
            //Flip the Y
            listener.touchDown(screenX, Gdx.graphics.getHeight() - screenY);
        }
        startX = screenX;

        //Flip the Y
        startY = Gdx.graphics.getHeight() - screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        fingerCount--;
        if (fingerCount >= 1) {
            return true;
        }
        if (listener != null) {
            //Flip the Y
            listener.touchUp(screenX, Gdx.graphics.getHeight() - screenY);
        }
        direction = null;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        //The current position of the drag
        Vector2 dragged = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);

        //The start position of the drag
        Vector2 start = new Vector2(startX, startY);

        //Vector pointing in the diraction of the drag
        Vector2 dirVector = dragged.cpy().sub(start);
        //Angle of above vector
        float angle = dirVector.angleRad();

        //Calculate the opposite vector
        float opposite = angle - MathUtils.PI;
        if (opposite <= -MathUtils.PI) {
            opposite += MathUtils.PI2;
        }

        //If direction is null a slide hasn't been initiated yet
        if (direction == null) {

            //Distance between the two points
            float dst = start.dst(dragged);

            //Check if the distance if long enough
            if (dst > Config.MIN_DRAG_DISTANCE) {

                //Magic angles
                if (angle >= -MathUtils.PI / 6.0f && angle < MathUtils.PI / 6.0f ||
                        angle >= 5.0f * MathUtils.PI / 6.0f || angle < -5.0f * MathUtils.PI / 6.0f) {
                    //User dragged left/right
                    direction = SlideDirection.SIDEWAYS;
                } else if (angle >= MathUtils.PI / 6.0f && angle < MathUtils.PI / 2.0f ||
                        opposite >= MathUtils.PI / 6.0f && opposite < MathUtils.PI / 2.0f) {
                    //User dragged NE or SW
                    direction = SlideDirection.ANTI_DIAGONAL;
                } else {
                    //User dragged NW or SE
                    direction = SlideDirection.MAIN_DIAGONAL;
                }
                logger.info(direction.toString() + ":" + dst);
            }
        } else {
            //Vector parallel to the slide direction
            Vector2 projectionBase = new Vector2(1, 0);
            switch (direction) {
                case SIDEWAYS:
                    projectionBase.setAngleRad(0);
                    break;
                case ANTI_DIAGONAL:
                    projectionBase.setAngleRad(MathUtils.PI / 3.0f);
                    break;
                case MAIN_DIAGONAL:
                    projectionBase.setAngleRad(2.0f * MathUtils.PI / 3.0f);
                    break;
            }

            //Project the direction vector to the base slide direction vector
            float slideDistance = dirVector.dot(projectionBase) / projectionBase.len();

            //Tell the listener drag is going on
            if (listener != null) {
                listener.touchDragged(direction, slideDistance);
            }
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // unused
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        // unused
        return true;
    }

    public void setListener(HexleInputListener listener) {
        this.listener = listener;
    }

    public float[] updateAccelerometer() {
        accelX = Gdx.input.getAccelerometerX();
        accelY = Gdx.input.getAccelerometerY();
        accelZ = Gdx.input.getAccelerometerZ();
        orientation = Gdx.input.getRotation();

        g.set(-accelX, accelY, accelZ);
        g.nor();

        float inclination = (float) Math.acos(g.z);

        float rotation = MathUtils.atan2(g.y, g.x);

        // TODO: 2016.04.16. return to draw for now
        return new float[]{rotation, inclination};
    }

    public interface HexleInputListener {
        void touchDown(int x, int y);

        void touchUp(int x, int y);

        void touchDragged(SlideDirection direction, float dst);

        void onOrientationChanged(Orientation orientation);
    }
}
