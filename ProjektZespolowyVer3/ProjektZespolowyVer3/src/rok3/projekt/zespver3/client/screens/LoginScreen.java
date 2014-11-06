package rok3.projekt.zespver3.client.screens;

import static rok3.projekt.zespver3.client.Settings._window_height;
import static rok3.projekt.zespver3.client.Settings._window_width;
import rok3.projekt.zespver3.client.MyGame;
import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.client.widgets.customized.LoginPanel;
import rok3.projekt.zespver3.client.widgets.customized.RegistrationPanel;
import rok3.projekt.zespver3.client.widgets.customized.SettingsPanel;
import rok3.projekt.zespver3.network.packet.__RoomInfo;
import rok3.projekt.zespver3.network.packet.__UserInfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class LoginScreen implements Screen {
	private Stage stage;
	private MyGame game;
	private __UserInfo userinfo;
	private __RoomInfo[] roominfo;
	
	public LoginScreen(MyGame game) {
		this.game = game;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		
		stage.draw();
		
		Table.drawDebug(stage);
		
		if ( userinfo != null && roominfo != null  ) {
			game.setScreen(new LobalScreen(game,userinfo,roominfo));
		}
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		
		LoginPanel lp = new LoginPanel();
		lp.setPosition(200, 200);
		
		RegistrationPanel rp = new RegistrationPanel();
		rp.setPosition(200, 350);
		
		SettingsPanel sspanel = new SettingsPanel(Settings._window_width,Settings._window_height);
		sspanel.setPosition(0, Settings._window_height-50);
		
		stage = new Stage(Settings._window_width, Settings._window_height,false);	
		Image bg = new Image(new Texture(Gdx.files.internal("test/randomimage.jpg")));
		bg.setSize(_window_width, _window_height);
		stage.addActor(bg);
		stage.addActor(lp);
		stage.addActor(rp);
		stage.addActor(sspanel);
		stage.addActor(Resources._messbar);
		
		
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		
	}
	
	public void setUserInfo (__UserInfo info) {
		this.userinfo = info;
	}
	
	public void setRoomListInfo(__RoomInfo[] roominfo) {
		this.roominfo = roominfo;
	}
}
