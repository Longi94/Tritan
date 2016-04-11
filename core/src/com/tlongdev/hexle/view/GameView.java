package com.tlongdev.hexle.view;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tlongdev.hexle.controller.GameController;
import com.tlongdev.hexle.model.SlideDirection;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class GameView implements BaseView {

    private int screenWidth;
    private int screenHeight;

    private TileView[][] tileViews;

    private TileView selectedTile;

    private SlideDirection slideDirection;
    private float slideDistance;

    @Override
    public void render() {
        //Get the maximum width the tile can fit in the screen
        float width = (float) (screenWidth / Math.ceil(GameController.TILE_COLUMNS / 2.0));

        //Calculate the height from the width (equilateral triangle height from side)
        float height = width * (float) Math.sqrt(3) / 2.0f;

        //Calculate the vertical offset, so the triangles are in the middle of the screen
        float offsetY = (screenHeight - (GameController.TILE_ROWS - 1) * height) / 2.0f;

        //The vector that will translate all the affected tiles
        Vector2 slideVector = new Vector2(slideDistance, 0);

        //Iterate through the tiles
        for (int i = 0; i < GameController.TILE_ROWS; i++) {
            for (int j = 0; j < GameController.TILE_COLUMNS; j++) {
                TileView view = tileViews[i][j];

                //Set the center
                view.setCenter(new Vector2(
                        (j + 1) * width / 2.0f,
                        offsetY + i * height
                ));

                view.setSide(width * 0.9f);

                //If slideDirection is not null the a slide is currently happening
                if (slideDirection != null && view.isAffectedBySlide(selectedTile, slideDirection)) {
                    switch (slideDirection) {
                        case EAST:
                            slideVector.setAngleRad(0);
                            break;
                        case NORTH_EAST:
                            slideVector.setAngleRad(MathUtils.PI / 3.0f);
                            break;
                        case NORTH_WEST:
                            slideVector.setAngleRad(2.0f * MathUtils.PI / 3.0f);
                            break;
                    }

                    //Because setting the length of the vector will always make if face in the
                    //positive direction no matter the distance being negative. Dumb.
                    if (slideDistance < 0) {
                        slideVector.rotateRad(MathUtils.PI);
                    }
                    view.getCenter().add(slideVector);

                    renderDuplicates(view, slideDirection, width);
                }

                view.render();
            }
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < GameController.TILE_ROWS; i++) {
            for (int j = 0; j < GameController.TILE_COLUMNS; j++) {
                tileViews[i][j].dispose();
            }
        }
    }

    /**
     * This will render duplicates of triangles which are currently sliding creating an illusion of
     * a looped shift register.
     *
     * @param original the original tile view
     * @param direction the direction the sliding is going on
     * @param side the (full) size of the triangles side
     */
    private void renderDuplicates(TileView original, SlideDirection direction, float side) {
        Vector2 slideVector = new Vector2(slideDistance, 0);
        Vector2 originalVector = original.getCenter();
        float distance;
        switch (direction) {
            case EAST:
                distance = side * 5.0f;
                slideVector.setAngleRad(0);
                break;
            case NORTH_EAST:
                int rightIndex = original.getTile().getRightDiagonalIndex();
                distance = (1 + Math.min(rightIndex, 7 - rightIndex)) * 2.0f * side;
                slideVector.setAngleRad(MathUtils.PI / 3.0f);
                break;
            default:
                int leftIndex = original.getTile().getLeftDiagonalIndex();
                distance = (1 + Math.min(leftIndex, 7 - leftIndex)) * 2.0f * side;
                slideVector.setAngleRad(2.0f * MathUtils.PI / 3.0f);
                break;
        }
        slideVector.setLength(distance);
        original.setCenter(originalVector.cpy().add(slideVector));
        original.render();

        slideVector.rotateRad(MathUtils.PI);
        original.setCenter(originalVector.cpy().add(slideVector));
        original.render();
        original.setCenter(originalVector);
    }

    public void setDimensions(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public TileView[][] getTileViews() {
        return tileViews;
    }

    public void setTileViews(TileView[][] tileViews) {
        this.tileViews = tileViews;
    }

    public void touchDown(int x, int y) {
        Vector2 touchDown = new Vector2(x, y);
        float minDist = screenHeight;

        //Find the closest tile and mark it as selected
        for (int i = 0; i < GameController.TILE_ROWS; i++) {
            for (int j = 0; j < GameController.TILE_COLUMNS; j++) {
                float dist = touchDown.dst(tileViews[i][j].getTriangleCenter());
                if (minDist > dist) {
                    minDist = dist;
                    selectedTile = tileViews[i][j];
                }
            }
        }
    }

    public void touchUp(int screenX, int screenY) {
        selectedTile = null;
        slideDirection = null;
    }

    public void setSlide(SlideDirection direction, float dst) {
        this.slideDirection = direction;
        this.slideDistance = dst;
    }
}
