package in.dragonbra.renderer;

import com.badlogic.gdx.math.Vector2;

/**
 * @author longi
 * @since 2016.04.13.
 */
public interface GameRenderer extends Renderer {
    void notifyModelChanged();

    void update(float dt);

    in.dragonbra.view.FieldView getFieldView();

    void setSlideOffset(Vector2 offset);

    void notifyNewTilesGenerated();
}
