package rok3.projekt.zespver3.client.objects;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.client.screens.GameStage;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Generator extends Group {
	
	private Rectangle bound;
	private Image img;
	private DummieCharacter current;
	private float counter;
	private GameStage stage;
	private Label score_label;

	public Generator(GameStage stage,Rectangle bound) {
		super();
		this.bound = bound;
		this.stage = stage;
		img = new Image(Resources._portal_texture1);
		img.setSize(bound.width, bound.height);
		img.setPosition(bound.x, bound.y);
		img.setOrigin(bound.width / 2, bound.height / 2);
		
		addActor(img);
		score_label = new Label("+"+Settings._score_from_generator,Resources._skin);
	}

	@Override
	public void act(float delta) {
		if(current != null) {
			img.setVisible(true);
			counter += delta;
			if(counter >= Settings._generator_generating_time ) {
				counter = counter - Settings._generator_generating_time;
				displayAnimation();
				if( current.isAlive()) current.score(Settings._score_from_generator);
			}
			img.setRotation(img.getRotation() - 120*delta);
		} 
		super.act(delta);
	}

	private void displayAnimation() {
		score_label.setPosition(img.getX()+10, img.getY()+20);
		stage.addActor(score_label);
		score_label.addAction(Actions.sequence(Actions.moveBy(0, 60,0.6f),Actions.removeActor()));
	}

	public void isOnBy(DummieCharacter c) {
		img.setVisible(true);
		if( current != c) {
			current = c;
			restart();
		}
	}

	private void restart() {
		counter = 0;
		img.setRotation(0);
	}

	public Rectangle getBound() {
		return bound;
	}

	public void isOff() {
		current=null;
		img.setVisible(false);
	}
}
