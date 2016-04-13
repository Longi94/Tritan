package com.tlongdev.hexle.renderer;

/**
 * @author longi
 * @since 2016.04.13.
 */
public interface GameRenderer extends Renderer {
    void notifyModelChanged();

    void update(float dt);
}
