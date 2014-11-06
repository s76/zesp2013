package rok3.projekt.zespver3.client.screens;

import static rok3.projekt.zespver3.client.Settings._window_height;
import static rok3.projekt.zespver3.client.Settings._window_width;
import rok3.projekt.zespver3.client.MyGame;
import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.client.widgets.customized.EscPanel;
import rok3.projekt.zespver3.client.widgets.models.FastMessage;
import rok3.projekt.zespver3.network.packet.__EndGame;
import rok3.projekt.zespver3.network.packet.__GameInitializer;
import rok3.projekt.zespver3.network.packet.__PlayerLeft;
import rok3.projekt.zespver3.network.packet.__RoomInfo;
import rok3.projekt.zespver3.network.packet.__UserInfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

/**
 * @author sheepw76
 * 
 */
public class GameScreen implements Screen {
	private MyGame game;
	private GameStage stage;
	
	
	private __GameInitializer gameInit;

	private EscPanel esc;
	private __RoomInfo[] ri;
	private __UserInfo userinfo;
	private __PlayerLeft player_left;
	public FastMessage fastMessage;
	
	public GameScreen(MyGame game,__GameInitializer gi) {
		this.game = game;
		this.gameInit = gi;
	}
	

	@Override
	public void render(float delta) {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
		
		if ( player_left != null ) {
			fastMessage.trigger(new String(player_left.userid)+ " has left the game");
			player_left = null;
		}
		if ( !Resources._nclient.isConnected() ) {
			fastMessage.trigger("Disconnected from server",2);
		}
		if( ri != null && userinfo != null ) {
			game.setScreen(new LobalScreen(game, userinfo, ri));
		}
	}

	@Override
	public void resize(int width, int height) {

	}
	
	@Override
	public void show() {

		esc = new EscPanel(150, 200);
		esc.setPosition(Settings._window_width/2-150/2, Settings._window_height/2-200/2);
		esc.setVisible(false);
		
		stage = new GameStage(gameInit,_window_width, _window_height){
			@Override
			public boolean keyTyped(char character) {
				if( !stage.isGameEnd() && (int) character == 27 ) {
					esc.setVisible(!esc.isVisible());
					if(esc.isVisible()) stage.pause();
					else stage.resume();
				}
				return super.keyTyped(character);
			}
		};
		
		fastMessage = new FastMessage(300,40);
		fastMessage.setPosition(Settings._window_width-250, -40);
		
		stage.addActor(esc);
		stage.addActor(fastMessage);
		
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {
	//	Gdx.app.exit();
	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		Gdx.app.exit();
	}

	public GameStage getStage() {
		return stage;
	}


	public GameStage getInGameStage() {
		return stage;
	}


	public void setRoomListInfo(__RoomInfo[] ri) {
		this.ri = ri;
	}


	public void setUserInfo(__UserInfo info) {
		this.userinfo = info;
	}


	public void playerLeft(__PlayerLeft object) {
		this.player_left= object;
	}


	public void gameEnd(__EndGame object) {
		stage.endGame(object);
	}


}
