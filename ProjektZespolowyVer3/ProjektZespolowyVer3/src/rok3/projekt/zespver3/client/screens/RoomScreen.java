package rok3.projekt.zespver3.client.screens;

import rok3.projekt.zespver3.client.MyGame;
import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.client.widgets.customized.RoomChatPanel;
import rok3.projekt.zespver3.client.widgets.customized.RoomControlPanel;
import rok3.projekt.zespver3.client.widgets.customized.RoomDisplayPanel;
import rok3.projekt.zespver3.network.packet.__GameInitializer;
import rok3.projekt.zespver3.network.packet.__RoomChatBuffor;
import rok3.projekt.zespver3.network.packet.__RoomInfo;
import rok3.projekt.zespver3.network.packet.__RoomInitializer;
import rok3.projekt.zespver3.network.packet.__RoomStatus;
import rok3.projekt.zespver3.network.packet.__UserInfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class RoomScreen implements Screen{

	private MyGame game;
	private Stage stage;
	private boolean status_changed;
	private RoomDisplayPanel rdp;
	private RoomControlPanel rcp;
	private RoomChatPanel chatp;
	
	private __UserInfo userinfo;
	private __RoomStatus roomStatus;
	private __RoomInitializer roomInit;
	private __RoomInfo[] roominfo;
	private __GameInitializer gameInit;
	
	public RoomScreen(MyGame game, __RoomInitializer roomInit,
			__RoomStatus roomStatus, __UserInfo userinfo) {
		this.game = game;
		this.roomStatus = roomStatus;
		status_changed = true;
		this.roomInit = roomInit;
		this.userinfo=userinfo;
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if ( status_changed ) {
			rdp.applyStatus(roomStatus);
			status_changed =false;
		}
		
		stage.act(delta);
		
		stage.draw();
		
		if ( roominfo != null  ) {
			game.setScreen(new LobalScreen(game,userinfo,roominfo));
		}
		
		if ( gameInit != null ) {
			game.setScreen(new GameScreen(game, gameInit));
		}
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		chatp= new RoomChatPanel(600,220);
		chatp.setPosition(30, 20);
		
		rcp = new RoomControlPanel(roomInit.room_master_id == Resources._nclient.my_id? true:false);
		rcp.setPosition(915, 120);
		
		rdp = new RoomDisplayPanel(new String(roomInit.room_master_userid),roomInit.max_pl,roomInit.max_score);
		rdp.setPosition(30, 265);
		
		stage = new Stage(Settings._window_width, Settings._window_height,false);
		stage.addActor(chatp);
		stage.addActor(rcp);
		stage.addActor(rdp);
		
		
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

	
	public void setRoomStatus(__RoomStatus status) {
		System.out.println("RoomScreen: room status = null? " + (status==null));
		this.roomStatus = status;
		this.status_changed=true;
	}

	public void setRoomListInfo(__RoomInfo[] ri) {
		this.roominfo = ri;
	}

	public void updateRoomChat(__RoomChatBuffor chat) {
		chatp.updateContent(chat);
	}

	public void setGameInitializer(__GameInitializer init) {
		System.out.println("roomscreen# set game initializer1");
		this.gameInit= init;
		System.out.println("roomscreen# set game initializer2");
	}
}
