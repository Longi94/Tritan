package in.dragonbra.model.impl;

import in.dragonbra.model.Field;
import in.dragonbra.renderer.GameRenderer;

/**
 * @author longi
 * @since 2016.04.13.
 */
public class GameModelImpl implements in.dragonbra.model.GameModel {

    private GameRenderer renderer;

    private Field field;

    public GameModelImpl() {
        init();
    }

    private void init() {
        field = new Field(in.dragonbra.Consts.FIELD_COLUMNS, in.dragonbra.Consts.FIELD_ROWS);
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public void randomizeField() {
        field.randomize();
        renderer.notifyModelChanged();
    }

    public void setRenderer(GameRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void setField(Field field) {
        this.field = field;
        renderer.notifyModelChanged();
    }
}
