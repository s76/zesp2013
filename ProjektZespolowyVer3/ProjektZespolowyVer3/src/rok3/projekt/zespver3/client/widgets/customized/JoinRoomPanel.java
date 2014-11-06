package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.client.widgets.models.ScrollList;
import rok3.projekt.zespver3.network.packet.JoinRoom__;
import rok3.projekt.zespver3.network.packet.__RoomInfo;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class JoinRoomPanel extends Group {
	private ScrollList slist;
	private boolean room_list_changed;
	private __RoomInfo[] roominfo;
	private TextField passfield;

	public JoinRoomPanel() {
		super();
		
		TextButton button_join_room = new TextButton("Join",
				Resources._skin);
		button_join_room.setWidth(80);
		button_join_room.setPosition(200, 0);
		button_join_room.addListener(new ClickListener() {
			JoinRoom__ jr = new JoinRoom__();
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!Resources._nclient.isConnected()) {
					Resources._messbar.setText("Not connected");
					return;
				}
				if ( roominfo == null || roominfo.length == 0 ) {
					Resources._messbar.setText("No room selected");
					return;
				
				}
				__RoomInfo room = roominfo[slist.getSelectedIndex()];
				if ( room.password_protected ) {
					jr.room_password=passfield.getText().getBytes();
					if ( jr.room_password.length < Settings._min_room_password_length) {
						Resources._messbar.setText("Room password is too short, min "+Settings._min_room_password_length+" characters");
						return;
					}
				} else {
					jr.room_password=null;
				}
				jr.room_id = room.room_id;
				
				Resources._nclient.sendJoinRoom(jr);
			}
		});
		
		
		slist = new ScrollList(Resources._skin);
		slist.setPosition(0, 50);
		slist.setStableSize(540, 250);
	

		passfield = new TextField("",Resources._skin){
			@Override
			public void act(float delta) {
				int i = slist.getSelectedIndex();
				if( roominfo.length > 0 && i != -1 && roominfo[slist.getSelectedIndex()] != null && roominfo[slist.getSelectedIndex()].password_protected ) {
					passfield.setVisible(true);
				} else {
					passfield.setText("");
					passfield.setVisible(false);
				}
				super.act(delta);
			}
		};
		passfield.setPasswordCharacter('*');
		passfield.setPasswordMode(true);
		passfield.setMaxLength(Settings._max_room_password_length);
		passfield.setVisible(false);
		passfield.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				passfield.setText("");
				return false;
			}
		});
		
		addActor(button_join_room);
		addActor(slist);
		addActor(passfield);
	}
	
	
	@Override
	public void act(float delta) {
		if ( room_list_changed ) {
			StringBuilder[] t = new StringBuilder[roominfo.length];
			for( int i = 0 ; i< roominfo.length; i++ ) {
				__RoomInfo room = roominfo[i];
				t[i] = new StringBuilder();
				if( room == null ) continue;
				t[i].append("roomID( ").append(room.room_id).append(')').append(',').append(' ')
					.append("map( ").append(Settings._map_names[room.mapselection]).append(')').append(',').append(' ')
					.append("status( ").append(room.current_playernb).append('/').append(room.max_playernb).append(')').append(',').append(' ')
					.append("locked( ").append(room.password_protected==true?"yes":"no").append(')').append(',').append(' ')
					.append("level( ").append(room.res_min).append('-').append(' ').append(room.res_max).append(')');
			}
			slist.setItems(t);
			room_list_changed=false;
		}
		super.act(delta);
	}
	
	public void setNewRoomList(__RoomInfo[] info) {
		room_list_changed=true;
		roominfo= info;
	}
}
