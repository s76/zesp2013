package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.widgets.customized.Slot.Team;
import rok3.projekt.zespver3.network.packet.__RoomStatus;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class RoomDisplayPanel extends Group{
	public static int max_slot = 32;
	Slot[] slots;
	public RoomDisplayPanel(String roommaster, short room_max_pl, short room_max_score) {
		super();
		
		Table table = new Table();
		
		slots = new Slot[room_max_pl];
		
		table.defaults().pad(3, 0, 3, 20);
		table.defaults().left();
		table.defaults().width(220);
		table.defaults().height(25);
		
		Label text = new Label("RoomMaster="+ roommaster+"/ MaxScore="+room_max_score,Resources._skin);
		table.add(text);
		table.row();
		createPane(table,Team.TEAM_1, room_max_pl/2, max_slot/2);
		table.row();
		table.row();
		table.row();
		createPane(table,Team.TEAM_2, room_max_pl/2, max_slot/2);
		table.pack();
		
		addActor(table);
	}
	
	private void createPane (Table table, Team team, int open_number, int max_number ) {
		int teamnb = team == Team.TEAM_1?0:1;
		Label l = new Label(team == Team.TEAM_1?"TEAM ALFA":"TEAM BETA", Resources._skin);
		table.add(l);
		table.row();
		Slot slot;
		for (int i=0; i< max_number;i++ ) {
			if ( i < open_number ) {
				slot = new Slot(team, Resources._skin);
				slots[teamnb*open_number+i] = slot;
			} else {
				slot = new Slot(Team.CLOSED, Resources._skin);
			}
			table.add(slot);
			if ( i == max_number/4 -1) 
				table.row();
			if ( i == max_number/2 -1) 
				table.row();
			if ( i == max_number*3/4 -1) 
				table.row();
		}
	}

	public void applyStatus(__RoomStatus roomStatus) {
		for (int i=0;i<slots.length; i++ ) {
			if ( roomStatus.userid[i] == null ) {
				slots[i].free();
			}
			else {
				slots[i].occupy(new String(roomStatus.userid[i]));
				slots[i].setReady(roomStatus.ready[i]);
			}
		}
	}
}
