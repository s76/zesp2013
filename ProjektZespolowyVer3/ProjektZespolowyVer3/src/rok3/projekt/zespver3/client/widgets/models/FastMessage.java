package rok3.projekt.zespver3.client.widgets.models;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class FastMessage extends WidgetGroup {
	private float time;
	private float duration;
	private boolean lockDown;
	private boolean lockUp;
	private boolean trigger;
	private Label message;

	public FastMessage(float w, float h) {
		super();
		
		Image background = new Image(new Texture(
				Gdx.files.internal(Settings._serverselection_background)));
		background.setSize(w, h);
		background.setPosition(0, 0);
		background.setColor(0.5f, 0.7f, 0.8f, 1f);
		addActor(background );
		
		message = new Label("",Resources._skin);
		message.setPosition(10, h/2+message.getHeight()/2-5);
		addActor(message);
		
		setSize(w,h);
	}
	
	@Override
	public void act(float delta) {
		if(trigger ) {
			time += delta;
		
			if (!lockUp ) {
				clearActions();
				addAction(Actions.moveBy(0, getHeight(), 0.25f, Interpolation.bounceOut));
				lockUp= true;
			}
			if (time> 0.25f+duration && !lockDown) {
				clearActions();
				addAction(Actions.moveBy(0, -getHeight(),0.25f,Interpolation.bounceOut));
				lockDown=true;
			}
			if (time > duration + 0.5f ) {
				time = 0;
				lockUp=false;
				lockDown=false;
				trigger =false;
			}
		}
		super.act(delta);
	}
	
	public boolean trigger(String message, float duration ) {
		if ( trigger ) return false;
		this.message.setText(message);
		trigger =true;
		this.duration = duration;
		return true;
	}
	
	public boolean trigger(String message ) {
		return trigger(message, 1f);
	}
}
