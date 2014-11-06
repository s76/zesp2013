package rok3.projekt.zespver3.client.widgets.customized;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Slot extends TextButton {
	public static enum Team { CLOSED, TEAM_1, TEAM_2 };
	public Team team;
	
	public Slot(Team team, Skin skin) {
		this( team,skin.get(TextButtonStyle.class));
	}

	public Slot(Team team,  TextButtonStyle style ) {
		super("",style);
		
		this.team = team;
		if ( team == Team.CLOSED ) setColor(Color.CLEAR);
		else setColor(Color.LIGHT_GRAY);
	}
	
	public void occupy(String userid ) {
		setText(userid);
	}
	
	public void setReady(boolean ready) {
		setColor(ready ?Color.GREEN:Color.LIGHT_GRAY);
	}

	public void free(){
		setText("");
		setColor(Color.LIGHT_GRAY);
	}
	
}
