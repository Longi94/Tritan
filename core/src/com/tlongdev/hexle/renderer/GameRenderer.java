package com.tlongdev.hexle.renderer;

import com.badlogic.gdx.math.Vector2;
import com.tlongdev.hexle.view.FieldView;

/**
 * @author longi
 * @since 2016.04.13.
 */
public interface GameRenderer extends Renderer {
    void notifyModelChanged();

    void update(float dt);

    FieldView getFieldView();

    void setOffset(Vector2 offset);
}
