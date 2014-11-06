package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.client.objects.DummieCharacter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class HudDisplayer extends Group {
	
	private Label hplabel;
	private Label killslabel;
	private Label deathslabel;
	private Label timelabel;
	private Label teamscorelabel;
	private boolean started;
	private float time;
	private DummieCharacter character;
	private Label respawnlabel;

	public HudDisplayer () {
		super();
		Image bg = new Image (new Texture(Gdx.files.internal(Settings._hud_background)));
		bg.setSize(Settings._window_width, 40);
		setHeight(40);
		addActor(bg);
		
		hplabel = new Label("HP: ",Resources._skin ); 
		hplabel.setPosition(5,5);
		
		killslabel = new Label("Kills: ",Resources._skin ); 
		killslabel.setPosition(80, 5);
		
		deathslabel = new Label("Deaths:",Resources._skin ); 
		deathslabel.setPosition(160, 5);
		
		teamscorelabel = new Label("Score:",Resources._skin ); 
		teamscorelabel.setPosition(260, 5);
		
		
		timelabel = new Label("Time:",Resources._skin ); 
		timelabel.setPosition(360, 5);
		
		respawnlabel = new Label("Respawn time: ",Resources._skin ); 
		respawnlabel.setPosition(460, 5);
		
		addActor(hplabel);
		addActor(killslabel);
		addActor(deathslabel);
		addActor(teamscorelabel);
		addActor(timelabel);
		
		addActor(respawnlabel);
		
		time =0;
	}

	
	public void watch(DummieCharacter character) {
		this.character = character;
	}
	
	private void update () {
		hplabel.setText("HP: " + Integer.toString(character.getHp()));
		killslabel.setText("Kills: " + Integer.toString(character.getKills()));
		deathslabel.setText("Deaths: " + Integer.toString(character.getDeaths()));
		teamscorelabel.setText("Score: " + Short.toString(character.getTeamScore()));
		timelabel.setText("Time: " + (int)time);
		respawnlabel.setText("Respawn time: " + (int)character.getRespawnTime());
	}
	
	public void start() {
		started=true;
	}
	
	@Override
	public void act(float delta) {
		if (started ) {
			time += delta;
		}
		update();
		super.act(delta);
	}


	public void stop() {
		started=false;
	}
}
