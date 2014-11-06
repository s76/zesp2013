package rok3.projekt.zespver3.network.packet;

public enum __PrimitiveSignal__ {
	/**
	 * from client
	 */
	ready__, not_ready__,start__, quit_room__,switch_team_signal__,request_id__,
	
	
	/**
	 * from server 
	 */
	__start_not_ok, 
	
	/* login */
	__login_successful, __login_failed, __account_already_logged_in,
	
	/* registration */
	 __invalid_username_length, __username_has_been_used, __registration_successful,
	__database_error_registration_failed, __userid_has_been_used, __invalid_userid_length, 
	
	/*create room */
	__create_room_success,
	
	/* join room */
	 __room_not_exist, __game_has_already_been_started,__join_room_failed_password_not_match, 
	 __join_game_failed_invalid_password_length, __level_restricted, 
	 __join_game_success, __join_room_failed_room_full, quitgame__,
	 
	 __team2_win, __team1_win, endgameconfirm__, generatorscore__;
}
