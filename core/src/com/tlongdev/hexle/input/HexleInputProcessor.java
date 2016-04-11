package com.tlongdev.hexle.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Logger;

/**
 * @author Long
 * @since 2016. 04. 11.
 */
public class HexleInputProcessor implements InputProcessor {

    private static final String TAG = HexleInputProcessor.class.getSimpleName();

    private Logger logger;

    private HexleInputListener listener;

    public HexleInputProcessor() {
        logger = new Logger(TAG, Logger.DEBUG);
    }

    @Override
    public boolean keyDown(int keycode) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (listener != null) {
            listener.touchDown(screenX, Gdx.graphics.getHeight() - screenY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (listener != null) {
            listener.touchUp(screenX, Gdx.graphics.getHeight() - screenY);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (listener != null) {
            listener.touchDown(screenX, Gdx.graphics.getHeight() - screenY);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return true;
    }

    public void setListener(HexleInputListener listener) {
        this.listener = listener;
    }

    public interface HexleInputListener {
        void touchDown(int x, int y);
        void touchUp(int x, int y);
    }
}
