package in.dragonbra;

import com.badlogic.gdx.Game;

import in.dragonbra.screen.TritanGameScreen;

public class TritanApplication extends Game {
	private static final String TAG = TritanApplication.class.getName();

	@Override
	public void create() {
		setScreen(new TritanGameScreen());
	}
}
