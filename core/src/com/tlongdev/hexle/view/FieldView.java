package com.tlongdev.hexle.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.tlongdev.hexle.animation.TileViewAccessor;
import com.tlongdev.hexle.model.SlideDirection;
import com.tlongdev.hexle.shape.Rectangle;
import com.tlongdev.hexle.shape.Triangle;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;

import static com.tlongdev.hexle.model.impl.GameModelImpl.TILE_COLUMNS;
import static com.tlongdev.hexle.model.impl.GameModelImpl.TILE_ROWS;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class FieldView implements BaseView {

    private static final String TAG = FieldView.class.getName();

    private Logger logger;

    private OnAnimationListener animationListener;
    private OnSlideEndListener slideEndListener;

    private int screenWidth;
    private int screenHeight;

    private TileView[][] tileViews;
    private TileView[] fillerTileViews;

    //Booleans for animation, input disabling etc.
    private boolean touchDown = false;
    private boolean animating = false;

    private TileView selectedTile;
    private SlideDirection slideDirection;
    private float slideDistance;
    private int rowIndex;

    //Calculated when screen size is set
    private float tileWidth;
    private float tileHeight;
    private float offsetY;

    //Calculated on drag start
    private Vector2 slideVector;
    private float rowWidth = 0;

    //Animation stuff
    private TweenManager manager;
    private TweenCallback tweenCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            animating = false;
            selectedTile = null;
            slideDirection = null;

            //Notify the listener that the animation stopped
            if (animationListener != null) {
                animationListener.onAnimationFinished();
            }
        }
    };

    public FieldView(TweenManager manager) {
        this.manager = manager;
        slideVector = new Vector2();
        logger = new Logger(TAG, Logger.DEBUG);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        //Iterate through the tiles
        for (int i = 0; i < TILE_ROWS; i++) {
            for (int j = 0; j < TILE_COLUMNS; j++) {
                TileView view = tileViews[i][j];

                //If slideDirection is not null the a slide is currently happening
                if (touchDown && view.isAffectedBySlide(selectedTile, slideDirection) ||
                        animating && view.getTile().getRowIndex(slideDirection) == rowIndex) {

                    view.setCenter(view.getOriginCenter().cpy().add(slideVector));
                    //Render duplicates
                    renderDuplicates(view, shapeRenderer);
                } else {
                    view.setCenter(view.getOriginCenter());
                }

                view.render(shapeRenderer);
            }
        }

        //Render fillers
        renderFillers(shapeRenderer);
        renderBorders(shapeRenderer);
    }

    /**
     * Draw fillers to create a seemingly infinite row.
     */
    private void renderFillers(ShapeRenderer shapeRenderer) {
        if (slideDirection == null) {
            return;
        }

        //Draw the filler tiles if needed
        int fillerIndex;

        float leftFillerPosX;
        float leftFillerPosY;
        float rightFillerPosX;
        float rightFillerPosY;

        switch (slideDirection) {
            case EAST:
                //The index of the filler in the array
                fillerIndex = rowIndex;

                //Calculate the X coordinates of the fillers
                leftFillerPosX = 0 + slideVector.x;
                rightFillerPosX = screenWidth + slideVector.x;

                //The Y coordinates since they are in the same row
                rightFillerPosY = leftFillerPosY = offsetY + fillerIndex * tileHeight;
                break;

            case NORTH_EAST:

                if (rowIndex < 4) {
                    fillerIndex = 6 - 2 * rowIndex;

                    leftFillerPosX = 0 + slideVector.x;
                    rightFillerPosX = tileWidth * (rowIndex + 1) + slideVector.x;

                    leftFillerPosY = offsetY + fillerIndex * tileHeight + slideVector.y;
                    rightFillerPosY = offsetY + TILE_ROWS * tileHeight + slideVector.y;
                } else {
                    fillerIndex = 15 - 2 * rowIndex;

                    leftFillerPosX = tileWidth * (rowIndex - 3) + slideVector.x;
                    rightFillerPosX = screenWidth + slideVector.x;

                    rightFillerPosY = offsetY + fillerIndex * tileHeight + slideVector.y;
                    leftFillerPosY = offsetY + -1 * tileHeight + slideVector.y;
                }
                break;

            default:
                if (rowIndex < 4) {
                    fillerIndex = rowIndex * 2 + 1;

                    leftFillerPosX = 0 + slideVector.x;
                    rightFillerPosX = tileWidth * (rowIndex + 1) + slideVector.x;

                    leftFillerPosY = offsetY + fillerIndex * tileHeight + slideVector.y;
                    rightFillerPosY = offsetY + -1 * tileHeight + slideVector.y;
                } else {
                    fillerIndex = rowIndex * 2 - 8;

                    leftFillerPosX = tileWidth * (rowIndex - 3) + slideVector.x;
                    rightFillerPosX = screenWidth + slideVector.x;

                    rightFillerPosY = offsetY + fillerIndex * tileHeight + slideVector.y;
                    leftFillerPosY = offsetY + TILE_ROWS * tileHeight + slideVector.y;
                }
                break;
        }

        TileView filler = fillerTileViews[fillerIndex];
        filler.setSide(tileWidth * 0.9f);

        //Draw the left filler
        filler.setCenter(leftFillerPosX, leftFillerPosY);
        filler.setFullWidth(tileWidth);
        filler.render(shapeRenderer);

        //Draw the fight filler
        filler.setCenter(rightFillerPosX, rightFillerPosY);
        filler.setFullWidth(tileWidth);
        filler.render(shapeRenderer);
    }

    /**
     * This will render duplicates of triangles which are currently sliding creating an illusion of
     * a looped shift register.
     *
     * @param original      the original tile view
     * @param shapeRenderer shape renderer
     */
    private void renderDuplicates(TileView original, ShapeRenderer shapeRenderer) {
        Vector2 dupeVector = new Vector2(slideVector);
        Vector2 originalVector = original.getCenter().cpy();

        dupeVector.setLength(rowWidth);
        original.setCenter(originalVector.cpy().add(dupeVector));
        original.render(shapeRenderer);

        dupeVector.rotateRad(MathUtils.PI);
        original.setCenter(originalVector.cpy().add(dupeVector));
        original.render(shapeRenderer);

        original.setCenter(originalVector);
    }

    /**
     * Render the borders that will hide the duplicate tiles.
     */
    private void renderBorders(ShapeRenderer shapeRenderer) {
        float rectangleHeight = (screenHeight - 8 * tileHeight) / 2;

        //The rectangle for the top and bottom of the screen
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(screenWidth);
        rectangle.setHeight(rectangleHeight);
        rectangle.setColor(Color.BLACK);
        rectangle.setX(0);
        rectangle.setY(0);
        rectangle.render(shapeRenderer);

        rectangle.setY(screenHeight - rectangleHeight);
        rectangle.render(shapeRenderer);

        //The triangles on the sides of the screen
        Triangle triangle = new Triangle();
        triangle.setColor(Color.BLACK);
        for (int i = 0; i < 5; i++) {
            triangle.setA(tileWidth / 2.0f, rectangleHeight + 2.0f * i * tileHeight);
            triangle.setB(0, rectangleHeight + 2.0f * i * tileHeight + tileHeight);
            triangle.setC(0, rectangleHeight + 2.0f * i * tileHeight - tileHeight);
            triangle.render(shapeRenderer);

            triangle.setA(screenWidth - tileWidth / 2.0f, rectangleHeight + 2.0f * i * tileHeight);
            triangle.setB(screenWidth, rectangleHeight + 2.0f * i * tileHeight + tileHeight);
            triangle.setC(screenWidth, rectangleHeight + 2.0f * i * tileHeight - tileHeight);
            triangle.render(shapeRenderer);
        }
    }

    public void setDimensions(int width, int height) {
        logger.info("screensize changed: " + width + "x" + height);
        this.screenWidth = width;
        this.screenHeight = height;

        //Get the maximum width the tile can fit in the screen
        tileWidth = (float) (screenWidth / Math.ceil(TILE_COLUMNS / 2.0));

        //Calculate the height from the width (equilateral triangle height from side)
        tileHeight = tileWidth * (float) Math.sqrt(3) / 2.0f;

        //Calculate the vertical offset, so the triangles are in the middle of the screen
        offsetY = (screenHeight - (TILE_ROWS - 1) * tileHeight) / 2.0f;

        //Iterate through the tiles
        for (int i = 0; i < TILE_ROWS; i++) {
            for (int j = 0; j < TILE_COLUMNS; j++) {
                TileView view = tileViews[i][j];

                //Set the center
                view.setOriginCenter(
                        (j + 1) * tileWidth / 2.0f,
                        offsetY + i * tileHeight
                );
                view.setCenter(view.getOriginCenter());
                view.setFullWidth(tileWidth);
                view.setSide(tileWidth * 0.9f);
            }
        }
    }

    public void setTileViews(TileView[][] tileViews, TileView[] fillers) {
        selectedTile = null;

        this.fillerTileViews = fillers;
        this.tileViews = tileViews;

        //Calculate the vertical offset, so the triangles are in the middle of the screen
        offsetY = (screenHeight - (TILE_ROWS - 1) * tileHeight) / 2.0f;

        //Iterate through the tiles
        for (int i = 0; i < TILE_ROWS; i++) {
            for (int j = 0; j < TILE_COLUMNS; j++) {
                TileView view = tileViews[i][j];

                //Set the center
                view.setOriginCenter(
                        (j + 1) * tileWidth / 2.0f,
                        offsetY + i * tileHeight
                );
                view.setFullWidth(tileWidth);
                view.setSide(tileWidth * 0.9f);
                view.setCenter(view.getOriginCenter());

                if (view.getTile().getTemporaryOffset() != null) {
                    if (!animating) {
                        animating = true;
                        slideVector.set(view.getTile().getTemporaryOffset());
                        Tween.to(slideVector, TileViewAccessor.POS_XY, 500)
                                .target(0, 0)
                                .ease(Quad.INOUT)
                                .setCallback(tweenCallback)
                                .start(manager);
                    }
                    view.getTile().setTemporaryOffset(null);
                }
            }
        }

        //Notify the listener if animation has started
        if (animating && animationListener != null) {
            animationListener.onAnimationStarted();
        }
    }

    public void touchDown(int x, int y) {
        touchDown = true;

        Vector2 touchDown = new Vector2(x, y);
        float minDist = screenHeight;

        //Find the closest tile and mark it as selected
        for (int i = 0; i < TILE_ROWS; i++) {
            for (int j = 0; j < TILE_COLUMNS; j++) {
                float dist = touchDown.dst(tileViews[i][j].getTriangleCenter());
                if (minDist > dist) {
                    minDist = dist;
                    selectedTile = tileViews[i][j];
                }
            }
        }
    }

    public void touchUp() {
        touchDown = false;
        //If slide direction is null, slide has not started
        if (slideDirection != null && slideEndListener != null) {
            slideEndListener.onSlideEnd();
        }
    }

    public void setDrag(SlideDirection direction, float dst) {

        this.slideDirection = direction;
        this.slideDistance = dst;

        //The vector that will translate all the affected tiles
        slideVector.set(dst, 0);

        //Calculate the direction if the slide vector and the number of tiles in the sliding row
        switch (slideDirection) {
            case EAST:
                slideVector.setAngleRad(0);
                rowWidth = tileWidth * 5.0f;
                rowIndex = selectedTile.getTile().getPosY();
                break;
            case NORTH_EAST:
                rowIndex = selectedTile.getTile().getRightDiagonalIndex();
                slideVector.setAngleRad(MathUtils.PI / 3.0f);
                rowWidth = (1 + Math.min(rowIndex, 7 - rowIndex)) * 2.0f * tileWidth;
                break;
            default:
                rowIndex = selectedTile.getTile().getLeftDiagonalIndex();
                slideVector.setAngleRad(2.0f * MathUtils.PI / 3.0f);
                rowWidth = (1 + Math.min(rowIndex, 7 - rowIndex)) * 2.0f * tileWidth;
                break;
        }

        slideVector.setLength(Math.abs(dst) > Math.abs(rowWidth) ?
                rowWidth : dst);

        //Because setting the length of the vector will always make if face in the
        //positive direction no matter the distance being negative. Dumb.
        if (dst < 0) {
            slideVector.rotateRad(MathUtils.PI);
        }
    }

    public void noMatch() {
        //If the tile is out of it's place animate it back
        if (slideVector.len() > 0) {
            animating = true;
            Tween.to(slideVector, TileViewAccessor.POS_XY, 500)
                    .target(0, 0)
                    .ease(Quad.INOUT)
                    .setCallback(tweenCallback)
                    .start(manager);
        }

        //Notify the listener if animation has started
        if (animating && animationListener != null) {
            animationListener.onAnimationStarted();
        }
    }

    public void setAnimationListener(OnAnimationListener animationListener) {
        this.animationListener = animationListener;
    }

    public void setSlideEndListener(OnSlideEndListener slideEndListener) {
        this.slideEndListener = slideEndListener;
    }

    public SlideDirection getSlideDirection() {
        return slideDirection;
    }

    public float getSlideDistance() {
        return slideDistance;
    }

    public TileView getSelectedTile() {
        return selectedTile;
    }

    public float getTileWidth() {
        return tileWidth;
    }

    public interface OnAnimationListener {
        void onAnimationStarted();

        void onAnimationFinished();
    }

    public interface OnSlideEndListener {
        void onSlideEnd();
    }
}
