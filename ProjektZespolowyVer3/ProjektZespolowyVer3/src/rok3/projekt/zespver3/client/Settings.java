package rok3.projekt.zespver3.client;

import com.badlogic.gdx.Input.Keys;

public class Settings {
	/* 
	 * game display settings 
	 * 
	 * */
	static public final String game_name="SpaceShooter";
	static public final int _window_width=1024;
	static public final int _window_height=648;
	static public final  boolean full_screen=false;
	static public final boolean useGL20=true;
	static public final boolean resizable=false;
	static public final String icon_path = "misc/Game3.ico";
	
	
	/* 
	 * network settings 
	 * 
	 * */
	public static int _port_tcp = 4487;
	public static int _port_udp = 4440;
	public static int _time_out = 5000;
	public static String[] _host_address = {"127.0.0.1" , "194.29.146.42", "192.227.166.97"};
	public static String[] _host_dns = {"localhost" , "len.iem.pw.edu.pl","customserver1"};
	
	
	/* 
	 * in game settings 
	 * 
	 * */
	public static String[] _map_names= {"deadspace", "map2", "map3", "map4", "nightmare", "map6" };
	public static String[] _map_url= {"map/map1.tmx", "map/map2.tmx", "map/map3.tmx", "map/map4.tmx", "map/map5.tmx", "map/map6.tmx" };
	static public int _keys_left = Keys.A;
	static public int _keys_right = Keys.D;
	static public int _keys_up = Keys.W;
	static public int _keys_down = Keys.S;
	public static boolean _debug_on = false;
	public static short _min_room_password_length=3;
	public static short _max_room_password_length = 15;
	public static Byte[] _maxplayer_options = { 2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32 }; 
	
//	public static int _score_to_win = 1000;
	public static short _score_per_kill = 100;
	public static float _respawn_time = 5;
	public static Short[] _maxscore_options={ 300, 500,700,1000, 1500, 2500 };
	public static float _generator_generating_time = 3;
	public static int _score_from_generator = 80;
	
	/*
	 * database settings
	 * 
	 */
	static public String _database_url = "jdbc:sqlite:data.db";
	public static short _max_reister_username_length = 25;
	public static short _max_reister_userid_length=12;
	public static short _min_reister_username_length=3;
	public static short _min_reister_password_length=3;
	public static short _min_reister_userid_length=3;
	public static String _database_table="Users";
	
	
	/*
	 * graphics
	 */
	public static String _serverselection_background="misc/severselection_bg.png";
	public static String _serverselection_bar = "misc/severselection_bar.png";
	public static float _serverselection_bar_height= 80;
	public static String _messagebar_background="misc/severselection_bar.png";
	public static String _hud_background="misc/severselection_bar.png";;
}
