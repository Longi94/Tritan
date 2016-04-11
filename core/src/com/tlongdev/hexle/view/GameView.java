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
        float width = (float) (screenWidth / Math.ceil(GameController.TILE_COLUMNS / 2.0));
        float height = width * (float) Math.sqrt(3) / 2.0f;
        float offsetY = (screenHeight - (GameController.TILE_ROWS - 1) * height) / 2.0f;

        for (int i = 0; i < GameController.TILE_ROWS; i++) {
            for (int j = 0; j < GameController.TILE_COLUMNS; j++) {
                TileView view = tileViews[i][j];
                view.setCenter(new Vector2(
                        (j + 1) * width / 2.0f,
                        offsetY + i * height
                ));

                if (selectedTile == view) {
                    view.setSide(width);

                    if (slideDirection != null) {
                        Vector2 slideVector = new Vector2(slideDistance, 0);
                        switch (slideDirection){
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
                        if (slideDistance < 0) {
                            slideVector.rotateRad(MathUtils.PI);
                        }
                        view.getCenter().add(slideVector);
                    }

                } else {
                    view.setSide(width * 0.9f);
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
