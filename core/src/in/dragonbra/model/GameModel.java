package in.dragonbra.model;

/**
 * @author longi
 * @since 2016.04.13.
 */
public interface GameModel {
    /**
     * @return the field object
     */
    Field getField();

    /**
     * Randomize the field.
     */
    void randomizeField();

    /**
     * Set the field
     *
     * @param field the field object
     */
    void setField(Field field);
}
