package rok3.projekt.zespver3.server;

import static rok3.projekt.zespver3.client.Settings._port_tcp;
import static rok3.projekt.zespver3.client.Settings._port_udp;

import java.io.IOException;
import java.util.Random;

import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.network.Network;
import rok3.projekt.zespver3.network.packet.CharacterState__;
import rok3.projekt.zespver3.network.packet.CreateRoom__;
import rok3.projekt.zespver3.network.packet.KillRecord__;
import rok3.projekt.zespver3.network.packet.RoomChat__;
import rok3.projekt.zespver3.network.packet.ShootEvent__;
import rok3.projekt.zespver3.network.packet.JoinRoom__;
import rok3.projekt.zespver3.network.packet.LoginInfo__;
import rok3.projekt.zespver3.network.packet.RegistrationInfo__;
import rok3.projekt.zespver3.network.packet.__PrimitiveSignal__;
import rok3.projekt.zespver3.network.packet.__RoomInfo;
import rok3.projekt.zespver3.network.packet.__UserInfo;
import rok3.projekt.zespver3.network.packet.__YourId;
import rok3.projekt.zespver3.server.database.GameDatabaseAdapter;
import rok3.projekt.zespver3.utils.Hasher;

import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class CentralServer extends Server {
	static class MyConnection extends Connection {
		public boolean ready = false;
		public short room_id;
		public byte[] userid;
		public byte team;
		protected boolean online;
		public short level;
		public byte slotid;
	}

	/* utilities */
	public static Random random = new Random();
	public static GameDatabaseAdapter db = new GameDatabaseAdapter(Settings._database_url,Settings._database_table);
	public static Hasher hash = new Hasher();
	
	/* */
	public IntMap<Room> rooms = new IntMap<Room>(32);
	public __RoomInfo[] __room_list = new __RoomInfo[0];
	
	public CentralServer() {
		super();
	
		Network.register(this);
		addListener(new Listener() {
			
			@Override
			public void disconnected(Connection arg0) {
				System.out.println("server#player disconnected");
				MyConnection mc = (MyConnection) arg0;
				
				if ( mc.userid != null ) db.setOnlineStatus(new String(mc.userid), false);
				if ( mc.room_id == 0 ) return;
				
				Room r = rooms.get(mc.room_id);
				if ( r==null ) return;
				r.playerQuit(mc);
				
				if ( r.isDestroyed() ) {
					synchronized (rooms) {
						rooms.remove(r.room_id);
					}
				}
				refreshRoomList();
			}
			@Override
			public void received(Connection arg0, Object arg1) {
				MyConnection mc = (MyConnection) arg0;
				/*
				 * Primitive signals 
				 */
				if (arg1 instanceof __PrimitiveSignal__) {
					receive_primitive_signal(mc, (__PrimitiveSignal__) arg1);
				}
				

				/*
				 * Character state : position, rotation, local timestamp, source's id
				 */
				if (arg1 instanceof CharacterState__) {
					receive_characterState(mc,(CharacterState__)arg1);
				}
				
				
				/*
				 * Events
				 */
				if (arg1 instanceof ShootEvent__[] ) {
					receive_events(mc,(ShootEvent__[])arg1);
				}
				
				
				/*
				 * Join room 
				 */
				if( arg1 instanceof JoinRoom__ ) {
					receive_joinroom(mc,(JoinRoom__)arg1);
					
				}
				
				/*
				 * create room
				 */
				if( arg1 instanceof CreateRoom__ ) {
					receive_createroom(mc,(CreateRoom__)arg1);
				}
				
				
				/*
				 * login
				 */
				if (arg1 instanceof LoginInfo__ ) {
					receive_login(mc,(LoginInfo__)arg1);
				}	
				
				
				/*
				 * register 
				 * username max length = 20;
				 * 
				 * check Settings > database setting
				 */
				if ( arg1 instanceof RegistrationInfo__ ) {
					receive_registrationinfo(mc,(RegistrationInfo__)arg1);
				}
				
				if( arg1 instanceof RoomChat__ ) {
					receive_roomchat(mc,(RoomChat__) arg1 );
				}
				
				if( arg1 instanceof KillRecord__ ) {
					receive_killrecord(mc,(KillRecord__) arg1);
				}
			}

			
		});
	}


	@Override
	protected Connection newConnection() {
		return new MyConnection();
	}


	private void receive_killrecord(MyConnection mc, KillRecord__ kr) {
		System.out.println("server# got killrecord");
		Room r = rooms.get(mc.room_id);
		r.playerGetKill(mc,kr);
	}
	
	void receive_primitive_signal(MyConnection mc,__PrimitiveSignal__ p) {
		switch (p) {
			/*
			 * Primitive signal - ready for start signal
			 */
			case ready__: {
				Room r = rooms.get(mc.room_id);
				if (r != null ) r.playerReady(mc,true);
				break;
			}
			
			case not_ready__: {
				Room r = rooms.get(mc.room_id);
				if (r != null ) r.playerReady(mc,false);
				break;
			}
			
			/*
			 * Primitive signals - start signal
			 */
			case start__: {
				Room r = rooms.get(mc.room_id);
				if (!r.playerStartGame())  {
					sendToUDP(mc.getID(), __PrimitiveSignal__.__start_not_ok );
				} else {
					refreshRoomList();
				}
				break;
			}
			
			case request_id__ : {
				__YourId yourid = new __YourId();
				yourid.id = (short) mc.getID();
				sendToUDP(mc.getID(), yourid);
				break;
			}
			
			/*
			 * Primitive signal - quit room
			 */
			case quit_room__ : {
				Room r = rooms.get(mc.room_id);
				r.playerQuit(mc);
				if ( r.isDestroyed() ) {
					synchronized (rooms) {
						rooms.remove(r.room_id);
					}
				}
				refreshRoomList();
				break;
			}
			
			case switch_team_signal__ : {
				Room r = rooms.get(mc.room_id);
				if ( r!=null) r.playerSwitchTeam(mc);
				
				break;
			}
			
			case quitgame__ : {
				Room r= rooms.get(mc.room_id);
				if(r== null )break;
				r.playerQuit(mc);
				if ( r.isDestroyed() ) {
					synchronized (rooms) {
						rooms.remove(r.room_id);
					}
					refreshRoomList();
				}
				synchronized (__room_list) {
					sendToUDP(mc.getID(),__room_list);
				}
				__UserInfo info = db.getUserInfo(new String(mc.userid));
				sendToUDP(mc.getID(),info);
				
				break;
			}
			
			case endgameconfirm__ : {
				if ( mc.room_id != 0 ) break;
				synchronized (__room_list) {
					sendToUDP(mc.getID(),__room_list);
				}
				__UserInfo info = db.getUserInfo(new String(mc.userid));
				sendToUDP(mc.getID(),info);
				break;
			}
			
			case generatorscore__ : {
				Room r= rooms.get(mc.room_id);
				if( r== null ) break;
				r.playGetScoreFromGenerator(mc);
			}
			
			default: {
				//System.out.println("Recived unknown primitive signal");
			}
			
		}
	}

	void refreshRoomList() {
		synchronized (__room_list) {
			short index=0;
			__room_list = new __RoomInfo[rooms.size];
			System.out.println("server# room size = " + rooms.size);
			for ( Room r : rooms.values() ) {
				if ( r.isRunningGame() || r.isDestroyed() ) continue;
				__RoomInfo info = r.getRoomInfo();
				__room_list[index++] = info;
			}
			for (Connection c : getConnections() )
				if ( ((MyConnection)c).room_id == 0 && c.isConnected() ) sendToUDP(c.getID(),__room_list);
		}
	}


	void receive_characterState(MyConnection mc,CharacterState__ cs ) {
		//System.out.println("Server# got character state1");
		if ( mc.room_id == 0 ) return;
		Room r = rooms.get(mc.room_id);
		if ( r== null ) {
			sendToUDP(mc.getID(), __PrimitiveSignal__.__room_not_exist);
			return;
		}
		r.playerUpdateState (mc,cs);
		//System.out.println("Server# got character state2");
	}
	
	private void receive_events(MyConnection mc, ShootEvent__[] arg1) {
		if ( mc.room_id == 0 ) return;
		Room r = rooms.get(mc.room_id);
		if ( r == null ) {
			sendToUDP(mc.getID(), __PrimitiveSignal__.__room_not_exist);
			return;
		}
		r.playerUpdateEvent (mc,(ShootEvent__[]) arg1);
	}

	private void receive_registrationinfo(MyConnection mc,
			RegistrationInfo__ arg1) {
		RegistrationInfo__ ri = (RegistrationInfo__) arg1;
		if ( ri.username.length > Settings._max_reister_username_length || ri.username.length < Settings._min_reister_username_length ) {
			/*
			 * invalid username length
			 */
			sendToUDP(mc.getID(), __PrimitiveSignal__.__invalid_username_length);
		} else if( ri.userid.length > Settings._max_reister_userid_length || ri.userid.length < Settings._min_reister_userid_length) {
			/*
			 * invalid user id length
			 */
			sendToUDP(mc.getID(), __PrimitiveSignal__.__invalid_userid_length);
		} 
		else {
			String username = new String(ri.username);
			String userid = new String(ri.userid);
			if (db.usernameExists(username) ){ 
				sendToUDP(mc.getID(), __PrimitiveSignal__.__username_has_been_used );
			} else if (db.useridExists(userid)) {
				sendToUDP(mc.getID(), __PrimitiveSignal__.__userid_has_been_used );
			}
			else {
				String password_with_salt = new StringBuilder(new String(ri.one_time_hashed_password)).append(ri.username[1]).append(ri.username[2]).toString();
				if ( db.addUser(username, hash.sha1(password_with_salt), userid)) {
					sendToUDP(mc.getID(), __PrimitiveSignal__.__registration_successful);
				} else {
					/*
					 * unknown database error, registration failed
					 */
					sendToUDP(mc.getID(), __PrimitiveSignal__.__database_error_registration_failed);
				}
			}
		}
	}

	private void receive_login(MyConnection mc, LoginInfo__ arg1) {
		LoginInfo__ lg = (LoginInfo__) arg1;
		String username = new String(lg.username);
		
		if ( !db.usernameExists(username) ){
			/*
			 * error: invalid username
			 */
			sendToUDP(mc.getID(),__PrimitiveSignal__.__login_failed );
			
		} else {
			String double_hashed_password = db.getPassword(username);
			String password_with_salt = new StringBuilder(new String(lg.one_time_hashed_password)).append(lg.username[1]).append(lg.username[2]).toString();
			
			if ( double_hashed_password.equals(hash.sha1(password_with_salt))) {
				/*
				 * login successful
				 */

				__UserInfo info = db.getUserInfo(username);
				if (!info.online )  {
					sendToUDP(mc.getID(),__PrimitiveSignal__.__login_successful );
					sendToUDP(mc.getID(),info );
					synchronized(__room_list){
						sendToUDP(mc.getID(),__room_list);
					}
					mc.online = true;
					mc.userid = info.userid;
					db.setOnlineStatus(new String(info.userid),true);
				} else {
					sendToUDP(mc.getID(),__PrimitiveSignal__.__account_already_logged_in );
				}
			} else {
				/*
				 * password not match
				 */
				sendToUDP(mc.getID(),__PrimitiveSignal__.__login_failed );
			}
		}
		
	}

	private void receive_joinroom(MyConnection mc, JoinRoom__ arg1) {
		JoinRoom__ jr = (JoinRoom__) arg1;
		Room r = rooms.get(jr.room_id);

		if ( r== null ) {
			/*
			 * room not exist
			 */

			sendToUDP(mc.getID(), __PrimitiveSignal__.__room_not_exist);
		} else {
			if ( r.playerJoin(mc, jr.room_password)) {
				/*
				 * success
				 */
				
				sendToUDP(mc.getID(), r.getRoomInitializer());
				refreshRoomList();
				
			} 
		}
	}
	
	private void receive_createroom(MyConnection mc, CreateRoom__ cr) {
		Room r=null;
		synchronized (rooms) {
			short room_id;
			do {
				room_id = (short) (random.nextInt(Short.MAX_VALUE - 1)  + 1);
			} while (!isRoomIdValid(room_id));
			
			r = new Room(this,room_id, mc, cr);
			mc.room_id = room_id;
			rooms.put(room_id, r);
			refreshRoomList();
		}
		
		new Thread(r).start();
		sendToUDP(mc.getID(), r.getRoomInitializer());
	}

	private void receive_roomchat(MyConnection mc, RoomChat__ chat) {
		Room r = rooms.get(mc.room_id);
		if ( r == null ) {
			sendToUDP(mc.getID(), __PrimitiveSignal__.__room_not_exist);
			return;
		}
		r.playerSays(mc,chat);
	}
	
	private boolean isRoomIdValid(short id) {
		for (Room r: rooms.values()) { 
			if (r.room_id == id)
				return false;
		}
		
		return true;
	}
	
	public static void main (String[] args ) {
		final CentralServer server = new CentralServer();
		
		try {
			server.bind(_port_tcp, _port_udp);
		} catch (NumberFormatException | IOException e) {
			//System.out.println("Error: server cant bind");
			return;
		}
		
		new Thread(server).start();
	}
}
