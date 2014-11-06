package rok3.projekt.zespver3.server;

import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.network.packet.CharacterState__;
import rok3.projekt.zespver3.network.packet.CreateRoom__;
import rok3.projekt.zespver3.network.packet.KillRecord__;
import rok3.projekt.zespver3.network.packet.RoomChat__;
import rok3.projekt.zespver3.network.packet.ShootEvent__;
import rok3.projekt.zespver3.network.packet.__CharacterState;
import rok3.projekt.zespver3.network.packet.__EndGame;
import rok3.projekt.zespver3.network.packet.__GameInitializer;
import rok3.projekt.zespver3.network.packet.__PlayerLeft;
import rok3.projekt.zespver3.network.packet.__PrimitiveSignal__;
import rok3.projekt.zespver3.network.packet.__RoomChatBuffor;
import rok3.projekt.zespver3.network.packet.__RoomInfo;
import rok3.projekt.zespver3.network.packet.__RoomInitializer;
import rok3.projekt.zespver3.network.packet.__RoomStatus;
import rok3.projekt.zespver3.network.packet.__ShootEvent;
import rok3.projekt.zespver3.network.packet.__Update;
import rok3.projekt.zespver3.server.CentralServer.MyConnection;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public class Room implements Runnable {
	final static int in_game_update_interval = 30;
	final static byte team_number =2;

	private CentralServer server;
	private volatile byte players_counter;
	private MyConnection[][] inroom_players = new MyConnection[team_number][];
	private boolean[][] changes = new boolean[team_number][];
	
	/* room settings */
	public short room_id;
	
	private byte[] password;
	private byte max_player;
	private short max_score;
	private boolean password_protected;
	private byte mapselection;
	private short res_min;
	private short res_max;

	
	private volatile boolean destroyed;
	private volatile boolean before_game;
	private volatile boolean in_game;
	
	/* tmp */
	private __Update updates = new __Update();
	private __RoomStatus roomstatus = new __RoomStatus();
	private __RoomInitializer roomInit = new __RoomInitializer();
	private __RoomInfo info = new __RoomInfo();
	private __PlayerLeft player_left;
	private boolean stop_score_counter;
	
	/* arrays states , ingame_players and team_score have exactly the same length and order */
	private MyConnection[] ingame_players;
	private __CharacterState[] states;
	private short[] team_score;
	private Array<__ShootEvent> events;
	
	/* chat thread */
	private Runnable chatthread;
	private __RoomChatBuffor roomchatbuffor = new __RoomChatBuffor();
	
	
	
	
	
	public Room(CentralServer server, short room_id, MyConnection room_master, CreateRoom__ cr) {
		this.server = server;
		this.room_id = room_id;
		for (byte team=0;team<team_number;team++ ) {
			inroom_players[team] = new MyConnection[cr.max_player/team_number];
			changes[team] = new boolean[cr.max_player/team_number];
		}
		
		this.password_protected = cr.password == null ? false : true;
		if (this.password_protected) {
			this.password = cr.password;
			if ( this.password.length > Settings._max_room_password_length ) {
				byte[] tmp = this.password;
				this.password = new byte[Settings._max_room_password_length ];
				System.arraycopy(tmp, 0, cr.password, 0, Settings._max_room_password_length);
			}
		}
		this.max_player = cr.max_player;
		this.mapselection = cr.mapIndexSelection;
		this.res_min = cr.res_min;
		this.res_max = cr.res_max;
		this.max_score = cr.max_score;
		
		before_game = true;
		in_game = false;
		
		roomstatus.changed = new boolean [cr.max_player];
		roomstatus.userid = new byte[cr.max_player][];
		roomstatus.ready = new boolean[cr.max_player];
		
		inroom_players[0][0] = room_master;
		room_master.team = 0;
		room_master.slotid = 0;
		room_master.room_id = room_id;
		players_counter=1;
		changes[0][0]=true;
		
		roomInit.max_pl = this.max_player;
		roomInit.room_master_id = room_master.getID();
		roomInit.room_master_userid = room_master.userid;
		roomInit.max_score = this.max_score;
		
		chatthread = new Runnable() {
			public void run() {
				while (before_game) {
					synchronized (chatthread) {
						try {
							chatthread.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					updateChat();
				}
			}
		};
		info.room_id = room_id;
		
		player_left = new __PlayerLeft();
	}


	private void updateChat() {
		sendToAllTCP(roomchatbuffor);
	};
	
	public boolean playerJoin(MyConnection player, byte[] room_password) {
		if (players_counter == max_player ) {
			server.sendToUDP(player.getID(), __PrimitiveSignal__.__join_room_failed_room_full );
			return false;
		}
		if (player.level < res_min || player.level > res_max) {
			server.sendToUDP(player.getID(), __PrimitiveSignal__.__level_restricted);
			return false;
		}
		if (password_protected) {
			if (room_password == null) {
				server.sendToUDP(player.getID(), __PrimitiveSignal__.__join_room_failed_password_not_match);
				return false;
			}
			if (this.password.length != room_password.length) {
				server.sendToUDP(player.getID(), __PrimitiveSignal__.__join_game_failed_invalid_password_length );
				return false;
			}
			for (short i = 0; i < this.password.length; i++) {
				if (this.password[i] != room_password[i]) {
					server.sendToUDP(player.getID(), __PrimitiveSignal__.__join_room_failed_password_not_match );
					return false;
				}
			}
		}
		
		synchronized (inroom_players) {
			if (in_game) {
				server.sendToUDP(player.getID(), __PrimitiveSignal__.__game_has_already_been_started );
				return false;
			}
			if (destroyed) {
				server.sendToUDP(player.getID(), __PrimitiveSignal__.__room_not_exist );
				return false;
			}
			loop_label:
				for(byte team=0;team<team_number;team++){
					for(byte slot=0;slot<inroom_players[team].length;slot++){
						if(inroom_players[team][slot] == null ) {
							inroom_players[team][slot] = player;
							player.room_id=room_id;
							player.team=team;
							player.slotid=slot;
							players_counter ++;
							changes[team][slot]=true;
							break loop_label;
						}
					}
				}
		}
		
		synchronized (this) {
			notify();
		}
		
		return true;
	}

	public void playerQuit(MyConnection player) {		
		//System.out.println("room#player quit");
		/*
		 * a player's hp is always >=0 , 
		 * assigning -1 to player's hp will indicate that this player has already quited the room, 
		 * and needs to be removed from map
		 */  
		if( in_game) {
			states[player.slotid].hp = -1;
			player_left.userid = player.userid;
			sendToAllTCP(player_left);
		}
		if ( !in_game ) {
			synchronized (inroom_players) {
				inroom_players[player.team][player.slotid] = null;
				changes[player.team][player.slotid] = true;
			}
		}
		
		player.room_id=0;
		player.ready=false;
		players_counter --;
		System.out.println("room#player counter = "+ players_counter);
		if ( players_counter <= 0) {
			destroy();
			return;
		}
		if( !in_game ) {
			synchronized (this) {
				notify();
			}
		}
	}

	private void sendToAllUDP(Object object) {
		if ( before_game ) {
			synchronized (inroom_players) {
				for(byte i=0;i<team_number;i++){
					for (MyConnection m : inroom_players[i]) {
						if (m!=null)server.sendToUDP(m.getID(), object);
					}
				}
			}
			//System.out.println("room#send to all : before_game");
		}
		if ( in_game ) {
			synchronized (ingame_players) {
				for (MyConnection m : ingame_players) {
					if (m!=null)server.sendToUDP(m.getID(), object);
				}
			}
			//System.out.println("room#send to all : in_game");
		}
	}
	
	private void sendToAllTCP(Object object) {
		if ( before_game ) {
			synchronized (inroom_players) {
				for(byte i=0;i<team_number;i++){
					for (MyConnection m : inroom_players[i]) {
						if (m!=null)server.sendToTCP(m.getID(), object);
					}
				}
			}
			//System.out.println("room#send to all : before_game");
		}
		if ( in_game ) {
			synchronized (ingame_players) {
				for (MyConnection m : ingame_players) {
					if (m!=null)server.sendToTCP(m.getID(), object);
				}
			}
			//System.out.println("room#send to all : in_game");
		}
	}

	@Override
	public void run() {

		new Thread(chatthread).start();
		
		while (before_game) {
			updateRoomStatus();
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}


		long deltatime = 0;
		long lastStartTime = 0;
		
		while (in_game) {
			/* update states and events, do physic stuff */

			lastStartTime = System.currentTimeMillis();
			if (deltatime < in_game_update_interval) {
				try {
					Thread.sleep(in_game_update_interval - deltatime);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				doCaculation();
				updateToClients();

				deltatime -= in_game_update_interval;
			}
			deltatime += System.currentTimeMillis() - lastStartTime;
		}

		{
			/* after game */
			System.out.println("room#disposed");
		}

	}
	
	
	private void updateToClients() {
		if (destroyed ) return;
		synchronized (updates) {
			if(!stop_score_counter) { 
				short[] total_team_score = new short[team_number];
				for (byte slot=0;slot<ingame_players.length;slot++ ) {
					total_team_score[ingame_players[slot].team] += team_score[ingame_players[slot].slotid];
				}
				updates.total_team_score = total_team_score;
			}
			if ( updates.total_team_score[0] >= this.max_score || updates.total_team_score[1] >= this.max_score ) {
				stop_score_counter=true;
				byte teamwin= (byte) (updates.total_team_score[0] > updates.total_team_score[1]? 0: 1);
				
				short highest_balance=3;
				DelayedRemovalArray<MyConnection> mvp = new DelayedRemovalArray<MyConnection >(false,0 ,MyConnection.class);
				MyConnection current_best=null;
				boolean dup=false;
				short k;
				for (MyConnection player : ingame_players) {
					k= (short) (states[player.slotid].kills - states[player.slotid].deaths);
					if (k > highest_balance ) {
						current_best = player;
						dup = false;
						highest_balance= k;
					} else if ( k == highest_balance ) {
						current_best = player;
						dup = true;
					}
				}
				if ( current_best != null ) mvp.add(current_best);
				if ( dup ) {
					System.out.println("room: ingameplayer size="+ingame_players.length);
					for (MyConnection player : ingame_players) {
						if ( player == current_best ) continue;
						k= (short) (states[player.slotid].kills - states[player.slotid].deaths);
						if (k == highest_balance ) {
							System.out.println("room;mvp add, k == "+ k);
							mvp.add(player);
						} 
					}
					short highest_kill=3;
					
					for (MyConnection player: mvp) {
						if ( states[player.slotid].kills >= highest_kill ) {
							highest_kill = states[player.slotid].kills;
						}
					}
					
					mvp.begin();
					for (MyConnection player: mvp) {
						if ( states[player.slotid].kills < highest_kill ) {
							mvp.removeValue(player, true);
						}
					}
					mvp.end();
				}
				
				int[] mvp_ids = new int[mvp.size];
				for (short i=0;i<mvp.size;i++) {
					MyConnection player = mvp.get(i);
					if (player == null ) {
						System.out.println("room: mvp player = null");
						continue;
					} 
					mvp_ids[i] = player.getID();
				}
				gameEnd(teamwin,mvp_ids);
			}
			updates.states = states;
			updates.events = events.toArray();
			sendToAllUDP(updates);
			events.clear();
			if(stop_score_counter ) {
				destroy();
				synchronized (server.rooms) {
					server.rooms.remove(room_id);
				}
				server.refreshRoomList();
				for ( MyConnection player : ingame_players ) {
					player.room_id=0;
					player.ready=false;
				}
				sendToAllUDP(__PrimitiveSignal__.__room_not_exist);
			}
		}
	}

	private void gameEnd(byte teamwin, int[] mvp_ids) {
		__EndGame eg = new __EndGame();
		eg.winteam = teamwin;
		eg.mvp_id = mvp_ids;
		
		sendToAllTCP(eg);
	}


	private void doCaculation() {
		
	}


	public boolean playerStartGame() {
		synchronized (inroom_players) {
			for (MyConnection m : inroom_players[0]) {
				if ( m == null ) continue;
				if ( !m.ready ) return false;
			}
			for (MyConnection m : inroom_players[1]) {
				if ( m == null ) continue;
				if ( !m.ready )return false;
			}
			
			/* arrays states , ingame_players and team_score have exactly the same length and order */
			states = new __CharacterState[players_counter];
			ingame_players = new MyConnection[players_counter];
			team_score = new short[players_counter];
			
			events = new Array<__ShootEvent>(false,32,__ShootEvent.class);
		
			byte newslot=0;
			
			for(int team=0;team<team_number;team++ ){
				for (int slot=0;slot<inroom_players[team].length;slot++){
					MyConnection m = inroom_players[team][slot];
					if( m != null ) {
						ingame_players[newslot] = m;
						m.slotid = newslot;
						states[newslot] = new __CharacterState();
						states[newslot].id = m.getID();
						states[newslot].hp = 100;
						states[newslot].x = m.team==0? 5.5f:962.5f;
						states[newslot].y = m.team==0? 16f:558f;
						
						newslot++;
					}
				}
			}
			
			__GameInitializer gameinit = new __GameInitializer();
			gameinit.player_ids = new int[players_counter];
			gameinit.mapselection = mapselection;
			gameinit.player_userids = new byte[players_counter][];
			gameinit.team = new byte[players_counter];
			
			for ( short i =0;i< ingame_players.length; i++ ) {
				MyConnection m = ingame_players[i];
				gameinit.player_ids[i] = m.getID();
				gameinit.player_userids[i] = m.userid;
				gameinit.team[i]=m.team;
			}
			

			sendToAllUDP(gameinit);
			
			before_game = false;	
			in_game = true;
			inroom_players = null; /* free memory of players, no longer used */
			gameinit = null; /* free mememory, no loger used */
			synchronized (this) {
				notify();
			}
		}
		return true;
	}
	
	public __RoomStatus getRoomStatus() {
		synchronized (inroom_players) {
			System.out.println("room: inroom_players size =" + inroom_players.length);
			for(byte team=0;team<team_number;team++){
				for(byte slot=0;slot<inroom_players[team].length;slot++){

					byte idx = (byte) (team*inroom_players[team].length+slot);
					
					if ( changes[team][slot]) {
						MyConnection m = inroom_players[team][slot];
						
						if( m== null ) {
							roomstatus.userid[idx] = null;
						} else {
							roomstatus.userid[idx] = m.userid;
							roomstatus.ready[idx] = m.ready;
						}
						
						changes[team][slot]=false;
					} 
				}
			}
		}
		return roomstatus;
	}
	public void updateRoomStatus() {
		sendToAllUDP(getRoomStatus());
	}
	
	public boolean playerSwitchTeam (MyConnection player) {
		int team = player.team == 0? 1:0;
		boolean result=false;
		if ( in_game ) return false;
		synchronized (inroom_players) {
			for(byte slot=0;slot<inroom_players[team].length;slot++){
				if ( inroom_players[team][slot] == null ) {
					inroom_players[player.team][player.slotid]=null;
					changes[player.team][player.slotid]=true;
					
					inroom_players[team][slot] = player;
					player.team= (byte) team;
					player.slotid=slot;
					changes[team][slot] =true;
					
					result=true;
					break;
				}
			}
		}
		synchronized (this) {
			notify();
		}
		return result;
	}
	
	public void playerUpdateState(MyConnection mc, CharacterState__ cs) {
		//System.out.println("room#"+(new String(mc.userid))+" updates ");
		synchronized (updates) {
			synchronized (states[mc.slotid]) {
				states[mc.slotid].x = cs.x;
				states[mc.slotid].y = cs.y;
				states[mc.slotid].hp = cs.hp;
				states[mc.slotid].rotation = cs.rotation;
			}
		}
		//System.out.println("room#player updates state2");
	}

	public void playerUpdateEvent(MyConnection mc, ShootEvent__[] newevents) {
		synchronized (updates) {
			for( ShootEvent__ e : newevents) {
				if ( e != null ) {
					__ShootEvent ev = new __ShootEvent();
					ev.source_id = mc.getID();
					events.add(ev);
				}
			}
		}
	}


	public __RoomInitializer getRoomInitializer() {
		return roomInit;
	}

	public void destroy() {
		//System.out.println("room#destroy");
		before_game = false;
		in_game = false;
		destroyed=true;
	}

	public void playerReady(MyConnection player,boolean ready) {
		player.ready=ready;
		changes[player.team][player.slotid]=true;
		synchronized (this) {
			notify();
		}
	}

	public void playerSays(MyConnection player,RoomChat__ chat) {
		roomchatbuffor.userid = player.userid;
		roomchatbuffor.content = chat.content;
		synchronized (chatthread) {
			chatthread.notify();
		}
	}


	public boolean isDestroyed() {
		return destroyed;
	}

	public boolean isRunningGame() {
		return in_game;
	}


	public __RoomInfo getRoomInfo() {
		info.room_id = room_id;
		info.current_playernb = players_counter;
		info.max_playernb = max_player;
		info.mapselection = mapselection;
		info.password_protected = password_protected;
		info.res_min = res_min;
		info.res_max = res_max;
		return info;
	}

	public void playerGetKill(MyConnection killed, KillRecord__ kr) {
		for (MyConnection killer : ingame_players ) {
			if ( killer.getID() == kr.source_id) {
				synchronized (updates) {
					team_score[killer.slotid] += Settings._score_per_kill;
					states[killer.slotid].kills +=1;
					states[killed.slotid].deaths +=1;
				}
				System.out.println("room# "+(new String(killer.userid))+" team("+killer.team+") got a kill!");
			}
		}
	}


	public void playGetScoreFromGenerator(MyConnection player) {
		synchronized (updates) {
			System.out.println("room: " + (new String(player.userid)) + " got score from generator");
			team_score[player.slotid] += Settings._score_from_generator;
			System.out.println("room: score total: ");
			for ( MyConnection m : ingame_players ) {
				System.out.print(team_score[m.slotid]+ " ");
			}
			System.out.println();
		}
	}
}
