package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.network.packet.__UserInfo;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class UserInfoPanel extends Table {
	public UserInfoPanel (__UserInfo info ){
		super();
		
		/* can add background here */
		/*
		Image background = new Image(new Texture(Gdx.files.internal(Settings._serverselection_background) ));
		background.setSize(w,h);
		
		 */
		Label id_label = new Label("ID",Resources._skin);
		Label wins_label = new Label("Wins",Resources._skin);
		Label losses_label = new Label("Losses",Resources._skin);
		Label level_label = new Label("Level",Resources._skin);
		
		Label id = new Label(new String(info.userid),Resources._skin);
		Label wins = new Label(""+info.wins,Resources._skin);
		Label losses = new Label(""+info.losses, Resources._skin);
		Label level = new Label(""+info.level, Resources._skin);
		
		/*
		addActor(background);
		 */
		defaults().pad(3, 0, 3, 0);
		defaults().left();
		
		add(id_label);add(id).padLeft(50);row();
		add(wins_label);add(wins).padLeft(50);row();
		add(losses_label);add(losses).padLeft(50);row();
		add(level_label);add(level).padLeft(50);
		debug();
		
	}	
}
