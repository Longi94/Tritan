package com.tlongdev.hexle.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.tlongdev.hexle.Consts;
import com.tlongdev.hexle.animation.Vector2Accessor;
import com.tlongdev.hexle.model.Field;
import com.tlongdev.hexle.model.enumration.SlideDirection;
import com.tlongdev.hexle.shape.Rectangle;
import com.tlongdev.hexle.shape.Triangle;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;

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
    private int animatingTiles = 0;

    public FieldView(TweenManager manager) {
        this.manager = manager;
        init();
    }

    private void init() {
        slideVector = new Vector2();
        logger = new Logger(TAG, Logger.DEBUG);

        tileViews = new TileView[Consts.FIELD_ROWS][Consts.FIELD_COLUMNS];
        fillerTileViews = new TileView[Consts.FIELD_COLUMNS];

        for (int i = 0; i < Consts.FIELD_ROWS; i++) {
            for (int j = 0; j < Consts.FIELD_COLUMNS; j++) {
                tileViews[i][j] = new TileView();
            }
            fillerTileViews[i] = new TileView();
        }
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        //Iterate through the tiles
        for (int i = 0; i < Consts.FIELD_ROWS; i++) {
            for (int j = 0; j < Consts.FIELD_COLUMNS; j++) {
                TileView view = tileViews[i][j];

                //If slideDirection is not null the a slide is currently happening
                if (slideDirection != null && view.getTile().getRowIndex(slideDirection) == rowIndex
                        && (touchDown || animating)) {
                    view.setCenter(view.getOriginCenter().cpy().add(slideVector));
                    //Render duplicates
                    renderDuplicates(view, shapeRenderer);
                } else if (!animating) {
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

        //The index of the filler in the array
        int fillerIndex = Field.getFillerIndex(slideDirection, rowIndex);

        float leftFillerPosX;
        float leftFillerPosY;
        float rightFillerPosX;
        float rightFillerPosY;

        switch (slideDirection) {
            case SIDEWAYS:
                //Calculate the X coordinates of the fillers
                leftFillerPosX = 0 + slideVector.x;
                rightFillerPosX = screenWidth + slideVector.x;

                //The Y coordinates since they are in the same row
                rightFillerPosY = leftFillerPosY = offsetY + fillerIndex * tileHeight;
                break;
            case ANTI_DIAGONAL:
                if (rowIndex < 4) {
                    leftFillerPosX = 0 + slideVector.x;
                    rightFillerPosX = tileWidth * (rowIndex + 1) + slideVector.x;

                    leftFillerPosY = offsetY + fillerIndex * tileHeight + slideVector.y;
                    rightFillerPosY = offsetY + Consts.FIELD_ROWS * tileHeight + slideVector.y;
                } else {
                    leftFillerPosX = tileWidth * (rowIndex - 3) + slideVector.x;
                    rightFillerPosX = screenWidth + slideVector.x;

                    rightFillerPosY = offsetY + fillerIndex * tileHeight + slideVector.y;
                    leftFillerPosY = offsetY + -1 * tileHeight + slideVector.y;
                }
                break;
            default:
                if (rowIndex < 4) {
                    leftFillerPosX = 0 + slideVector.x;
                    rightFillerPosX = tileWidth * (rowIndex + 1) + slideVector.x;

                    leftFillerPosY = offsetY + fillerIndex * tileHeight + slideVector.y;
                    rightFillerPosY = offsetY + -1 * tileHeight + slideVector.y;
                } else {
                    leftFillerPosX = tileWidth * (rowIndex - 3) + slideVector.x;
                    rightFillerPosX = screenWidth + slideVector.x;

                    rightFillerPosY = offsetY + fillerIndex * tileHeight + slideVector.y;
                    leftFillerPosY = offsetY + Consts.FIELD_ROWS * tileHeight + slideVector.y;
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

        //One duplicate
        dupeVector.setLength(rowWidth);
        original.setCenter(originalVector.cpy().add(dupeVector));
        original.render(shapeRenderer);

        //The other duplicate
        dupeVector.rotateRad(MathUtils.PI);
        original.setCenter(originalVector.cpy().add(dupeVector));
        original.render(shapeRenderer);

        original.setCenter(originalVector);
    }

    /**
     * Render the borders that will hide the duplicate tiles.
     *
     * @param shapeRenderer shape renderer
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
            //Left triangle
            triangle.setA(tileWidth / 2.0f, rectangleHeight + 2.0f * i * tileHeight);
            triangle.setB(0, rectangleHeight + 2.0f * i * tileHeight + tileHeight);
            triangle.setC(0, rectangleHeight + 2.0f * i * tileHeight - tileHeight);
            triangle.render(shapeRenderer);

            //Right triangle
            triangle.setA(screenWidth - tileWidth / 2.0f, rectangleHeight + 2.0f * i * tileHeight);
            triangle.setB(screenWidth, rectangleHeight + 2.0f * i * tileHeight + tileHeight);
            triangle.setC(screenWidth, rectangleHeight + 2.0f * i * tileHeight - tileHeight);
            triangle.render(shapeRenderer);
        }
    }

    public void setDimensions(int width, int height) {
        logger.info("screen size changed: " + width + "x" + height);
        this.screenWidth = width;
        this.screenHeight = height;

        //Get the maximum width the tile can fit in the screen
        tileWidth = (float) (screenWidth / Math.ceil(Consts.FIELD_COLUMNS / 2.0));

        //Calculate the height from the width (equilateral triangle height from side)
        tileHeight = tileWidth * (float) Math.sqrt(3) / 2.0f;

        //Calculate the vertical offset, so the triangles are in the middle of the screen
        offsetY = (screenHeight - (Consts.FIELD_ROWS - 1) * tileHeight) / 2.0f;

        //Iterate through the tiles
        for (int i = 0; i < Consts.FIELD_ROWS; i++) {
            for (int j = 0; j < Consts.FIELD_COLUMNS; j++) {
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

    public void touchDown(int x, int y) {
        touchDown = true;

        Vector2 touchDown = new Vector2(x, y);
        float minDist = screenHeight;

        //Find the closest tile and mark it as selected
        for (int i = 0; i < Consts.FIELD_ROWS; i++) {
            for (int j = 0; j < Consts.FIELD_COLUMNS; j++) {
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
            slideEndListener.onUserInputFinish();
        }
    }

    public void setDrag(SlideDirection direction, float dst) {

        this.slideDirection = direction;

        //The vector that will translate all the affected tiles
        slideVector.set(dst, 0);

        //Calculate the direction if the slide vector and the number of tiles in the sliding row
        switch (slideDirection) {
            case SIDEWAYS:
                slideVector.setAngleRad(0);
                rowWidth = tileWidth * 5.0f;
                rowIndex = selectedTile.getTile().getPosY();
                break;
            case ANTI_DIAGONAL:
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

        this.slideDistance = Math.abs(dst) > Math.abs(rowWidth) ? rowWidth : dst;

        slideVector.setLength(slideDistance);

        //Because setting the length of the vector will always make if face in the
        //positive direction no matter the distance being negative. Dumb.
        if (dst < 0) {
            slideVector.rotateRad(MathUtils.PI);
        }
    }

    public void animateNoMatchSlide() {
        //If the tile is out of it's place animate it back
        if (slideVector.len() > 0) {
            animating = true;
            Tween.to(slideVector, Vector2Accessor.POS_XY, Consts.SLIDE_DURATION)
                    .target(0, 0)
                    .ease(Cubic.OUT)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            //Animation finished
                            animating = false;
                            selectedTile = null;
                            slideDirection = null;

                            //Notify the listener that the animation stopped
                            if (animationListener != null) {
                                animationListener.onNoMatchAnimationFinished();
                            }
                        }
                    })
                    .start(manager);
        }

        //Notify the listener if animation has started
        if (animating && animationListener != null) {
            animationListener.onAnimationStarted();
        }
    }

    public void animateFinishShift() {
        //If the tile is out of it's place animate it back
        if (slideVector.len() > 0) {
            animating = true;
            Tween.to(slideVector, Vector2Accessor.POS_XY, Consts.SLIDE_DURATION)
                    .target(0, 0)
                    .ease(Cubic.OUT)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            //Animation finished
                            animating = false;
                            selectedTile = null;
                            slideDirection = null;

                            //Notify the listener that the animation stopped
                            if (animationListener != null) {
                                animationListener.onFinishShiftAnimationFinished();
                            }
                        }
                    })
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

    public void setSlideVector(Vector2 slideVector) {
        this.slideVector.set(slideVector);
    }

    public TileView[][] getTileViews() {
        return tileViews;
    }

    public TileView[] getFillerTileViews() {
        return fillerTileViews;
    }

    public void animateNewTileGeneration() {
        for (int i = 0; i < Consts.FIELD_ROWS; i++) {
            for (int j = 0; j < Consts.FIELD_COLUMNS; j++) {
            }
        }

        //Notify the listener if animation has started
        if (animating && animationListener != null) {
            animationListener.onAnimationStarted();
        }
    }

    public void setAnimating(boolean animating) {
        this.animating = animating;
    }

    public interface OnAnimationListener {
        /**
         * Called when an animation starts
         */
        void onAnimationStarted();

        /**
         * Called when the no match (sliding the tiles back to their original place) animation
         * finishes.
         */
        void onNoMatchAnimationFinished();

        /**
         * Called when the shift (animation tiles to their new place) finishes
         */
        void onFinishShiftAnimationFinished();

        void onFinishNewTileGenerationAnimation();
    }

    public interface OnSlideEndListener {
        void onUserInputFinish();
    }
}
