package com.tlongdev.hexle.view;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tlongdev.hexle.controller.GameController;
import com.tlongdev.hexle.model.SlideDirection;
import com.tlongdev.hexle.model.Tile;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class FieldView implements BaseView {

    private ShapeRenderer shapeRenderer;

    private int screenWidth;
    private int screenHeight;

    private TileView[][] tileViews;
    private TileView[] fillerTileViews;

    private TileView selectedTile;
    private SlideDirection slideDirection;
    private float slideDistance;

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;

        //Get the maximum width the tile can fit in the screen
        float tileWidth = (float) (screenWidth / Math.ceil(GameController.TILE_COLUMNS / 2.0));

        //Calculate the height from the width (equilateral triangle height from side)
        float tileHeight = tileWidth * (float) Math.sqrt(3) / 2.0f;

        //Calculate the vertical offset, so the triangles are in the middle of the screen
        float offsetY = (screenHeight - (GameController.TILE_ROWS - 1) * tileHeight) / 2.0f;

        //The vector that will translate all the affected tiles
        Vector2 slideVector = new Vector2(slideDistance, 0);

        float rowWidth = 0;

        if (slideDirection != null && selectedTile != null) {
            switch (slideDirection) {
                case EAST:
                    slideVector.setAngleRad(0);
                    rowWidth = tileWidth * 5.0f;
                    break;
                case NORTH_EAST:
                    int rightIndex = selectedTile.getTile().getRightDiagonalIndex();
                    slideVector.setAngleRad(MathUtils.PI / 3.0f);
                    rowWidth = (1 + Math.min(rightIndex, 7 - rightIndex)) * 2.0f * tileWidth;
                    break;
                default:
                    int leftIndex = selectedTile.getTile().getLeftDiagonalIndex();
                    slideVector.setAngleRad(2.0f * MathUtils.PI / 3.0f);
                    rowWidth = (1 + Math.min(leftIndex, 7 - leftIndex)) * 2.0f * tileWidth;
                    break;
            }

            slideVector.setLength(Math.abs(slideDistance) > Math.abs(rowWidth) ?
                    rowWidth : slideDistance);

            //Because setting the length of the vector will always make if face in the
            //positive direction no matter the distance being negative. Dumb.
            if (slideDistance < 0) {
                slideVector.rotateRad(MathUtils.PI);
            }
        }

        //Iterate through the tiles
        for (int i = 0; i < GameController.TILE_ROWS; i++) {
            for (int j = 0; j < GameController.TILE_COLUMNS; j++) {
                TileView view = tileViews[i][j];

                //Set the center
                view.setCenter(new Vector2(
                        (j + 1) * tileWidth / 2.0f,
                        offsetY + i * tileHeight
                ));

                view.setSide(tileWidth * 0.9f);

                //If slideDirection is not null the a slide is currently happening
                if (slideDirection != null && view.isAffectedBySlide(selectedTile, slideDirection)) {
                    view.getCenter().add(slideVector);

                    //Render duplicates
                    renderDuplicates(view, slideDirection, rowWidth);
                }

                view.render(shapeRenderer);
            }
        }

        //Render fillers
        renderFillers(tileWidth, tileHeight);
    }

    /**
     * Draw fillers to create a seemingly infinite row.
     *
     * @param tileWidth
     * @param tileHeight
     */
    private void renderFillers(float tileWidth, float tileHeight) {
        // TODO: 2016.04.12. This function could really use some magic calculation rather than loops
        if (slideDirection == null || selectedTile == null) {
            return;
        }

        //Draw the filler tiles if needed
        int fillerIndex;
        Vector2 leftFillerPos = new Vector2();
        Vector2 rightFillerPos = new Vector2();
        Tile tile = selectedTile.getTile();
        int leftStepsX;
        int leftStepsY;
        int tilePosX;
        int tilePosY;
        int rightStepsX;
        int rightStepsY;

        switch (slideDirection) {
            case EAST:
                //The index of the filler in the array
                fillerIndex = tile.getHorizontalRowIndex();

                //Calculate the X coordinates of the fillers
                float leftFillerX = selectedTile.getCenter().x -
                        (tile.getPosX() + 1) * tileWidth / 2.0f;
                float rightFillerX = selectedTile.getCenter().x +
                        (GameController.TILE_COLUMNS - tile.getPosX()) * tileWidth / 2.0f;

                //The Y coordinates since they are in the same row
                float leftFillerY = selectedTile.getCenter().y;
                float rightFillerY = selectedTile.getCenter().y;

                //The vector positions
                leftFillerPos.set(leftFillerX, leftFillerY);
                rightFillerPos.set(rightFillerX, rightFillerY);
                break;

            case NORTH_EAST:
                int rightDiagonalIndex = tile.getRightDiagonalIndex();

                //Calculate the number of horizontal and vertical steps needed to reach the
                //Position if the lower left filler
                leftStepsX = 0;
                leftStepsY = 0;
                tilePosX = tile.getPosX();
                tilePosY = tile.getPosY();

                do {
                    if ((tilePosX + tilePosY) % 2 == 1) {
                        tilePosY--;
                        leftStepsY++;
                    } else {
                        tilePosX--;
                        leftStepsX++;
                    }
                } while (tilePosX >= 0 && tilePosY >= 0);

                //Calculate the number of horizontal and vertical steps needed to reach the
                //Position if the upper right filler
                rightStepsX = 0;
                rightStepsY = 0;
                tilePosX = tile.getPosX();
                tilePosY = tile.getPosY();

                do {
                    if ((tilePosX + tilePosY) % 2 == 1) {
                        tilePosX++;
                        rightStepsX++;
                    } else {
                        tilePosY++;
                        rightStepsY++;
                    }
                } while (tilePosX < GameController.TILE_COLUMNS &&
                        tilePosY < GameController.TILE_ROWS);

                //Offset the center relative to the selected tile
                rightFillerPos.set(
                        selectedTile.getCenter().x + rightStepsX * tileWidth / 2.0f,
                        selectedTile.getCenter().y + rightStepsY * tileHeight
                );

                leftFillerPos.set(
                        selectedTile.getCenter().x - leftStepsX * tileWidth / 2.0f,
                        selectedTile.getCenter().y - leftStepsY * tileHeight
                );

                if (rightDiagonalIndex <= 3) {
                    fillerIndex = 6 - 2 * rightDiagonalIndex;
                } else {
                    fillerIndex = 15 - 2 * rightDiagonalIndex;
                }
                break;

            default:
                int leftDiagonalIndex = tile.getLeftDiagonalIndex();

                //Calculate the number of horizontal and vertical steps needed to reach the
                //Position if the upper left filler
                leftStepsX = 0;
                leftStepsY = 0;
                tilePosX = tile.getPosX();
                tilePosY = tile.getPosY();

                do {
                    if ((tilePosX + tilePosY) % 2 == 1) {
                        tilePosX--;
                        leftStepsX++;
                    } else {
                        tilePosY++;
                        leftStepsY++;
                    }
                } while (tilePosX >= 0 && tilePosY < GameController.TILE_ROWS);

                //Calculate the number of horizontal and vertical steps needed to reach the
                //Position if the lower right filler
                rightStepsX = 0;
                rightStepsY = 0;
                tilePosX = tile.getPosX();
                tilePosY = tile.getPosY();

                do {
                    if ((tilePosX + tilePosY) % 2 == 1) {
                        tilePosY--;
                        rightStepsY++;
                    } else {
                        tilePosX++;
                        rightStepsX++;
                    }
                } while (tilePosX < GameController.TILE_COLUMNS && tilePosY >= 0);

                //Offset the center relative to the selected tile
                rightFillerPos.set(
                        selectedTile.getCenter().x + rightStepsX * tileWidth / 2.0f,
                        selectedTile.getCenter().y - rightStepsY * tileHeight
                );

                leftFillerPos.set(
                        selectedTile.getCenter().x - leftStepsX * tileWidth / 2.0f,
                        selectedTile.getCenter().y + leftStepsY * tileHeight
                );

                if (leftDiagonalIndex <= 3) {
                    fillerIndex = leftDiagonalIndex * 2 + 1;
                } else {
                    fillerIndex = leftDiagonalIndex * 2 - 8;
                }

                break;
        }

        TileView filler = fillerTileViews[fillerIndex];
        filler.setSide(tileWidth * 0.9f);

        //Draw the left filler
        filler.setCenter(leftFillerPos);
        filler.render(shapeRenderer);

        //Draw the fight filler
        filler.setCenter(rightFillerPos);
        filler.render(shapeRenderer);
    }

    /**
     * This will render duplicates of triangles which are currently sliding creating an illusion of
     * a looped shift register.
     *
     * @param original  the original tile view
     * @param direction the direction the sliding is going on
     * @param rowWidth  the (full) size of the row
     */
    private void renderDuplicates(TileView original, SlideDirection direction, float rowWidth) {
        Vector2 slideVector = new Vector2(slideDistance, 0);
        Vector2 originalVector = original.getCenter();
        switch (direction) {
            case EAST:
                slideVector.setAngleRad(0);
                break;
            case NORTH_EAST:
                slideVector.setAngleRad(MathUtils.PI / 3.0f);
                break;
            default:
                slideVector.setAngleRad(2.0f * MathUtils.PI / 3.0f);
                break;
        }
        slideVector.setLength(rowWidth);
        original.setCenter(originalVector.cpy().add(slideVector));
        original.render(shapeRenderer);

        slideVector.rotateRad(MathUtils.PI);
        original.setCenter(originalVector.cpy().add(slideVector));
        original.render(shapeRenderer);
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

    public void setFillerTileViews(TileView[] fillerTileViews) {
        this.fillerTileViews = fillerTileViews;
    }
}
