package in.dragonbra.renderer;

/**
 * @author longi
 * @since 2016.04.13.
 */
public interface Renderer {
    /**
     * Render everything.
     */
    void render();

    /**
     * Called when the screen is resized.
     *
     * @param width  the width of the screen
     * @param height the height of the screen
     */
    void resize(int width, int height);
}
