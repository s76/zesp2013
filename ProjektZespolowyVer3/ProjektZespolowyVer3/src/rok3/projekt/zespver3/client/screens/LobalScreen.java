package rok3.projekt.zespver3.client.screens;

import static rok3.projekt.zespver3.client.Settings._window_height;
import static rok3.projekt.zespver3.client.Settings._window_width;
import rok3.projekt.zespver3.client.MyGame;
import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.widgets.customized.CreateGamePanel;
import rok3.projekt.zespver3.client.widgets.customized.JoinRoomPanel;
import rok3.projekt.zespver3.client.widgets.customized.UserInfoPanel;
import rok3.projekt.zespver3.network.packet.__RoomInfo;
import rok3.projekt.zespver3.network.packet.__RoomInitializer;
import rok3.projekt.zespver3.network.packet.__RoomStatus;
import rok3.projekt.zespver3.network.packet.__UserInfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class LobalScreen implements Screen {
	private MyGame game;
	private Stage stage;

	private __UserInfo userinfo;
	private JoinRoomPanel jrp;
	private __RoomInitializer roomInit;
	private __RoomStatus roomStatus;
	private __RoomInfo[] roominfo;

	public LobalScreen(MyGame myGame, __UserInfo userinfo,__RoomInfo[] roominfo) {
		this.game = myGame;
		this.userinfo = userinfo;
		this.roominfo = roominfo;
		System.out.println("TestRoom#constructor - OK");
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
		
		if (  roomInit != null && roomStatus != null ) {
			game.setScreen(new RoomScreen(game,roomInit,roomStatus,userinfo));
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		stage = new Stage(_window_width, _window_height, true);
//		Image bg = new Image(new Texture(Gdx.files.internal("test/bg2.jpg")));
//		bg.setSize(_window_width, _window_height);
//		stage.addActor(bg);
		CreateGamePanel cgp = new CreateGamePanel();
		cgp.setPosition(600, 300);
		
		jrp = new JoinRoomPanel();
		jrp.setPosition(50,280);
		jrp.setNewRoomList(roominfo);

		UserInfoPanel uip = new UserInfoPanel(userinfo);
		uip.setPosition(200, 100);
		
		stage.addActor(jrp);
		stage.addActor(uip);
		stage.addActor(cgp);
		
		
		Resources._messbar.setText("");
		stage.addActor(Resources._messbar);
	
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void setRoomListInfo(__RoomInfo[] ri) {
		jrp.setNewRoomList(ri);
	}

	public void setRoomInitializer(__RoomInitializer init) {
		this.roomInit = init;
	}

	public void setRoomStatus(__RoomStatus status) {
		this.roomStatus = status;
	}

}
