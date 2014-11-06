package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class RoomControlPanel extends Table {
	private static float standar_delay=0.5f;
	private float lock;
	private boolean readyIsOn;
	public RoomControlPanel (boolean isRoomMaster) {
		super();
		
		defaults().pad(3, 0, 3, 15);
		defaults().left();
		defaults().width(140);
		defaults().height(45);
		
		
		TextButton switch_team = new TextButton("Switch Team", Resources._skin);
		switch_team.addListener(new ClickListener() {
			float last_try;
			public void clicked(InputEvent event, float x, float y) {
				if ( lock - last_try > standar_delay) {
					Resources._nclient.sendSwitchTeamSignal();
					last_try=lock;
				}
			};
		} );
		
		TextButton quit_room = new TextButton("Quit Room", Resources._skin);
		quit_room.addListener(new ClickListener() {
			float last_try;
			public void clicked(InputEvent event, float x, float y) {
				if ( lock - last_try > standar_delay) {
					Resources._nclient.sendQuitRoomSignal();
					last_try=lock;
				}
			};
		} );
		
		TextButton ready = new TextButton("Ready", Resources._skin){
			@Override
			public boolean isPressed() {
				return readyIsOn;
			}
		};
		ready.addListener(new ClickListener() {
			float last_try;
			public void clicked(InputEvent event, float x, float y) {
				if ( lock - last_try > standar_delay) {
					if ( !readyIsOn ) {
						Resources._nclient.sendReadySignal();
						readyIsOn = !readyIsOn;
					} else {
						Resources._nclient.sendNotReadySignal();
						readyIsOn = !readyIsOn;
					}
					last_try=lock;
				}
			};
		} );
		
		add(switch_team);row();
		add(quit_room);row();
		add(ready);row();
		if( isRoomMaster ) {
			TextButton start = new TextButton("Start",Resources._skin);
			start.addListener(new ClickListener() {
				float last_try;
				public void clicked(InputEvent event, float x, float y) {
					if ( lock - last_try > standar_delay) {
						Resources._nclient.sendStartSignal();
						last_try=lock;
					}
				};
			} );
			add(start);
		}
		row();
	}
	
	@Override
	public void act(float delta) {
		lock+= delta;
		super.act(delta);
	}
}
