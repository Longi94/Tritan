package com.tlongdev.hexle.controller;

/**
 * @author longi
 * @since 2016.04.13.
 */
public interface GameController {
    /**
     * Init the game.
     */
    void startGame();

    /**
     * Called when a valid user input is received.
     */
    void notifyUserInputFinish();

    /**
     * Called when the shifting animation finished.
     */
    void notifyShiftAnimationFinish();

    /**
     * Called after sliding in new tiles finishes
     */
    void notifySlideInAnimationFinish();

    void notifyOrientationChanged(boolean animating);
}
