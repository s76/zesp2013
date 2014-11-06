package rok3.projekt.zespver3;


import rok3.projekt.zespver3.client.MyGame;
import rok3.projekt.zespver3.client.Settings;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = Settings.game_name;
		cfg.useGL20 = Settings.useGL20;
		cfg.resizable = Settings.resizable;
		cfg.width = Settings._window_width;
		cfg.height = Settings._window_height;
		cfg.x = 200;
		cfg.y = 50;
		new LwjglApplication(new MyGame(), cfg);
	}
}
