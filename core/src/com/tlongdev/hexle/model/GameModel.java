package com.tlongdev.hexle.model;

/**
 * @author longi
 * @since 2016.04.13.
 */
public interface GameModel {
    Field getField();

    void randomizeField();

    void setField(Field field);
}
