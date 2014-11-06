package rok3.projekt.zespver3.utils;

import static rok3.projekt.zespver3.network.packet.__PrimitiveSignal__.ready__;
import static rok3.projekt.zespver3.network.packet.__PrimitiveSignal__.start__;

import java.io.IOException;

import rok3.projekt.zespver3.client.MyGame;
import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.client.screens.GameScreen;
import rok3.projekt.zespver3.client.screens.GameStage;
import rok3.projekt.zespver3.client.screens.LobalScreen;
import rok3.projekt.zespver3.client.screens.LoginScreen;
import rok3.projekt.zespver3.client.screens.RoomScreen;
import rok3.projekt.zespver3.network.Network;
import rok3.projekt.zespver3.network.packet.CharacterState__;
import rok3.projekt.zespver3.network.packet.CreateRoom__;
import rok3.projekt.zespver3.network.packet.JoinRoom__;
import rok3.projekt.zespver3.network.packet.KillRecord__;
import rok3.projekt.zespver3.network.packet.LoginInfo__;
import rok3.projekt.zespver3.network.packet.RegistrationInfo__;
import rok3.projekt.zespver3.network.packet.RoomChat__;
import rok3.projekt.zespver3.network.packet.ShootEvent__;
import rok3.projekt.zespver3.network.packet.__EndGame;
import rok3.projekt.zespver3.network.packet.__GameInitializer;
import rok3.projekt.zespver3.network.packet.__PlayerLeft;
import rok3.projekt.zespver3.network.packet.__PrimitiveSignal__;
import rok3.projekt.zespver3.network.packet.__RoomChatBuffor;
import rok3.projekt.zespver3.network.packet.__RoomInfo;
import rok3.projekt.zespver3.network.packet.__RoomInitializer;
import rok3.projekt.zespver3.network.packet.__RoomStatus;
import rok3.projekt.zespver3.network.packet.__Update;
import rok3.projekt.zespver3.network.packet.__UserInfo;
import rok3.projekt.zespver3.network.packet.__YourId;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class GameNetworkClient {

	private Client client;
	public short my_id;
	
	public Array<CharacterState__> states = new Array<CharacterState__> (true,32);
	public final int LIST_MAX_SIZE = 128;
	private MyGame game;

	public GameNetworkClient(final MyGame game) {
		this.game = game;
		client = new Client();
		Network.register(client);
		client.addListener(new Listener() {
			@Override
			public void connected(Connection connection) {
				sendIdRequest();
			}

			@Override
			public void disconnected(Connection connection) {
				Resources._messbar.setText("Disconnected from server");
			}

			@Override
			public void received(Connection connection, Object object) {
				/*
				 * Primitive signals
				 */
				if ( object instanceof __PrimitiveSignal__) {
					receive_primitivesignal((__PrimitiveSignal__) object);
				}
				
				/*
				 *  update
				 */
				if ( object instanceof __Update ){
					receive_updates((__Update) object);
				}
				
				
				/*
				 * Information about client's id, received right after the client has been connected to the server
				 */
				if ( object instanceof __YourId ) {
					receive_id((__YourId) object);
					
				}
				
				/*
				 * Room list 
				 */
				if ( object instanceof __RoomInfo[] ) {
					receive_roominfo((__RoomInfo[]) object);
				}
				
				/*
				 * Room status 
				 */
				if ( object instanceof __RoomStatus ) {
					receive_roomstatus((__RoomStatus) object);
				}
				
				/*
				 * After successful joining room
				 */
				if ( object instanceof __RoomInitializer ) {
					receive_roominitializer((__RoomInitializer) object);
				}
				
				/*
				 * User info (received after login)
				 */
				if( object instanceof __UserInfo) {
					receive_userinfo((__UserInfo) object);
				}
				
				/*
				 * 
				 */
				if( object instanceof __RoomChatBuffor ) {
					receive_roomchat((__RoomChatBuffor) object);
				}
				
				/*
				 * 
				 */
				if( object instanceof __GameInitializer ) {
					receive_gameinitializer((__GameInitializer) object);
				}
				
				/*
				 * 
				 */
				if( object instanceof __PlayerLeft ) {
					receive_playerleft((__PlayerLeft) object);
				}
				
				/*
				 * 
				 */
				if ( object instanceof __EndGame ) {
					receive_endGame((__EndGame)object);
				}
			}

			

		});
	}
	
	public void disconnect() {
		if(client.isConnected()) client.stop();
	}
	
	volatile boolean try_connecting;
	
	public void tryConnecting (final int index) {
		if (try_connecting ) {
			Resources._messbar.setText("Trying connecting to server, please wait", true);
			return;
		}
		if (client.isConnected()) {
			client.stop();
		}
		new Thread(client).start();
		new Thread() {
			public void run() {
				Resources._messbar.setText("Trying connecting to server",true);
				try_connecting=true;
				try {
					client.connect(Settings._time_out, Settings._host_address[index],Settings._port_tcp, Settings._port_udp);
				
				} catch (IOException e) {
					Resources._messbar.setText(e.getMessage());
					try_connecting=false;
					return;
				}
				try_connecting=false;
				Resources._messbar.setText("Successfully established connection");
			};
		}.start();
	}
	
	public boolean isConnected() {
		return client.isConnected();
	}

	public void sendCharacterState(CharacterState__ state) {
		if ( states.size <= LIST_MAX_SIZE) states.add(state);
		client.sendUDP(state);
	}

	

	private void receive_playerleft(__PlayerLeft object) {
		Screen s= game.getScreen();
		if ( s instanceof GameScreen ) {
			((GameScreen)s).playerLeft(object);
		}
	}
	
	private void receive_roomchat(__RoomChatBuffor chat) {
		Screen s= game.getScreen();
		if ( s instanceof RoomScreen ) {
			((RoomScreen)s).updateRoomChat(chat);
		}
	}

	private void receive_roominitializer(__RoomInitializer init) {
		Screen s= game.getScreen();
		if ( s instanceof LobalScreen ) {
			((LobalScreen)s).setRoomInitializer(init);
			Resources._messbar.setText("Joined room successfully, waiting for room status",true);
			System.out.println("223/set room initializer");
		}
	}
	
	private void receive_userinfo(__UserInfo info) {
		Screen s= game.getScreen();
		if ( s instanceof LoginScreen ) {
			((LoginScreen)s).setUserInfo(info);
		}
		if( s instanceof GameScreen ) {
			((GameScreen)s).setUserInfo(info);
		}
	}
	private void receive_endGame(__EndGame object) {
		Screen s= game.getScreen();
		if( s instanceof GameScreen ) {
			((GameScreen)s).gameEnd(object);
		}
	}
	private void receive_roomstatus(__RoomStatus object) {
		Screen s= game.getScreen();
		if ( s instanceof LobalScreen ) {
			((LobalScreen)s).setRoomStatus(object);
			Resources._messbar.setText("Joined room successfully, waiting for room initializer",true);
		}
		if ( s instanceof RoomScreen ) {
			((RoomScreen)s).setRoomStatus(object);
			System.out.println("245/in room- got__RoomStatus");
		}
	}

	private void receive_roominfo(__RoomInfo[] ri) {
		Screen s= game.getScreen();
		if ( s instanceof LobalScreen ) {
			((LobalScreen)s).setRoomListInfo(ri);
		}
		if ( s instanceof LoginScreen ) {
			((LoginScreen)s).setRoomListInfo(ri);
		}
		if ( s instanceof RoomScreen ) {
			((RoomScreen)s).setRoomListInfo(ri);
		}
		if ( s instanceof GameScreen ) {
			((GameScreen)s).setRoomListInfo(ri);
		}
	}
	
	private void receive_id(__YourId your_id) {
		GameNetworkClient.this.my_id = your_id.id;
	}

	private void receive_updates(__Update update) {
		Screen s= game.getScreen();
		if ( s instanceof GameScreen ) {
			GameStage stage= ((GameScreen)s).getInGameStage();
			if ( stage!=null ) {
				stage.update(update);
			}
		}
	}
	
	private void receive_gameinitializer(__GameInitializer init) {
		Screen s= game.getScreen();
		if ( s instanceof RoomScreen ) {
			((RoomScreen)s).setGameInitializer(init);
		}
	}
	
	private void receive_primitivesignal(__PrimitiveSignal__ signal) {
		switch (signal ) {
			/*
			 * Primitive signals : start request denied
			 */
			case __start_not_ok: {
				Screen s= game.getScreen();
				if ( s instanceof LobalScreen ) {
				}
				break;
			}
			
			/*
			 * Primitive signals : registration failed due to database error
			 */
			case __database_error_registration_failed : {
				Screen s= game.getScreen();
				if ( s instanceof LoginScreen ) {
					Resources._messbar.setText("Registration failed: Unknown database error");
				}
				break;
			}
			
			/*
			 * Primitive signals : registration failed due to invalid username length
			 */
			case __invalid_username_length : {
				Screen s= game.getScreen();
				if ( s instanceof LoginScreen ) {
					Resources._messbar.setText("Registration failed: Invalid username length(should be >"+Settings._min_reister_username_length+" & < "+Settings._max_reister_username_length+")");
				}
				break;
			}
			
	
			/*
			 * Primitive signals : registration failed , username has been used
			 */
			case __username_has_been_used : {
				Screen s= game.getScreen();
				if ( s instanceof LoginScreen ) {
					Resources._messbar.setText("Registration failed: Username has been used");
				}
				break;
			}
			
			/*
			 * Primitive signals : login failed , invalid user/password
			 */
			case __login_failed : {
				Screen s= game.getScreen();
				if ( s instanceof LoginScreen ) {
					Resources._messbar.setText("Authentication failed: Invalid username/password");
				}
				break;
			}
			
			/*
			 * Primitive signals : login successful
			 */
			case __login_successful : {
				Screen s= game.getScreen();
				if ( s instanceof LoginScreen ) {
					Resources._messbar.setText("Login successful, switching to user's interface after few seconds",true);
				}
				break;
			}
			
			
			/*
			 * Primitive signals : login successful
			 */
			case __registration_successful : {
				Screen s= game.getScreen();
				if ( s instanceof LoginScreen ) {
					Resources._messbar.setText("Registration successful");
				}
				break;
			}
			
			case __invalid_userid_length: {
				System.out.println("ha cai l");
				Screen s= game.getScreen();
				if ( s instanceof LoginScreen ) {	
					Resources._messbar.setText("Registration failed: Invalid user id length(should be >"+Settings._min_reister_userid_length+" & < "+Settings._max_reister_userid_length+")");
				
				}
				break;
			}
			
			case __userid_has_been_used : {
				Screen s= game.getScreen();
				if ( s instanceof LoginScreen ) {
					Resources._messbar.setText("Registration failed: User ID has been used");
				}
				break;
			}
			
			case __join_room_failed_password_not_match: {
				Screen s= game.getScreen();
				if ( s instanceof LobalScreen ) {
					Resources._messbar.setText("Join room failed, password does not match");
				}
				break;
			}
			
			case __join_room_failed_room_full: {
				Screen s= game.getScreen();
				if ( s instanceof LobalScreen ) {
					Resources._messbar.setText("Join room failed, room is full");
				}
				break;
			}
			
			case __room_not_exist: {
				Screen s= game.getScreen();
				if ( s instanceof LobalScreen ) {
					Resources._messbar.setText("Join room failed, room does not exist");
				}
				if ( s instanceof GameScreen ) {
					((GameScreen)s).fastMessage.trigger("Game has ended, use ESC->QuitGame",2);
				}
				break;
			}
			
			case __account_already_logged_in: {
				Screen s= game.getScreen();
				if ( s instanceof  LoginScreen ) {
					Resources._messbar.setText("This account is already logged in else where");
				}
				break;
			}
			
			case __create_room_success : {
				Screen s= game.getScreen();
				if ( s instanceof  LobalScreen ) {
					Resources._messbar.setText("Create room successfully");
				}
				break;
			}
			
			case __game_has_already_been_started : {
				Screen s= game.getScreen();
				if ( s instanceof  LobalScreen ) {
					Resources._messbar.setText("Join room failed, the game has been already started");
				}
				break;
			}
			
			case __join_game_failed_invalid_password_length : {
				Screen s= game.getScreen();
				if ( s instanceof  LobalScreen ) {
					Resources._messbar.setText("Join room failed, invalid password length");
				}
				break;
			}
			
			case __join_game_success : {
				Screen s= game.getScreen();
				if ( s instanceof  LobalScreen ) {
					Resources._messbar.setText("Join room successfully");
				}
				break;
			}
			
			case __level_restricted : {
				Screen s= game.getScreen();
				if ( s instanceof  LobalScreen ) {
					Resources._messbar.setText("Join room failed, level restricted");
				}
				break;
			}
			
			default : {
				Resources._messbar.setText("ClientUpdater#recived: unknown primitive signal");
			}
		}
	}

	public void sendReadySignal() {
		client.sendTCP(ready__);
	}
	public void sendStartSignal() {
		client.sendTCP(start__);
	}

	public void sendEvents(ShootEvent__[] event) {
		if( event.length != 0 ) client.sendUDP(event);
	}

	public void sendCreateRoomSignal(CreateRoom__ cr) {
		client.sendTCP(cr);
		System.out.println("update@282: send create room signal");
	}
	
	public void sendQuitRoomSignal() {
		client.sendTCP(__PrimitiveSignal__.quit_room__);
	}
	
	public void sendLoginInfo(LoginInfo__ li) {
		client.sendTCP(li);
	}

	public void sendRegistrationInfo(RegistrationInfo__ ri) {
		client.sendTCP(ri);
	}

	public void sendIdRequest() {
		client.sendTCP(__PrimitiveSignal__.request_id__);
	}

	public void sendJoinRoom(JoinRoom__ jr) {
		client.sendTCP(jr);
	}

	public void sendNotReadySignal() {
		client.sendTCP(__PrimitiveSignal__.not_ready__);
	}

	public void sendSwitchTeamSignal() {
		client.sendTCP(__PrimitiveSignal__.switch_team_signal__);
		
	}

	public void sendRoomChat(RoomChat__ chat) {
		client.sendTCP(chat);
	}

	public void sendKillRecord(KillRecord__ kr) {
		client.sendTCP(kr);
	}

	public void sendQuitGameSignal() {
		client.sendTCP(__PrimitiveSignal__.quitgame__);
	}

	public void sendEndGameConfirmation() {
		client.sendTCP(__PrimitiveSignal__.endgameconfirm__);
	}

	public void sendScoreWithGenerator() {
		client.sendUDP(__PrimitiveSignal__.generatorscore__);
	}
	
		
}
