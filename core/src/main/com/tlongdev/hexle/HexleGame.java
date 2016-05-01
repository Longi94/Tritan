package com.tlongdev.hexle;

import com.badlogic.gdx.Game;
import com.tlongdev.hexle.screen.HexleGameScreen;

public class HexleGame extends Game {

    private static final String TAG = HexleGame.class.getName();

    @Override
    public void create() {
        setScreen(new HexleGameScreen());
    }
}
