package rok3.projekt.zespver3.client;

import rok3.projekt.zespver3.client.screens.LoginScreen;

import com.badlogic.gdx.Game;

public class MyGame extends Game {

	@Override
	public void create() {
		Resources.initialize(this);
		setScreen(new LoginScreen(this));
//		setScreen(new  rok3.projekt.zespver3.client.screens.TestS());
		System.out.println("Game#create - OK ");
	}

}
