package com.tlongdev.hexle.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Logger;
import com.tlongdev.hexle.controller.impl.GameControllerImpl;
import com.tlongdev.hexle.input.HexleInputProcessor;
import com.tlongdev.hexle.model.impl.GameModelImpl;
import com.tlongdev.hexle.renderer.impl.GameRendererImpl;

/**
 * @author longi
 * @since 2016.04.10.
 */
public class HexleGameScreen implements Screen {

    public static final int TEXT_UPDATE_FREQUENCY = 500;

    private static final String TAG = HexleGameScreen.class.getSimpleName();

    private Logger logger;

    private GameControllerImpl controller;
    private GameRendererImpl renderer;

    private boolean paused;

    private BitmapFont arial12;
    private SpriteBatch batch;

    private long lastTextUpdate = 0;
    private float accelX;
    private float accelY;
    private float accelZ;
    private int orientation;
    private float rad;
    private HexleInputProcessor inputProcessor;
    private float inclination;

    public HexleGameScreen() {
        logger = new Logger(TAG, Logger.DEBUG);
    }

    @Override
    public void show() {

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 32;
        arial12 = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        batch = new SpriteBatch();

        //Set Libgdx log level to DEBUG
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        logger.info("show");

        inputProcessor = new HexleInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

        //Initialize controller and renderer
        controller = new GameControllerImpl();
        renderer = new GameRendererImpl();
        GameModelImpl model = new GameModelImpl();

        controller.setModel(model);
        controller.setRenderer(renderer);

        renderer.setModel(model);
        renderer.setController(controller);

        model.setRenderer(renderer);

        inputProcessor.setListener(renderer);

        controller.startGame();

        //Active on start
        paused = false;
    }

    @Override
    public void render(float delta) {

        // TODO: 2016.04.16. Remove accel data drawing
        if (lastTextUpdate < System.currentTimeMillis() - TEXT_UPDATE_FREQUENCY) {
            lastTextUpdate = System.currentTimeMillis();
            float[] values = inputProcessor.updateAccelerometer();
            rad = values[0];
            inclination = values[1];
            accelX = Gdx.input.getAccelerometerX();
            accelY = Gdx.input.getAccelerometerY();
            accelZ = Gdx.input.getAccelerometerZ();
            orientation = Gdx.input.getRotation();
        }

        //Do not update is paused
        if (!paused) {
            //Update game world by the time that has passed since last rendered frame.
            controller.update(Gdx.graphics.getDeltaTime() * 1000.0f);
        }

        //Sets the clear screen color to white
        Gdx.gl.glClearColor(0, 0, 0, 1);

        //Clears the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Render game world to screen
        renderer.render();

        batch.begin();
        arial12.draw(batch, "Accelerometer\nX: " + accelX + "\nY: " + accelY +
                "\nZ: " + accelZ, 0, Gdx.graphics.getHeight());
        arial12.draw(batch, "Radian\n" + rad , 250, Gdx.graphics.getHeight());
        arial12.draw(batch, "Inclination\n" + inclination , 500, Gdx.graphics.getHeight());
        arial12.draw(batch, "Orientation\n" + orientation, 750, Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        logger.info("resize");
        renderer.resize(width, height);
    }

    @Override
    public void pause() {
        logger.info("pause");
        paused = true;
    }

    @Override
    public void resume() {
        logger.info("resize");
        paused = false;
    }

    @Override
    public void hide() {
        logger.info("hide");
    }

    @Override
    public void dispose() {
        logger.info("dispose");
        renderer.dispose();
        arial12.dispose();
        batch.dispose();
    }
}
